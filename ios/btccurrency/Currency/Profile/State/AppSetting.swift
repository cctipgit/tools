//
//  AppSetting.swift
//  btccurrency
//
//  Created by fd on 2022/11/3.
//

import Foundation
import RxCocoa
import RxRelay
import RxSwift

class AppSetting {
    static let shared = AppSetting()

    var localCurrencyMark: BehaviorRelay<Bool>
    var currencySymbol: BehaviorRelay<Bool>
    
    var currentCurrncy: BehaviorRelay<String>
    var defaultCurrencyValue: BehaviorRelay<String>
    
    let tokensList: BehaviorRelay<[String]>

    var legalDecimalDigit: BehaviorRelay<Int>
    var btcDecimalDigit: BehaviorRelay<Int>

    var troyWeight: BehaviorRelay<String>
    var quoteColor: BehaviorRelay<Bool>
    var keyboardSound: BehaviorRelay<Bool>

    var disposeBag = DisposeBag()
    
    var userDefaults = UserDefaults.standard
    private init() {
        
        userDefaults.register(defaults: [
            "localCurrencyMark": true,
            "currencySymbol": true,
            "keyboardSound": true,
            "defaultCurrencyPlaceHolderValue": "100",
            "currencyDecimalCount": 4,
            "digitDecimalCount": 6,
            "troyWeight": "",
            "quoteColor": true,
            "currentCurrncy": "",
            "tokensList": ["USD", "BTC", "USDT", "DOGE", "ETH", "FJD"],
        ])

        let showLocal = userDefaults.bool(forKey: "localCurrencyMark")
        localCurrencyMark = BehaviorRelay(value: showLocal)

        let showSymbol = userDefaults.bool(forKey: "currencySymbol")
        currencySymbol = BehaviorRelay(value: showSymbol)
        
        let current = userDefaults.string(forKey: "currentCurrncy")
        currentCurrncy = BehaviorRelay(value: current.or(""))

        let playSound = userDefaults.bool(forKey: "keyboardSound")
        keyboardSound = BehaviorRelay(value: playSound)

        let defaultValue = userDefaults.string(forKey: "defaultCurrencyPlaceHolderValue")
        defaultCurrencyValue = BehaviorRelay(value: defaultValue.or("100"))
        
        let defTokensList = userDefaults.stringArray(forKey: "tokensList")
        tokensList = BehaviorRelay(value: defTokensList!)

        let legalDigit = userDefaults.integer(forKey: "currencyDecimalCount")
        legalDecimalDigit = BehaviorRelay(value: legalDigit)

        let btcDigit = userDefaults.integer(forKey: "digitDecimalCount")
        btcDecimalDigit = BehaviorRelay(value: btcDigit)

        let troy = userDefaults.string(forKey: "troyWeight")
        troyWeight = BehaviorRelay(value: troy.or("g"))

        let isRedGrow = userDefaults.bool(forKey: "quoteColor")
        quoteColor = BehaviorRelay(value: isRedGrow)

        DispatchQueue.main.async {
            self.subscribe()
        }
    }

    func subscribe() {
        localCurrencyMark.throttle(.seconds(1), scheduler: MainScheduler())
            .subscribe(onNext: { value in
                self.userDefaults.set(value, forKey: "localCurrencyMark")
                self.userDefaults.synchronize()
            })
            .disposed(by: disposeBag)

        currencySymbol.throttle(.seconds(1), scheduler: MainScheduler())
            .subscribe(onNext: { value in
                self.userDefaults.set(value, forKey: "currencySymbol")
                self.userDefaults.synchronize()
            })
            .disposed(by: disposeBag)

        keyboardSound.throttle(.seconds(1), scheduler: MainScheduler())
            .subscribe(onNext: { value in
                self.userDefaults.set(value, forKey: "keyboardSound")
                self.userDefaults.synchronize()
            })
            .disposed(by: disposeBag)

        defaultCurrencyValue
            .distinctUntilChanged()
            .throttle(.seconds(1), scheduler: MainScheduler())
            .subscribe(onNext: { value in
                self.userDefaults.set(value, forKey: "defaultCurrencyPlaceHolderValue")
                self.userDefaults.synchronize()
            })
            .disposed(by: disposeBag)
        
        currentCurrncy
            .distinctUntilChanged()
            .throttle(.seconds(1), scheduler: MainScheduler())
            .subscribe(onNext: { value in
                self.userDefaults.set(value, forKey: "currentCurrncy")
                self.userDefaults.synchronize()
            })
            .disposed(by: disposeBag)
        
        tokensList
            .distinctUntilChanged()
            .throttle(.seconds(1), scheduler: MainScheduler())
            .subscribe(onNext: { value in
                self.userDefaults.setValue(value, forKey: "tokensList")
                self.userDefaults.synchronize()
            })
            .disposed(by: disposeBag)

        legalDecimalDigit
            .distinctUntilChanged()
            .throttle(.seconds(1), scheduler: MainScheduler())
            .subscribe(onNext: { value in
                self.userDefaults.set(value, forKey: "currencyDecimalCount")
                CurrencyFormatter.shared.coinFormatter.maximumFractionDigits = value
                CurrencyFormatter.shared.coinFormatter.minimumFractionDigits = value
                self.userDefaults.synchronize()
            })
            .disposed(by: disposeBag)

        btcDecimalDigit
            .distinctUntilChanged()
            .throttle(.seconds(1), scheduler: MainScheduler())
            .subscribe(onNext: { value in
                self.userDefaults.set(value, forKey: "digitDecimalCount")
                CurrencyFormatter.shared.digitFormatter.maximumFractionDigits = value
                CurrencyFormatter.shared.digitFormatter.minimumFractionDigits = value
                self.userDefaults.synchronize()
            })
            .disposed(by: disposeBag)

        troyWeight.throttle(.seconds(1), scheduler: MainScheduler())
            .subscribe(onNext: { value in
                self.userDefaults.set(value, forKey: "troyWeight")
                self.userDefaults.synchronize()
            })
            .disposed(by: disposeBag)

        quoteColor.throttle(.seconds(1), scheduler: MainScheduler())
            .subscribe(onNext: { value in
                self.userDefaults.set(value, forKey: "quoteColor")
                self.userDefaults.synchronize()
            })
            .disposed(by: disposeBag)
    }
}
