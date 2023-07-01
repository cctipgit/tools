//
//  CurrencyDetailViewModel.swift
//  btccurrency
//
//  Created by fd on 2022/10/30.
//

import Charts
import Foundation
import RxRelay
import RxSwift
import SocketTask

enum SegementTapedKind: Int {
    case day = 0
    case week
    case month
    case year
    case all

    func toString() -> String {
        switch self {
        case .day:
            return "24H"
        case .week:
            return "7D"
        case .month:
            return "1M"
        case .year:
            return "1Y"
        case .all:
            return "ALL"
        }
    }

    init(with string: String) {
        switch string {
        case "24H":
            self = .day
        case "7D":
            self = .week
        case "1M":
            self = .month
        case "1Y":
            self = .year
        case "ALL":
            self = .all
        default:
            self = .day
        }
    }
}

class CurrencyDetailViewModel: NSObject, ViewModel {
    struct Input {
        var inputTrigger: Observable<Void>
        var tokenRange: BehaviorRelay<String>
    }

    struct Output {
        var tokenFrom: BehaviorRelay<TokenSpecModel>
        var tokenTo: BehaviorRelay<TokenSpecModel>

        var totolChartPoints: BehaviorRelay<[ChartDataEntry]>
        var partialChartPoints: BehaviorRelay<[ChartDataEntry]>

        var max: BehaviorRelay<String>
        var min: BehaviorRelay<String>
        var avg: BehaviorRelay<String>
        var quote: BehaviorRelay<String>
        var price: BehaviorRelay<String>

        var isRequesting: BehaviorRelay<Bool>
    }

    var fromItem: GetCurrencyTokensResponse
    var toItem: GetCurrencyTokensResponse

    var tokenFrom: BehaviorRelay<TokenSpecModel>
    var tokenTo: BehaviorRelay<TokenSpecModel>

    var response: BehaviorRelay<GetQuotationResponse?>
    let min = BehaviorRelay(value: "--")
    let max = BehaviorRelay(value: "--")
    let quote = BehaviorRelay(value: "--")
    let avg = BehaviorRelay(value: "--")

    let isRequesting = BehaviorRelay(value: true)
    var provider = SocketProvider<MultiTarget>()

    var lastResponseDateRange: String = ""
    var currentDateRange: String = ""
    var isLoadCache: Bool = false
    var entries: [ChartDataEntry] = []
    var isBeginReceiveData = false
    
    var quoteChangeFormatter = NumberFormatter().then {
        $0.maximumFractionDigits = 4
        $0.minimumFractionDigits = 0
        $0.numberStyle = .percent
    }

    init(with from: GetCurrencyTokensResponse, to: GetCurrencyTokensResponse) {
        var value = readCurrencyDetail(tokenFrom: from.token, tokenTo: to.token)
        if value != nil {
            isLoadCache = true
            value?.amount = "0"
        }

        response = BehaviorRelay(value: value)
        isRequesting.accept(value == nil)

        fromItem = from
        toItem = to

        tokenFrom = BehaviorRelay(value: TokenSpecModel(model: fromItem))
        tokenTo = BehaviorRelay(value: TokenSpecModel(model: toItem))

        super.init()
    }

    func request(tokenFrom: String, tokenTo: String, dateUnit: String) {
        currentDateRange = dateUnit
        isBeginReceiveData = false
        if !isLoadCache {
            isRequesting.accept(true)
        } else {
            isLoadCache = false
        }
        

        let request = makeQuotationRequest(from: tokenFrom, to: tokenTo, dateUnit: dateUnit)
        provider.removeLast(request: request.cmd)

        provider
            .rx
            .request(.target(PriceService.quotation(request)))
            .mapModel(type: GetQuotationResponse.self)
            .do(onError: { [weak self] error in
                debugPrint(error)
                self?.isRequesting.accept(false)
            })
            .asDriver(onErrorJustReturn: nil)
            .drive(response)
            .disposed(by: rx.disposeBag)
    }

