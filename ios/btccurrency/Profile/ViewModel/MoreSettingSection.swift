//
//  MoreSettingSection.swift
//  btccurrency
//
//  Created by fd on 2022/11/3.
//

import Foundation
import RxDataSources
enum MoreSecttingSection {
    case setting(titile: String, items: [MoreSettingSectionItem])
}

enum MoreSettingSectionItem {
    case plugin(SettingArrowViewModel)
    case placeholder
    case language(SettingArrowViewModel)
    case troyWeight(SettingArrowViewModel)
    case quoteColor(SettingArrowViewModel)
    case keyboardSound(SettingSwitchViewModel)
}

extension MoreSecttingSection: SectionModelType {
    var items: [MoreSettingSectionItem] {
        switch self {
        case let .setting(_, items):
            return items
        }
    }

    init(original: MoreSecttingSection, items: [MoreSettingSectionItem]) {
        switch original {
        case let .setting(title, items):
            self = .setting(titile: title, items: items)
        }
    }

    typealias Item = MoreSettingSectionItem
}
