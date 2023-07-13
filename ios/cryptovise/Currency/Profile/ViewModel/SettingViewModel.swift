//
//  SettingViewModel.swift
//  cryptovise
//
//  Created by fd on 2022/11/2.
//

import Foundation
import NSObject_Rx
import RxCocoa
import RxRelay
import RxSwift
protocol ViewModel {
    associatedtype Input
    associatedtype Output
    func transform(input: Input) -> Output
}

class SettingViewModel: NSObject, ViewModel {
    struct Input {
        let trigger: Observable<Void>
        let selection: Driver<SettingSectionItem>
    }

    struct Output {
        let items: BehaviorRelay<[SettingSection]>
        let selectedEvent: Driver<SettingSectionItem>
    }

    override init() {
        super.init()
    }

    func transform(input: Input) -> Output {
        let elements = BehaviorRelay<[SettingSection]>(value: [])
        let selectedEvent = input.selection

        input.trigger
            .map { _ in

                
                var locationEnabled = false
                if LocationManager.shared.locationEnabled {
                     locationEnabled = AppSetting.shared.localCurrencyMark.value
                }
                let localItem = SettingSwitchViewModel(title: "Local Currency".localized(), isOn: locationEnabled)
                localItem.isOn.skip(1)
                    .bind(to: AppSetting.shared.localCurrencyMark)
                    .disposed(by: self.rx.disposeBag)

                let showSymbol = AppSetting.shared.currencySymbol.value
                let symbolItem = SettingSwitchViewModel(title: "Currency symbol".localized(), isOn: showSymbol)
                symbolItem.isOn.skip(1)
                    .bind(to: AppSetting.shared.currencySymbol)
                    .disposed(by: self.rx.disposeBag)

                let defaultValue = AppSetting.shared.defaultCurrencyValue.value
                let defaultItem = SettingArrowViewModel(title: "Default Value".localized(), value: defaultValue)
                defaultItem.value.skip(1)
                    .bind(to: AppSetting.shared.defaultCurrencyValue)
                    .disposed(by: self.rx.disposeBag)

                let digitItem = SettingArrowViewModel(title: "Setting point".localized(), value: "")

                let more = SettingArrowViewModel(title: "More Settings".localized(), value: "")

                let isRedRise = AppSetting.shared.quoteColor.value ? "red up, green down".localized() : "green up, red down".localized()
                let quote = SettingArrowViewModel(title: "Price color".localized(), value: isRedRise)
                
                return [
                    SettingSection.setting(titile: "0", items: [
                        .defaultCurrencyValue(defaultItem),
                        .decimalDigit(digitItem),
                        .placeholder,
                        .quoteColor(quote)
                    ])
                    
//                    SettingSection.setting(titile: "0", items: [
//                        .localCurrency(localItem),
//                        .currencySymbol(symbolItem),
//                        .placeholder,
//                        .defaultCurrencyValue(defaultItem),
//                        .decimalDigit(digitItem),
//                        .placeholder,
//                        .more(more),
//                        .placeholder,
//                    ])
                ]
            }
            .bind(to: elements)
            .disposed(by: rx.disposeBag)

        return Output(items: elements,
                      selectedEvent: selectedEvent)
    }
}