    func makeQuotationRequest(from: String, to: String, dateUnit: String) -> GetQuotationRequest {
        var getQuotationRequest = GetQuotationRequest()
        getQuotationRequest.cmd = RequestCmd.quotationRequest.rawValue
        getQuotationRequest.cid = UUID().uuidString
        getQuotationRequest.lang = "" // 默认语言
        getQuotationRequest.tokenFrom = from
        getQuotationRequest.tokenTo = to
        getQuotationRequest.dateUnit = dateUnit
        return getQuotationRequest
    }

    deinit {
        debugPrint(self)
        provider.removeAll(requestCmd: .quotationRequest)
        
        var request = GetQuotationStopRequest()
        request.cmd = RequestCmd.quotationStopRequest.rawValue
        request.cid = UUID().uuidString
        provider.request(.target(PriceService.quotationStop(request)),
                             option: .deleteWhenRequest) { _ in }
    }

    func transform(input: Input) -> Output {
        let requestSingal = Observable.combineLatest(input.inputTrigger,
                                                     input.tokenRange)

        requestSingal
            .throttle(.milliseconds(1000), latest: false, scheduler: MainScheduler())
            .subscribe(onNext: { [weak self] tuple in
                guard let self
                else { return }
                let (_, range) = tuple
                self.request(tokenFrom: self.fromItem.token,
                             tokenTo: self.toItem.token,
                             dateUnit: range)
            })
            .disposed(by: rx.disposeBag)

        let chart = BehaviorRelay<[ChartDataEntry]>(value: [])
        let dynamicChart = BehaviorRelay<[ChartDataEntry]>(value: [])

        response
            .unwrap()
            .ignoreWhen({ [weak self] response in
                guard let self
                else { return true }

                if self.isLoadCache {
                    return false
                }

                return response.tokenTo != self.toItem.token
                    || response.tokenFrom != self.fromItem.token
                    || response.dateUnit != self.currentDateRange
            })
//            .throttle(.milliseconds(1000), scheduler: MainScheduler.instance)
            .subscribe(onNext: { [weak self] response in
                guard let self
                else { return }

                if response.amount == "-1" {
                    self.isRequesting.accept(false)

                    if self.isBeginReceiveData == false {
                        self.isBeginReceiveData = true
                    }
                }

                if !self.isLoadCache {
                    if !self.isBeginReceiveData {
                        return
                    }

                    let isSameDateRange = response.dateUnit == self.lastResponseDateRange
                    self.lastResponseDateRange = response.dateUnit

                    if !isSameDateRange
                        && !self.entries.isEmpty
                        && response.data.count > 10 {
                        self.entries.removeAll()
                    }
                }

                let datas = response.data.sorted(by: \.priceTime)

                var entries: [ChartDataEntry] = []
                for item in datas {
                    if item.priceFrom.isEmpty
                        || item.priceTo.isEmpty {
                        continue
                    }
                    if let convertValue = self.makePriceConvert(fromPrice: item.priceFrom,
                                                                toPrice: item.priceTo).double() {
                        let point = ChartDataEntry(x: Double(item.priceTime), y: convertValue)
                        entries.append(point)
                        debugPrint(item.priceTime)
                    }
                }

                if self.entries.isEmpty {
                    self.entries.append(contentsOf: entries)
                } else {
                    for item in entries {
                        var i = self.entries.count - 1
                        while i >= 0 {
                            if self.entries[i].x == item.x {
                                self.entries[i] = item
                                debugPrint("replace:\(i)" + "\(item)")
                                break
                            } else if item.x > self.entries[i].x {
                                self.entries.insert(item, at: i)
                                self.entries.removeFirst()

                                debugPrint("insert:\(i)" + "\(item)")
                                break
                            } else if item.x < self.entries[i].x {
                                if i == 0 {
                                    self.entries.insert(item, at: i)
                                    break
                                }

                                i -= 1

                                debugPrint("index:\(i)")
                                continue
                            }
                        }
                    }
                }

                var low = 0.0
                var high = 0.0

                if let tuple = self.entries.minAndMax(by: { $0.y < $1.y }) {
                    low = tuple.0.y
                    high = tuple.1.y
                }

                
                var average = 0.0
                
                for (index,value) in self.entries.enumerated() {
                    let doubleIndex = index.double
                    average = average * doubleIndex / (doubleIndex + 1) + value.y / (doubleIndex + 1)
                }
                
                
                var change = 0.double

                if self.entries.count > 0 {
                    change = (self.entries.last!.y - self.entries.first!.y) / self.entries.first!.y
                }

                self.makeIndicators(high: high,
                                    low: low,
                                    change: change,
                                    avg: average)

                if self.entries.count > entries.count {
                    dynamicChart.accept(entries)
                } else {
                    chart.accept(self.entries)
                    if SegementTapedKind.day.toString() == response.dateUnit {
                        saveCurrencyDetail(tokenFrom: self.fromItem.token,
                                           tokenTo: self.toItem.token,
                                           response: response)
                    }
                }
            })
            .disposed(by: rx.disposeBag)

        let price = BehaviorRelay(value: "--")
        response
//            .throttle(.milliseconds(1000), scheduler: MainScheduler.instance)
            .subscribe(onNext: { [weak self] response in
                guard let self,
                      let response
                else { return }

                let priceFrom = response.priceFrom
                let priceTo = response.priceTo

                if priceFrom.isEmpty || priceTo.isEmpty {
                    return
                }

                let value = self.makePriceConvert(fromPrice: priceFrom,
                                                  toPrice: priceTo)
                let text = CurrencyFormatter.format(with: value,
                                                    type: self.fromItem.currencyType)
                price.accept(text)
            })
            .disposed(by: rx.disposeBag)

        return Output(tokenFrom: tokenFrom,
                      tokenTo: tokenTo,
                      totolChartPoints: chart,
                      partialChartPoints: dynamicChart,
                      max: max,
                      min: min,
                      avg: avg,
                      quote: quote,
                      price: price,
                      isRequesting: isRequesting)
    }

