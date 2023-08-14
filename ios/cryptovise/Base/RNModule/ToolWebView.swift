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

class ToolWebView: UIView {
    // RN
    @objc
    var url: String = "" {
        didSet {
            if let mUrl = URL(string: url), !webViewLoaded {
                let request = NSMutableURLRequest(url: mUrl, cachePolicy: .useProtocolCachePolicy, timeoutInterval: 60)
                webView.load(request as URLRequest)
                webViewLoaded = true
            }
        }
    }
    @objc
    var onMessage: RCTBubblingEventBlock?
    var webViewLoaded: Bool = false
    
    // private
    private var webView: WKWebView!
    private var modelWebView: WKWebView?
    private let contentController = WKUserContentController()
    fileprivate let kNativeToJsCallBackMethodName: String = "nativeCallBack"
    
    private let progressView = UIProgressView(progressViewStyle: .default) // Progress view
    private var estimatedProgressObserver: NSKeyValueObservation?
    public var isNeedShowProgress: Bool {
        get {
            return false
        }
    }
    
    // MARK: Super
    override init(frame: CGRect) {
        super.init(frame: frame)
        p_setElements()
        p_setupProgressView()
        p_setupEstimatedProgressObserver()
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
            return ["jsBridge"]
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
    private func p_setupEstimatedProgressObserver() {
        estimatedProgressObserver = webView.observe(\.estimatedProgress, options: [.new]) { [weak self] webView, _ in
            self?.progressView.progress = Float(webView.estimatedProgress)
        }
    }
    private func p_setupProgressView() {
        progressView.translatesAutoresizingMaskIntoConstraints = false
        progressView.trackTintColor = UIColor.toolViewBGColor
        progressView.progressTintColor = UIColor(red: 62.0 / 255.0, green: 161.0 / 255.0, blue: 39.0 / 255.0, alpha: 1)
        progressView.isHidden = true
        self.addSubview(progressView)
        NSLayoutConstraint.activate([
            progressView.leadingAnchor.constraint(equalTo: self.leadingAnchor),
            progressView.trailingAnchor.constraint(equalTo: self.trailingAnchor),
            progressView.topAnchor.constraint(equalTo: self.topAnchor),
            progressView.heightAnchor.constraint(equalToConstant: 1)
            ])
    }
    private func p_setElements() {
        self.backgroundColor = UIColor.toolViewBGColor
        let config = WKWebViewConfiguration()
        
        // window.jsBridge.postMessage(key, params)
        let scriptString = """
            window.jsBridge = {
              postMessage: function(key, params) {
                let dic = {};
                dic[key] = params?params:'0';
                window.webkit.messageHandlers.jsBridge.postMessage(JSON.stringify(dic));
              }
            };
        """
        let userScript = WKUserScript(source: scriptString, injectionTime: .atDocumentEnd, forMainFrameOnly: false)
        contentController.add(self, name: "jsBridge")
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
        if isNeedShowProgress {
            if progressView.isHidden {
                progressView.isHidden = false
            }
            UIView.animate(withDuration: 0.01) {
                self.progressView.alpha = 1.0
            }
        }
    }
    func webView(_ webView: WKWebView, didCommit navigation: WKNavigation!) {
        
    }
    func webView(_ webView: WKWebView, didFinish navigation: WKNavigation!) {
        if isNeedShowProgress {
            UIView.animate(withDuration: 0.01, animations: {
                self.progressView.alpha = 0.0
            }) { (isFinished) in
                self.progressView.isHidden = isFinished
            }
        }
    }
    func webView(_ webView: WKWebView, didFail navigation: WKNavigation!, withError error: Error) {
        self.progressView.isHidden = true
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
