//
//  Currency.swift
//  xCurrency
//
//  Created by fd on 2022/10/18.
//

import Foundation
import RxRelay
import RxSwift
import SocketTask

class CurrencyRate: NSObject {
    static var shared = CurrencyRate()
    var rates: BehaviorRelay<Dictionary<String, Rate>>
    var requestTokens: String = ""

    var provider = SocketProvider<PriceService>()
    var disposable: Disposable?
    var rateSubject: BehaviorRelay<GetSymbolsRateResponse?>

    override private init() {
        rates = BehaviorRelay(value: [:])
        rateSubject = BehaviorRelay(value: nil)
        super.init()
        loadCache()
        bindRate()

        NotificationCenter.default
            .rx
            .notification(SocketConstant.socketDidOpendNotification)
            .skip(1)
            .subscribe(onNext: { [weak self] _ in
                self?.reloadRequest()
            })
            .disposed(by: rx.disposeBag)
    }

    func bindRate() {
        rateSubject
            .unwrap()
            .buffer(timeSpan: .seconds(1), count: 10, scheduler: MainScheduler.asyncInstance)
            .subscribe(onNext: { [weak self] responses in
                guard let self
                else { return }

                var totalRate = self.rates.value
                var dictionary = Dictionary<String, Rate>()

                for response in responses {
                    let map = response.data

                    for value in map.values {
                        if value.price.isEmpty || value.price == "0" {
                            continue
                        }
                        let price = value.price
                        let token = value.token
                        dictionary[token] = Rate(price: price, token: token)
                        debugPrint("currency rate item: \(value)")
                    }

                    if dictionary.isEmpty {
                        return
                    }

                    totalRate.merge(dictionary) { _, new in
                        new
                    }
                }

                self.rates.accept(totalRate)
                AppDatabase.shared.saveRates(list: dictionary.map { $0.value })
            })
            .disposed(by: rx.disposeBag)
    }

    func loadCache() {
        let dictionary = AppDatabase.shared.readRates()
        if dictionary.isEmpty {
            return
        }
        rates.accept(dictionary)
    }

    func request(with tokens: String) {
        requestTokens = tokens

        let request = makeRequest(with: tokens)

        provider.removeLast(request: request.cmd)

        disposable?.dispose()
        disposable = provider
            .rx
            .request(.symbolsRate(request),
                     option: .keepOption)
            .mapModel(type: GetSymbolsRateResponse.self)
            .observe(on: MainScheduler.asyncInstance)
            .retry()
            .asDriver(onErrorJustReturn: nil)
            .drive(rateSubject)
    }

    func makeRequest(with tokens: String) -> GetSymbolsRateRequest {
        var request = GetSymbolsRateRequest()
        request.cmd = RequestCmd.symbolRateRequest.rawValue
        request.cid = UUID().uuidString
        request.lang = "" // 默认语言
        request.tokens = tokens
        return request
    }

    func reloadRequest() {
        request(with: requestTokens)
    }
}