    func makeIndicators(high: Double, low: Double, change: Double, avg: Double) {
        let minString = CurrencyFormatter.format(with: low.string, type: fromItem.currencyType)
        if !minString.isEmpty {
            self.min.accept(minString)
        }

        let maxString = CurrencyFormatter.format(with: high.string, type: fromItem.currencyType)
        if !maxString.isEmpty {
            self.max.accept(maxString)
        }

        let avgString = CurrencyFormatter.format(with: avg.string, type: fromItem.currencyType)
        if !avgString.isEmpty {
            self.avg.accept(avgString)
        }

        let quoteString = quoteChangeFormatter.string(for: change)
        if let quoteString {
            quote.accept(quoteString)
        }
    }

    func makePriceConvert(fromPrice: String, toPrice: String) -> String {
        let amount = CurrencyType.currency == fromItem.currencyType ? 100 : 1
        if let from = Decimal(string: fromPrice),
           let to = Decimal(string: toPrice) {
            let base = Decimal(amount)
            if to.isZero {
                return "0"
            }

            return (from / to * base).string
        } else {
            guard let from = fromPrice.double(),
                  let to = toPrice.double(),
                  to != 0
            else {
                return "0"
            }

            return String(from / to * amount.double)
        }
    }

    func changeFromTo() {
        (fromItem, toItem) = (toItem, fromItem)

        tokenFrom.accept(TokenSpecModel(model: fromItem))
        tokenTo.accept(TokenSpecModel(model: toItem))
    }

    func reloadFromTo() {
        tokenFrom.accept(TokenSpecModel(model: fromItem))
        tokenTo.accept(TokenSpecModel(model: toItem))
    }
}
