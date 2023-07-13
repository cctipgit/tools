//
//  AppConfig.swift
//  tcurrency
//
//  Created by admin on 2023/6/20.
//

import Foundation
import KeychainSwift
// com.hellpeng.abcWT
// com.cwallet.cryptovise
struct AppConfig {
    static let baseURLForChart = "http://46.51.243.83/coinconvert" // http server
    static let baseURLForAPI = "https://7697d200.cwallet.com/rate/tool"
    static let socketBaseURL = "ws://sea.linkflower.link:2100/wss" // socket server
    static let settingPrivacyUrl = "https://www.google.com"
    static let settingAboutUrl = "https://www.gogole.com"
    
    static let keychainAccess = KeychainSwiftAccessOptions.accessibleAfterFirstUnlockThisDeviceOnly
    
    // keychain
    static let kUserIdKeychain = "kUserIdKeychain"
    // UserDefault
    static let kUserDefaultUserInfoKey = "kUserDefaultUserInfoKey"
    static let kUserDefaultTaskKey = "kUserDefaultTaskKey"
    static let appstoreAppId = "1142110895"
}
