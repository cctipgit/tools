//
//  SwitchCurrencyGroup.swift
//  cryptovise
//
//  Created by fd on 2022/10/29.
//

import Differentiator
import Foundation


enum SwitchCurrencySectionType {
    case local
    case frequent
    case fixed
}

class SwitchCurrencyGroup: SectionModelType {
    var header: String
    var items: [SwitchCurrencyCellViewModel]
    var sectionType: SwitchCurrencySectionType

    typealias Item = SwitchCurrencyCellViewModel

    required init(original: SwitchCurrencyGroup, items: [SwitchCurrencyCellViewModel]) {
        self.items = items
        header = original.header
        sectionType = original.sectionType
    }

    init(header: String, items: [SwitchCurrencyCellViewModel], sectionType: SwitchCurrencySectionType) {
        self.header = header
        self.items = items
        self.sectionType = sectionType
    }
}
