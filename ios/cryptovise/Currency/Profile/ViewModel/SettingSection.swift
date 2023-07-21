//
//  SettingSection.swift
//  cryptovise
//
//  Created by fd on 2022/11/3.
//

import RxDataSources
import UIKit

enum SettingSection {
    case setting(titile: String, items: [SettingSectionItem])
}

//enum SettingSectionItem {
//    case localCurrency(SettingSwitchViewModel)
//    case currencySymbol(SettingSwitchViewModel)
//    case placeholder
//    case defaultCurrencyValue(SettingArrowViewModel)
//    case decimalDigit(SettingArrowViewModel)
//    case more(SettingArrowViewModel)
//}
enum SettingSectionItem {
    case placeholder
    case defaultCurrencyValue(SettingArrowViewModel)
    case decimalDigit(SettingArrowViewModel)
    case quoteColor(SettingArrowViewModel)
}

extension SettingSection: SectionModelType {
    var items: [SettingSectionItem] {
        switch self {
        case let .setting(_, items):
            return items
        }
    }

    init(original: SettingSection, items: [SettingSectionItem]) {
        switch original {
        case let .setting(title, items):
            self = .setting(titile: title, items: items)
        }
    }

    typealias Item = SettingSectionItem
}
