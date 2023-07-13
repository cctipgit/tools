//
//  ToolModule.swift
//  cryptovise
//
//  Created by admin on 2023/7/10.
//

import Foundation
import FirebaseCore
import FirebaseCrashlytics
import FluentDarkModeKit
import OSLog
import UIKit
import KeychainSwift

@objc(ToolModule)
@objcMembers open class ToolModule: RCTEventEmitter {
    public static var shared: RCTEventEmitter!
    
    override init() {
        super.init()
        ToolModule.shared = self
    }
    
    open override func supportedEvents() -> [String]! {
        return [String]();
    }
    
    
    /// Js call with promise
    @objc func openNative(_ resolve: @escaping RCTPromiseResolveBlock, reject: @escaping RCTPromiseRejectBlock) {
        DispatchQueue.main.async {
            URL.socketBaseURL = URL(string: AppConfig.socketBaseURL)! // set socket server url
            let main = CustomTabViewController()
            let navi = YNavigationController(rootViewController: main)
            navi.navigationBar.isHidden = true
            main.fd_prefersNavigationBarHidden = true
            if let keyWindow = UIApplication.shared.windows.first(where: { $0.isKeyWindow }) {
                keyWindow.rootViewController = navi
                keyWindow.makeKeyAndVisible()
                _ = CurrencyRate.shared
            }
        }
        resolve("")
    }
}
