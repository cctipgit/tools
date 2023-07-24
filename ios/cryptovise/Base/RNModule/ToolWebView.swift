//
//  ToolWebView.swift
//  cryptovise
//
//  Created by admin on 2023/7/21.
//

import UIKit
import SnapKit
import Then
import RxSwift
import Toast_Swift

class ToolWebView: UIView {
    // RN
    @objc
    var url: String = "" {
        didSet {
            if let mUrl = URL(string: url) {
                let request = NSMutableURLRequest(url: mUrl, cachePolicy: .reloadIgnoringLocalAndRemoteCacheData, timeoutInterval: 60)
                webView.load(request as URLRequest)
            }
        }
    }
    @objc
    var onMessage: RCTBubblingEventBlock?
    
    // private
    private var webView: WKWebView!
    private var modelWebView: WKWebView?
    private let contentController = WKUserContentController()
    fileprivate let kNativeToJsCallBackMethodName: String = "nativeCallBack"
    

    // MARK: Super
    override init(frame: CGRect) {
        super.init(frame: frame)
        p_setElements()
        updateConstraints()
    }
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    override func updateConstraints() {
        super.updateConstraints()
        webView.snp.makeConstraints { (make) in
            make.edges.equalToSuperview()
        }
    }
    public var jsToNativeBridges: [String] {
        get {
            return ["jsbridge"]
        }
    }
    public func manualCallJSMethod(methodName: String, params: String, callBack: ((_ obj: Any?) -> Void)?) {
        self.webView.evaluateJavaScript("\(methodName)(\(params))") { (obj, error) in
            if let mCallBack = callBack {
                mCallBack(obj)
            }
        }
    }
    // MARK: Private
    private func p_setElements() {
        self.backgroundColor = UIColor.toolViewBGColor
        let config = WKWebViewConfiguration()
        
        // window.jsbridge.postMessage('jack')
        let scriptString = """
            window.jsbridge = {
              postMessage: function(message) {
                window.webkit.messageHandlers.jsbridge.postMessage(message)
              }
            };
        """
        let userScript = WKUserScript(source: scriptString, injectionTime: .atDocumentEnd, forMainFrameOnly: false)
        contentController.add(self, name: "jsbridge")
        contentController.addUserScript(userScript)
        config.userContentController = contentController
        config.applicationNameForUserAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 13_4_1) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/16.5 Safari/605.1.15"
        // allow javascript alert
        
        config.preferences.setValue(true, forKey: "allowFileAccessFromFileURLs")
        config.setValue(true, forKey: "allowUniversalAccessFromFileURLs")
        
        config.preferences.javaScriptCanOpenWindowsAutomatically = true
        if #available(iOS 14.0, *) {
            config.defaultWebpagePreferences.allowsContentJavaScript = true
        } else {
            config.preferences.javaScriptEnabled = true
        }
        
        // setcache
        config.websiteDataStore = .default()
        config.processPool = WKProcessPool()
        
        let sessionConfiguration = URLSessionConfiguration.default
        sessionConfiguration.requestCachePolicy = .returnCacheDataElseLoad
        sessionConfiguration.urlCache = URLCache(memoryCapacity: 20 * 1024 * 1024, diskCapacity: 100 * 1024 * 1024, diskPath: nil)
        
        webView = WKWebView(frame: .zero, configuration: config)
        webView.uiDelegate = self
        webView.navigationDelegate = self
        webView.scrollView.contentInsetAdjustmentBehavior = .never
        webView.backgroundColor = UIColor.toolViewBGColor
        webView.isOpaque = false
        
        // set bgView
        let bgView = UIView(frame: .zero)
        webView.addSubview(bgView)
        bgView.snp.makeConstraints { make in
            make.edges.equalToSuperview()
        }
        webView.sendSubviewToBack(bgView)
        
        webView.scrollView.backgroundColor = UIColor.toolViewBGColor
        self.addSubview(webView)
    }
}

extension ToolWebView: WKScriptMessageHandler, WKUIDelegate, WKNavigationDelegate {
    // MARK: WKNavigationDelegate
    func webView(_ webView: WKWebView, didStartProvisionalNavigation navigation: WKNavigation!) {
        self.makeToastActivity(.center)
    }
    func webView(_ webView: WKWebView, didCommit navigation: WKNavigation!) {
        self.hideAllToasts(includeActivity: true)
    }
    func webView(_ webView: WKWebView, didFinish navigation: WKNavigation!) {
        self.hideAllToasts(includeActivity: true)
    }
    func webView(_ webView: WKWebView, didFail navigation: WKNavigation!, withError error: Error) {
        self.hideAllToasts(includeActivity: true)
        webView.reload()
    }
    // MARK: WKScriptMessageHandler
    func userContentController(_ userContentController: WKUserContentController, didReceive message: WKScriptMessage) {
        jsToNativeBridges.forEach { bridgeName in
            if message.name == bridgeName, let msgFunc = onMessage {
                msgFunc(["message": message.body])
            }
        }
    }
    // MARK: WKUIDelegate
    func webView(_ webView: WKWebView, runJavaScriptAlertPanelWithMessage message: String, initiatedByFrame frame: WKFrameInfo, completionHandler: @escaping () -> Void) {
        let alertVC = UIAlertController(title: nil, message: message, preferredStyle: .alert)
        alertVC.addAction(UIAlertAction(title: "OK".localized(), style: .default, handler: { (action) in
            alertVC.dismiss(animated: true, completion: nil)
        }))
        UIApplication.shared.windows.first?.rootViewController?.present(alertVC, animated: true)
        completionHandler()
    }
    func webViewDidClose(_ webView: WKWebView) {
        UIApplication.shared.windows.first?.rootViewController?.dismiss(animated: true)
    }
    func webView(_ webView: WKWebView, createWebViewWith configuration: WKWebViewConfiguration, for navigationAction: WKNavigationAction, windowFeatures: WKWindowFeatures) -> WKWebView? {
        if let _ = navigationAction.request.url {
            configuration.applicationNameForUserAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 13_4_1) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/16.5 Safari/605.1.15"
            modelWebView = WKWebView(frame: CGRect(x: 0, y: 40, width: UIDevice.kScreenWidth(), height: UIDevice.kScreenHeight()), configuration: configuration)
            modelWebView?.uiDelegate = self
            let vc = UIViewController()
            vc.view = modelWebView
            UIApplication.shared.windows.first?.rootViewController?.present(vc, animated: true)
            return modelWebView
        }
        return nil
    }
    func webView(_ webView: WKWebView, runJavaScriptConfirmPanelWithMessage message: String, initiatedByFrame frame: WKFrameInfo, completionHandler: @escaping (Bool) -> Void) {
        let alertVC = UIAlertController(title: nil, message: message, preferredStyle: .alert)
        alertVC.addAction(UIAlertAction(title: "OK".localized(), style: .default, handler: { (action) in
            alertVC.dismiss(animated: true, completion: {
                completionHandler(true)
            })
        }))
        alertVC.addAction(UIAlertAction(title: "Cancel".localized(), style: .default, handler: { (action) in
            alertVC.dismiss(animated: true, completion: {
                completionHandler(false)
            })
        }))
        UIApplication.shared.windows.first?.rootViewController?.present(alertVC, animated: true)
    }
}
