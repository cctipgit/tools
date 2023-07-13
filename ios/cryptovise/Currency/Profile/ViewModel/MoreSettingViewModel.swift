//
//  MoreSettingViewModel.swift
//  cryptovise
//
//  Created by fd on 2022/11/3.
//

import Foundation
import Localize_Swift
import NSObject_Rx
import RxCocoa
import RxRelay
import RxSwift

class MoreSettingViewModel: NSObject, ViewModel {
    struct Input {
        let trigger: Observable<Void>
        let selection: Driver<MoreSettingSectionItem>
    }

    struct Output {
        let items: BehaviorRelay<[MoreSecttingSection]>
        let selectedEvent: Driver<MoreSettingSectionItem>
    }

    override init() {
        super.init()
    }

    func transform(input: Input) -> Output {
        let elements = BehaviorRelay<[MoreSecttingSection]>(value: [])
        let selectedEvent = input.selection

        input.trigger
            .map { _ in

                let plugin = SettingArrowViewModel(title: "Plugin".localized(), value: "")
                
                let language = SettingArrowViewModel(title: "Language".localized(), value: LocalizeManager.shared.currentDispalyName())

                let isRedRise = AppSetting.shared.quoteColor.value ? "Red rise green fall".localized() : "Green rise red fall".localized()
                let quote = SettingArrowViewModel(title: "Quote color".localized(), value: isRedRise)

                let isKeyboardSound = AppSetting.shared.keyboardSound.value
                let keyboardSound = SettingSwitchViewModel(title: "Keyboard Sound".localized(), isOn: isKeyboardSound)
                keyboardSound.isOn
                    .bind(to: AppSetting.shared.keyboardSound)
                    .disposed(by: self.rx.disposeBag)

                return [
                    MoreSecttingSection.setting(titile: "0", items: [
                        .plugin(plugin),
                        .language(language),
                        .placeholder,
                        .quoteColor(quote),
                        .keyboardSound(keyboardSound),
                    ]),
                ]
            }
            .bind(to: elements)
            .disposed(by: rx.disposeBag)

        return Output(items: elements,
                      selectedEvent: selectedEvent)
    }
}
