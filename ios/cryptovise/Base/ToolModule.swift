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
import AppsFlyerLib

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
    @objc
    func openNative(_ resolve: @escaping RCTPromiseResolveBlock, reject: @escaping RCTPromiseRejectBlock) {
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
        resolve("success")
    }
    
    @objc
    func getAppsFlyerConversionData(_ resolve: @escaping RCTPromiseResolveBlock, reject: @escaping RCTPromiseRejectBlock) {
        if let json = AppInstance.shared.appFlyerConversionInfo.jsonString() {
            return resolve(json)
        }
        return reject("500", "conversion data get error", nil)
    }
    
    @objc
    func logEvent(_ parameter: NSDictionary, resolve: @escaping RCTPromiseResolveBlock, reject: @escaping RCTPromiseRejectBlock) {
        if let model = AppsFlyerEventModel.mj_object(withKeyValues: parameter) {
            AppsFlyerLib.shared().logEvent(name: model.event, values: model.params) { result, error in
                self.printLog(message: result?.jsonString() ?? "")
                self.printLog(message: error?.localizedDescription ?? "")
            }
            return resolve(parameter.mj_JSONString())
        }
        return reject("500", "logEvent get error", nil)
    }
}
