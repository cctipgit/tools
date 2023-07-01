//
//  LocalizeManager.swift
//  btccurrency
//
//  Created by fd on 2023/1/30.
//

import Foundation
import Localize_Swift

enum LanuageCode: String, CaseIterable {
    case chinese_cn = "zh-Hans"
    case chinese_tr = "zh-Hant"
    case english = "en"
    case arabic = "ar"
    case burmese = "my"
    case french = "fr"
    case dutch = "nl"
    case german = "de"
    case hindi = "hi"
    case indonesian = "id"
    case italian = "it"
    case japanese = "ja"
    case korean = "ko"
    case malay = "ms"
    case nepali = "ne"
    case polish = "pl"
    case portuguese = "pt-BR"
    case russian = "ru"
    case spanish = "es"
    case turkish = "tr"
    case ukrainian = "uk"
    case vietnamese = "vi"
    case urdu = "ur-Arab-PK"

    
    func displayName() -> String {
        Localize.displayNameForLanguage(rawValue)
    }
}

class LocalizeManager {
    static let shared = LocalizeManager()

    private init() {}

    func current() -> String {
        Localize.currentLanguage()
    }

    func currentDispalyName() -> String {
        Localize.displayNameForLanguage(Localize.currentLanguage())
    }

    func changeCurrent(lanuage code: LanuageCode) {
        Localize.setCurrentLanguage(code.rawValue)
    }
}
