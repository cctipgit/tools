//
//  HomeViewModel.swift
//  xCurrency
//
//  Created by fd on 2022/10/18.
//

import GRDB
import RxRelay
import RxSwift
import SocketTask

class HomeViewModel: NSObject, ViewModel {
    var currentSelectHeight: CGFloat = 74.0
    var currentCellHeight: CGFloat = 74.0

    var bindCellViewModelsBag = DisposeBag()

    var datasource: BehaviorRelay<[HomeCellSection]>
    var itemSelected: BehaviorRelay<IndexPath?>

    var showListTokens: [String] = []

    var tableViewHeight: CGFloat = 0
    var inputStruct: Input!

    var isSwitchSelectCurrency: BehaviorRelay<Bool>
    var isLeaveHome: BehaviorRelay<Bool>

    var provider = SocketProvider<MultiTarget>()
    var currentResponseSubject: BehaviorRelay<GetCurrentCurrencyTokensResponse?>
    var isExchangeLocationItem = false {
        didSet {
            UserDefaults.standard.set(isExchangeLocationItem, forKey: "isExchangeLocationItem")
        }
    }

    struct Input {
        var tableViewHeight: BehaviorRelay<CGFloat>
        var textFieldValueChanged: BehaviorRelay<String?>
        var itemSelected: Observable<IndexPath>
        var viewWillAppear: Observable<Bool>
        var viewDidDisappear: Observable<Bool>
    }

    struct Output {
        var datasource: Observable<[HomeCellSection]>
    }

    override init() {
        datasource = BehaviorRelay(value: [])
        itemSelected = BehaviorRelay(value: nil)
        isSwitchSelectCurrency = BehaviorRelay(value: false)
        isLeaveHome = BehaviorRelay(value: false)

        currentResponseSubject = BehaviorRelay<GetCurrentCurrencyTokensResponse?>(value: nil)
        
        isExchangeLocationItem = UserDefaults.standard.bool(forKey: "isExchangeLocationItem")
        super.init()
    }

    func transform(input: Input) -> Output {
        inputStruct = input
        receiveNotification()

        LocationManager.shared.startLocation()

        input.itemSelected.bind(to: itemSelected)
            .disposed(by: rx.disposeBag)

        input.viewWillAppear
            .subscribe(onNext: { _ in
                self.isLeaveHome.accept(false)
            })
            .disposed(by: rx.disposeBag)

        input.viewDidDisappear
            .subscribe(onNext: { _ in
                self.isLeaveHome.accept(true)
            })
            .disposed(by: rx.disposeBag)

        currentResponseSubject
            .unwrap()
            .do(onNext: { [weak self] _ in
                self?.loadCurrencyRate()
            })
            .subscribe(onNext: { [weak self] response in
                guard let self
                else { return }

                self.datasource.accept(self.makeSections(with: response.data))

                let currentTokens = response.data.map { $0.token }
                if self.showListTokens == currentTokens {
                    return
                }

                self.showListTokens = response.data.map { $0.token }
                self.saveHomeListTokens()
            })
            .disposed(by: rx.disposeBag)

        input.tableViewHeight
            .filter({ $0 > 0 })
            .take(1)
            .do(afterNext: { [weak self] _ in
                self?.loadCache()
            })
            .subscribe(onNext: { [weak self] height in
                guard let self
                else { return }
                self.tableViewHeight = height
            })
            .disposed(by: rx.disposeBag)

        Observable.merge(AppSetting.shared.btcDecimalDigit.mapToVoid(),
                         AppSetting.shared.legalDecimalDigit.mapToVoid())
            .subscribe(onNext: {
                input.textFieldValueChanged.accept(input.textFieldValueChanged.value)
            })
            .disposed(by: rx.disposeBag)

        input.itemSelected
            .distinctUntilChanged({
                $0.row == $1.row
            }).throttle(.seconds(1), scheduler: MainScheduler())
            .compactMap({ $0 })
            .subscribe(onNext: { [weak self] indexpath in
                guard let self
                else { return }
                if let items = self.datasource.value.first?.items {
                    if indexpath.row < items.count {
                        self.saveSelectToken(items[indexpath.row].token, index: indexpath.row)
                    }
                }
            })
            .disposed(by: rx.disposeBag)

        return Output(datasource: datasource.asObservable())
    }

    func makeSections(with items: [GetCurrencyTokensResponse]) -> [HomeCellSection] {
        let section = mapCellViewModel(items: items)
        bindCellViewModelsBag = DisposeBag()

        if let items = section.first?.items {
            sectionMakeBinding(input: inputStruct, items: items)
        }
        return section
    }

