//
//  TokensList.swift
//  btccurrency
//
//  Created by fd on 2022/11/6.
//
import DataCache
import Foundation
import RxRelay
import RxSwift
import SocketTask

class CurrencyTokensList: NSObject {
    static let shared = CurrencyTokensList()
    var datasource: BehaviorRelay<[SwitchCurrencyGroup]>
    var frequentTokens: [String] = []
    var tokenListSubject: BehaviorRelay<GetCurrencyTokensListResponse?>
    var provider = SocketProvider<CurrencyService>()

    override private init() {
        datasource = BehaviorRelay<[SwitchCurrencyGroup]>(value: [])
        tokenListSubject = BehaviorRelay<GetCurrencyTokensListResponse?>(value: nil)
        super.init()

        loadCache()
        addNotification()
        
        requestTokensList()
    }

    func loadCache() {
        if let tokens = DataCache.instance.readArray(forKey: "frequentTokens") as? [String] {
            frequentTokens = tokens
        }

        let data = GetCurrencyTokensResponse.readCurrencyList()
        datasource.accept(data)
        reloadFrequentData(group: loadFrequent())
    }

    func insertLocalItem() {
        var array = datasource.value

        if let localItem = LocationManager.shared.getCurrentLocationResponse() {
            let localGroup = SwitchCurrencyGroup(header: "Local Currency".localized(),
                                                 items: [SwitchCurrencyCellViewModel(with: localItem)],
                                                 sectionType: .local)
            localGroup.items.first?.isShowLocation = true

            if let index = array.firstIndex(where: { $0.sectionType == .local }) {
                array[index] = localGroup
            } else {
                array.insert(localGroup, at: 0)
            }
        }

        datasource.accept(array)
    }

    func removeLocationItem() {
        var array = datasource.value
        if let index = array.firstIndex(where: { $0.sectionType == .local }) {
            array.remove(at: index)
            datasource.accept(array)
        }
    }

    func addNotification() {
        tokenListSubject
            .unwrap()
            .subscribe(onNext: { listResponse in
                var newData = listResponse
                newData.data.removeAll()

                for item in listResponse.data {
                    var newItem = item

                    newItem.data.removeAll()
                    let list = item.data.filter({ !$0.price.isEmpty && $0.price.double().or(0) > 0 })
                    if !list.isEmpty {
                        newItem.data.append(contentsOf: list)
                        newData.data.append(newItem)
                    }
                }

                let array = self.mapViewModel(with: newData)
                self.datasource.accept(array)
                self.reloadFrequentData(group: self.loadFrequent())

                AppDatabase.shared.saveTokensList(with: newData.data)
            })
            .disposed(by: rx.disposeBag)

        Observable.combineLatest(
            AppSetting.shared.localCurrencyMark
                .distinctUntilChanged(),
            LocationManager.shared.currentZoneCode
                .skip(1)
                .distinctUntilChanged()
        )
        .subscribe(onNext: { [weak self] mark, _ in
            if mark {
                self?.insertLocalItem()
            } else {
                self?.removeLocationItem()
            }
        })
        .disposed(by: rx.disposeBag)
    }

    func mapViewModel(with response: GetCurrencyTokensListResponse) -> [SwitchCurrencyGroup] {
        let datas = response.data.sorted(by: \.fchat)

        var array = [SwitchCurrencyGroup]()

        for data in datas {
            let group = SwitchCurrencyGroup(header: data.fchat,
                                            items: data.data.map({ SwitchCurrencyCellViewModel(with: $0) }),
                                            sectionType: .fixed)
            array.append(group)
        }
        return array
    }

    func loadFrequent() -> SwitchCurrencyGroup {
        let frequent = AppDatabase.shared.readFrequent(with: frequentTokens)
        let group = SwitchCurrencyGroup(header: "Frequently Used".localized(),
                                        items: frequent.map { SwitchCurrencyCellViewModel(with: $0) },
                                        sectionType: .frequent)
        return group
    }

    func reloadFrequentData(group: SwitchCurrencyGroup) {
        func doNothing() {
            var array = datasource.value
            if array.isEmpty {
                return
            }
            let prefix = array[0 ..< 2]
            if let frequentIndex = prefix.firstIndex(where: { $0.sectionType == .frequent }) {
                array[frequentIndex] = group
            } else {
                var index = 0
                if prefix.first?.sectionType == .local {
                    index = 1
                }
                array.insert(group, at: index)
            }
            datasource.accept(array)
        }
    }

    func insertFrequent(item: GetCurrencyTokensResponse) {
        if frequentTokens.contains(item.token) {
            return
        }
        frequentTokens.insert(item.token, at: 0)
        if frequentTokens.count > 12 {
            frequentTokens.removeLast()
        }

        DataCache.instance.write(array: frequentTokens, forKey: "frequentTokens")
        reloadFrequentData(group: loadFrequent())
    }

    func insertFrequent(viewModel: SwitchCurrencyCellViewModel) {
        insertFrequent(item: viewModel.response)
    }

    func insertHomeList(tokens: [String]) {
        if frequentTokens.isEmpty {
            frequentTokens = tokens
            reloadFrequentData(group: loadFrequent())
        }
    }

    func requestTokensList() {
        let request = makeRequest()

        provider
            .rx
            .request(.currencyTokensList(request),
                     option: .default)
            .mapModel(type: GetCurrencyTokensListResponse.self)
            .asDriver(onErrorJustReturn: nil)
            .drive(tokenListSubject)
            .disposed(by: rx.disposeBag)
    }

    func makeRequest() -> GetCurrencyTokensListRequest {
        var req = GetCurrencyTokensListRequest()
        req.cmd = RequestCmd.currencyTokensListRequest.rawValue
        req.cid = UUID().uuidString
        req.lang = "" // 默认语言
        return req
    }
}
