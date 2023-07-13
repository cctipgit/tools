//
//  SettingCellViewModel.swift
//  btccurrency
//
//  Created by fd on 2022/11/3.
//

import RxRelay
import UIKit

class SettingCellViewModel {
}

class SettingSwitchViewModel: SettingCellViewModel {
    var title: String
    var isOn: BehaviorRelay<Bool>

    init(title: String, isOn: Bool) {
        self.title = title
        self.isOn = BehaviorRelay(value: isOn)
    }
}

class SettingArrowViewModel: SettingCellViewModel {
    var title: String
    var value: BehaviorRelay<String>

    init(title: String, value: String) {
        self.title = title
        self.value = BehaviorRelay(value: value)
    }
}

class SettingSelectViewModel: SettingCellViewModel {
    var name: String
    var isSelect: Bool
    init(name: String, isSelect: Bool) {
        self.name = name
        self.isSelect = isSelect
    }
}