    func mapCellViewModel(items: [GetCurrencyTokensResponse]) -> [HomeCellSection] {
        var cellViewModels = items.map { HomeCellViewModel(with: $0) }
        let selectIndex = readSelectIndex()

        for (index, cellViewModel) in cellViewModels.enumerated() {
            cellViewModel.cellHeight = currentCellHeight
            cellViewModel.selectCellHeight = currentSelectHeight

            func doNothing() {
                cellViewModel.selectedSubject.accept(cellViewModels[selectIndex].token)
                if selectIndex == index {
                    itemSelected.accept(IndexPath(row: selectIndex, section: 0))
                }
            }
        }
        if let locationItem = LocationManager.shared.getCurrentLocationResponse(),
           let index = cellViewModels.firstIndex(where: { $0.model.token == locationItem.token }) {
            if !isExchangeLocationItem {
                cellViewModels[index].location.accept(true)
                cellViewModels.swapAt(0, index)
            }
        }

        return [HomeCellSection(items: cellViewModels, header: "")]
    }

    func sectionMakeBinding(input: Input, items: [HomeCellViewModel]) {
        for (_, cellViewModel) in items.enumerated() {
            isSwitchSelectCurrency
                .distinctUntilChanged()
                .bind(to: cellViewModel.isSwitchSelectSubject)
                .disposed(by: bindCellViewModelsBag)

            let rate = CurrencyRate.shared.rates
                .ignoreWhen({ [weak self] _ in
                    guard let self
                    else { return true }
                    return self.isLeaveHome.value
                })
                .throttle(.milliseconds(1000), scheduler: MainScheduler.asyncInstance)
                .map { _ in 0 }
            let selected = input.itemSelected.map { _ in 1 }

            Observable.merge(rate,
                             selected)
                .subscribe(onNext: { [weak self] res in
                    guard let self
                    else { return }

                    if res == 1 {
                        cellViewModel.placeholderRaiseOrDown.accept(.none)
                    }

                    let dict = CurrencyRate.shared.rates.value
                    let selectIndex = self.itemSelected.value.or(IndexPath(row: 0, section: 0)).row
                    let token = items[selectIndex].token
                    if !token.isEmpty {
                        var rate = dict[cellViewModel.token]
                        rate?.currentSelectPrice = dict[token]?.price
                        rate?.price = dict[cellViewModel.token]!.price
                        cellViewModel.currencyRatePrice.accept(rate)
                    }
                })
                .disposed(by: bindCellViewModelsBag)

            input.textFieldValueChanged
                .unwrap()
                .distinctUntilChanged()
                .bind(to: cellViewModel.selectedInputValue)
                .disposed(by: bindCellViewModelsBag)
        }
    }

    func receiveNotification() {
        let location = LocationManager.shared.currentZoneCode
            .compactMap({ $0 })
            .distinctUntilChanged()
        let locationMark = AppSetting.shared.localCurrencyMark
            .distinctUntilChanged()

        let locationEnableChangeCount:(Int,Bool) = (0,false)
        locationMark.scan(into: locationEnableChangeCount) { count, enable in
            count.0 += 1
            count.1 = enable
        }
        .subscribe(onNext: { (count,enable) in
            if count > 1 && enable {
                self.isExchangeLocationItem = false
            }
        })
        .disposed(by: rx.disposeBag)
        
        Observable.combineLatest(location, locationMark)
            .subscribe(onNext: { [weak self] _, enabled in
                guard let self
                else { return }
                if enabled {
                    if self.isExchangeLocationItem {
                        return
                    }
                    if var items = self.datasource.value.first?.items,
                       let localItem = LocationManager.shared.getCurrentLocationResponse() {
                        if let index = items.firstIndex(where: {
                            $0.model.token == localItem.token
                        }) {
                            if index != 0 {
                                let insertItem = items[index]
                                items.remove(at: index)
                                items.insert(insertItem, at: 0)

                                self.showListTokens.remove(at: index)
                                self.showListTokens.insert(insertItem.token, at: 0)
                                self.saveHomeListTokens()
                            }

                            let section = self.datasource.value
                            section.first?.items.removeAll()
                            section.first?.items.append(contentsOf: items)
                            self.datasource.accept(section)

                            items[0].location.accept(true)
                        } else {
                            self.insertLocalCurrency(localItem)
                        }
                    }
                } else {
                    self.removeLocalCurrency()
                }
            })
            .disposed(by: rx.disposeBag)

        NotificationCenter.default.rx
            .notification(resetHomeListNotification, object: nil)
            .subscribe(onNext: { [weak self] _ in
                self?.clearCurrent()
                self?.request()
            })
            .disposed(by: rx.disposeBag)
    }

    func insertLocalCurrency(_ item: GetCurrencyTokensResponse?) {
        guard let item
        else { return }

        showListTokens.insert(item.token, at: 0)
        showListTokens.removeLast()
        saveHomeListTokens()

        if inputStruct.textFieldValueChanged.value.or("").count > 0 {
            return
        }

        if let items = GetCurrencyTokensResponse.readHomeList(tokens: showListTokens) {
            let sections = makeSections(with: items)
            datasource.accept(sections)
        }
    }

    func removeLocalCurrency() {
        if let section = datasource.value.first {
            for item in section.items {
                if item.location.value {
                    item.location.accept(false)
                }
            }
        }
    }

