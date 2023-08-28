//
//  AppConfig.swift
//  tcurrency
//
//  Created by admin on 2023/6/20.
//

import Foundation
import KeychainSwift

struct AppConfig {
    static let baseURLForChart = "https://xcr.tratao.com/api/ver2/exchange/yahoo/" // http server
    static let baseURLForAPI = "https://7697d200.cwallet.com/rate/tool"
    static let socketBaseURL = "ws://api.exchange2currency.com/wss" // socket server
    
    static let keychainAccess = KeychainSwiftAccessOptions.accessibleAfterFirstUnlockThisDeviceOnly
    
    // keychain
    static let kUserIdKeychain = "kUserIdKeychain"
    // UserDefault
    static let kUserDefaultUserInfoKey = "kUserDefaultUserInfoKey"
    static let kUserDefaultTaskKey = "kUserDefaultTaskKey"
    static let appstoreAppId = "6451831333"
    // appsflyer
    static let appsFlyerDevKey = "gUyT294NkvpnTkQBYSDLXC" // use for appsflyer
    // app link
    static let appDownloadLink = "https://itunes.apple.com/app/id6451831333"
}
