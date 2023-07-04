//
//  AppDelegate.swift
//  btccurrency
//
//  Created by fd on 2022/10/20.
//

import FirebaseCore
import FirebaseCrashlytics
import FluentDarkModeKit
import OSLog
import UIKit
import WidgetKit
import KeychainSwift

@main
class AppDelegate: UIResponder, UIApplicationDelegate {
    var window: UIWindow?
    
    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {

        let configuration = DMEnvironmentConfiguration()
        configuration.themeChangeHandler = {}
        DarkModeManager.setup(with: configuration)
        DarkModeManager.register(with: UIApplication.shared)

        FirebaseApp.configure()
        setUserInfo()

        
        // origin
        URL.socketBaseURL = URL(string: AppConfig.socketBaseURL)! // set socket server url
        let main = CustomTabViewController()
        let navi = YNavigationController(rootViewController: main)
        navi.navigationBar.isHidden = true
        main.fd_prefersNavigationBarHidden = true
        window = UIWindow(frame: UIScreen.main.bounds)
        window?.rootViewController = navi
        window!.makeKeyAndVisible()
        _ = CurrencyRate.shared
       
        
        // RN
//        var rnUrl: URL!
//        if true {
//            rnUrl = RCTBundleURLProvider.sharedSettings().jsBundleURL(forBundleRoot: "index", fallbackExtension: nil)
//        } else {
//            // [CodePush bundleURL]
//            rnUrl = RCTBundleURLProvider.sharedSettings().jsBundleURL(forBundleRoot: "index")
//        }
//        let rootView = RCTRootView(bundleURL: rnUrl, moduleName: "MyApp", initialProperties: nil)
//        rootView.backgroundColor = UIColor.systemPink
//
//        let rootViewController = UIViewController()
//
//        rootViewController.view = rootView
//
//        let navi = YNavigationController(rootViewController: rootViewController)
//        navi.navigationBar.isHidden = true
//        window = UIWindow(frame: UIScreen.main.bounds)
//        window?.rootViewController = navi
//        window?.makeKeyAndVisible()

        return true
    }

    func setUserInfo() {
        Crashlytics.crashlytics().setCrashlyticsCollectionEnabled(true)
    }
}