    func loadCache() {
        if let tokens = readHomeListTokens() {
            showListTokens = tokens
        } else {
            let list = ["USD", "CNY", "EUR", "BTC", "USDT", "DOGE", "ETH"]
            showListTokens.append(contentsOf: list)
            request()
        }
        if let list = GetCurrencyTokensResponse.readHomeList(tokens: showListTokens) {
            if list.count < showListTokens.count {
                request()
                return
            }
            var response = GetCurrentCurrencyTokensResponse()
            response.data = list
            currentResponseSubject.accept(response)
        } else {
            request()
        }
    }

    func saveHomeListTokens() {
        AppSetting.shared.tokensList.accept(showListTokens)
        let sharedDefault = UserDefaults.standard
        sharedDefault.setValue(showListTokens, forKey: showListTokensCacheKey)
        sharedDefault.synchronize()
    }

    func readHomeListTokens() -> [String]? {
        return UserDefaults.standard.stringArray(forKey: showListTokensCacheKey)
    }

    func saveSelectToken(_ token: String, index: Int) {
        let sharedDefault = UserDefaults.standard
        sharedDefault.setValue(token, forKey: selectTokenCacheKey)
        sharedDefault.setValue(index, forKey: selectIndexPathCacheKey)
        sharedDefault.synchronize()
    }

    func readSelectIndex() -> Int {
        let sharedDefault = UserDefaults.standard
        return sharedDefault.integer(forKey: selectIndexPathCacheKey)
    }

    func changeToken(item: GetCurrencyTokensResponse, index: Int) {
        if AppSetting.shared.localCurrencyMark.value {
            isExchangeLocationItem = true
        }
        showListTokens.append(item.token)
        saveHomeListTokens()
        //        // add new item
        //        var originItems = self.datasource.value.first?.items ?? [HomeCellViewModel]()
        //        originItems.append(HomeCellViewModel(with: item))
        //        let section = self.datasource.value
        //        section.first?.items.removeAll()
        //        section.first?.items.append(contentsOf: originItems)
        //        self.datasource.value(section)
        let sharedDefault = UserDefaults.standard
        if let tokens = sharedDefault.stringArray(forKey: showListTokensCacheKey) {
            if let list = GetCurrencyTokensResponse.readHomeList(tokens: tokens) {
                var response = GetCurrentCurrencyTokensResponse()
                response.data = list
                self.currentResponseSubject.accept(response)
            }
        }
        
        func doNothing() {
            if AppSetting.shared.localCurrencyMark.value {
                if index == 0 && LocationManager.shared.getCurrentLocationCurrencyToken() == showListTokens.first {
                    isExchangeLocationItem = true
                }
                if index == 0 && LocationManager.shared.getCurrentLocationCurrencyToken() == item.token {
                    isExchangeLocationItem = false
                }
                isExchangeLocationItem = true
            }
            showListTokens[index] = item.token
            saveHomeListTokens()
            
            if let items = datasource.value.first?.items {
                items[index].replaceItem(model: item)
                if index == 0 {
                    if isExchangeLocationItem {
                        items[index].location.accept(false)
                    } else {
                        items[index].location.accept(true)
                    }
                }
                loadCurrencyRate()
            }
        }
    }

    func reload(item: GetCurrencyTokensResponse, index: Int) {
        showListTokens[index] = item.token
        saveHomeListTokens()
        if let items = GetCurrencyTokensResponse.readHomeList(tokens: showListTokens) {
            let sections = makeSections(with: items)
            datasource.accept(sections)
        }
    }

    func loadCurrencyRate() {
        let tokens = showListTokens.joined(separator: ",")
        CurrencyRate.shared.request(with: tokens)
    }

    func getSymbolsRateRequest() {
        if showListTokens.count > 0 {
            CurrencyRate.shared.request(with: showListTokens.joined(separator: ","))
        }
    }

    func clearCurrent() {
        self.inputStruct.textFieldValueChanged.accept("")
    }
    
    func request() {
        let current = makeCurrentRequest()
        provider.removeLast(request: current.cmd)

        provider.rx
            .request(.target(CurrencyService.currentCurrency(current)))
            .observe(on: MainScheduler.asyncInstance)
            .mapModel(type: GetCurrentCurrencyTokensResponse.self)
            .take(1)
            .asDriver(onErrorJustReturn: nil)
            .drive(currentResponseSubject)
            .disposed(by: bindCellViewModelsBag)
    }

    func makeCurrentRequest() -> GetCurrentCurrencyTokensRequest {
        var current = GetCurrentCurrencyTokensRequest()
        current.cmd = RequestCmd.currentCurrencyTokensRequest.rawValue
        current.cid = UUID().uuidString
        current.lang = ""
        current.count = Int32(showListTokens.count)
        current.location = (LocationManager.shared.currentZoneCode.value.or("")).uppercased()
        return current
    }

    func getDefualtCompareModel() -> GetCurrencyTokensResponse? {
        if let model = readDefaultRateCompare() {
            return model
        } else {
            return datasource.value.first?.items.first?.model
        }
    }
}
