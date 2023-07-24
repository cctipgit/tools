//
//  AppDelegate.swift
//  cryptovise
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
import AppsFlyerLib
import AppTrackingTransparency
import RxSwift
import RxRelay

@main
class AppDelegate: UIResponder, UIApplicationDelegate {
    var window: UIWindow?
    var lView: UIView?
    var appflyerConversionInfo: BehaviorRelay<[AnyHashable : Any]?> = BehaviorRelay(value: nil)
 
    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {
        let configuration = DMEnvironmentConfiguration()
        configuration.themeChangeHandler = {}
        DarkModeManager.setup(with: configuration)
        DarkModeManager.register(with: UIApplication.shared)

        FirebaseApp.configure()
        setUserInfo()
        
        // AppsFlyer
        AppsFlyerLib.shared().appsFlyerDevKey = AppConfig.appsFlyerDevKey
        AppsFlyerLib.shared().appleAppID = AppConfig.appstoreAppId
        AppsFlyerLib.shared().isDebug = true
        AppsFlyerLib.shared().delegate = self
        
        // default window
        window = UIWindow(frame: UIScreen.main.bounds)
        if let launch = UIStoryboard(name: "LaunchScreen", bundle: nil).instantiateInitialViewController() {
            launch.view.frame = UIScreen.main.bounds
            lView = launch.view
            window?.rootViewController = UIViewController()
            window?.addSubview(launch.view)
            window!.makeKeyAndVisible()
        }
        appflyerConversionInfo.subscribe(onNext: { [weak self] val in
            guard let self = self else { return }
            guard let _ = val else { return }
            loadRN()
            lView?.removeFromSuperview()
        }).disposed(by: rx.disposeBag)
        AppsFlyerLib.shared().start()
        return true
    }
    
    func applicationDidBecomeActive(_ application: UIApplication) {
        
    }
    
    func setUserInfo() {
        Crashlytics.crashlytics().setCrashlyticsCollectionEnabled(true)
    }
    
    func loadOrigin() {
         URL.socketBaseURL = URL(string: AppConfig.socketBaseURL)! // set socket server url
         let main = CustomTabViewController()
         let navi = YNavigationController(rootViewController: main)
         navi.navigationBar.isHidden = true
         main.fd_prefersNavigationBarHidden = true
         window?.rootViewController = navi
         window!.makeKeyAndVisible()
         _ = CurrencyRate.shared
    }
    
    func loadRN() {
        var rnUrl: URL!
        #if DEBUG
            rnUrl = RCTBundleURLProvider.sharedSettings().jsBundleURL(forBundleRoot: "index", fallbackExtension: nil)
        #else
            rnUrl = Bundle.main.url(forResource: "main", withExtension: "jsbundle")
        #endif
        let rootView = RCTRootView(bundleURL: rnUrl, moduleName: "MyApp", initialProperties: nil)
        rootView.backgroundColor = UIColor.white

        let rootViewController = UIViewController()
        rootViewController.view = rootView

        let navi = YNavigationController(rootViewController: rootViewController)
        navi.navigationBar.isHidden = true
        window?.rootViewController = navi
        window?.makeKeyAndVisible()
    }
}

extension AppDelegate: AppsFlyerLibDelegate {
    func onConversionDataFail(_ error: Error) {
        loadOrigin()
    }
    
    func onConversionDataSuccess(_ conversionInfo: [AnyHashable : Any]) {
        appflyerConversionInfo.accept(conversionInfo)
        AppInstance.shared.appFlyerConversionInfo = conversionInfo
    }
}
