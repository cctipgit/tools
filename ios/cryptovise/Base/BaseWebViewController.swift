//
//  BaseWebViewController.swift
//  cryptovise
//
//  Created by admin on 2023/6/28.
//

import UIKit
import SnapKit
import WebKit

class BaseWebViewController: YBaseViewController {
    private let progressView = UIProgressView(progressViewStyle: .default) // Progress view
    private var estimatedProgressObserver: NSKeyValueObservation?
    public var webUrl: URL {
        get {
            let urlStr = Bundle.main.path(forResource: "index", ofType: "html")
            let mUrl = URL.init(fileURLWithPath: urlStr!)
            return mUrl
        }
    }
    
    // Is need progress view
    public var isNeedShowProgress: Bool {
        get {
            return true
        }
    }
    
    public var jsToNativeMethods: [String: ((_ params: Any) -> String)] {
        get {
            /// params: From JS parameter String: Origin to js parameter
            func funcHello(params: Any) -> String {
                return getCallBackParams(isSuccess: true, data: nil)
            }
            return ["funcHello": funcHello]
        }
    }

    public func manualCallJSMethod(methodName: String, params: String, callBack: ((_ obj: Any?) -> Void)?) {
        self.webView.evaluateJavaScript("\(methodName)(\(params))") { (obj, error) in
            if let mCallBack = callBack {
                mCallBack(obj)
            }
        }
    }
    
    public func getCallBackParams(isSuccess: Bool, data: Any?) -> String {
        let callBackDic: [String: Any] = [
            "isSuccess": isSuccess,
            "data": data ?? ""
        ]
        let callBackData = try? JSONSerialization.data(withJSONObject: callBackDic, options: .prettyPrinted)
        let callBackParamsStr = "\(NSString.init(data: callBackData ?? Data(), encoding: String.Encoding.utf8.rawValue) ?? "")"
        return callBackParamsStr
    }
    
    internal var webView: WKWebView!
    var newWebView: WKWebView?
    
    private let contentController = WKUserContentController()
    fileprivate let kNativeToJsCallBackMethodName: String = "nativeCallBack"
    
    // MARK: Super Method
    override func viewDidLoad() {
        super.viewDidLoad()
        p_setElements()
        p_setupProgressView()
        p_setupEstimatedProgressObserver()
        updateViewConstraints()
    }
    
    override func viewDidDisappear(_ animated: Bool) {
        super.viewDidDisappear(animated)
        self.progressView.isHidden = true
        // 移除Messagehandler
        jsToNativeMethods.keys.forEach { (methodName) in
            self.webView.configuration.userContentController.removeScriptMessageHandler(forName: methodName)
        }
    }
    
    override func updateViewConstraints() {
        super.updateViewConstraints()
        webView.snp.makeConstraints { (make) in
            make.bottom.equalToSuperview()
            make.left.equalToSuperview()
            make.right.equalToSuperview()
            make.top.equalTo(navigationView.snp.bottom)
        }
    }
    
    // Private Method
    private func p_setupEstimatedProgressObserver() {
        estimatedProgressObserver = webView.observe(\.estimatedProgress, options: [.new]) { [weak self] webView, _ in
            self?.progressView.progress = Float(webView.estimatedProgress)
        }
    }
    
    private func p_setupProgressView() {
        guard let navigationBar = navigationController?.navigationBar else { return }
        progressView.translatesAutoresizingMaskIntoConstraints = false
        progressView.trackTintColor = UIColor.white
        progressView.progressTintColor = UIColor.purple
        progressView.isHidden = true
        navigationBar.addSubview(progressView)
        NSLayoutConstraint.activate([
            progressView.leadingAnchor.constraint(equalTo: navigationBar.leadingAnchor),
            progressView.trailingAnchor.constraint(equalTo: navigationBar.trailingAnchor),
            progressView.bottomAnchor.constraint(equalTo: navigationBar.bottomAnchor),
            progressView.heightAnchor.constraint(equalToConstant: 1)
            ])
    }
    private func p_setElements() {
        view.backgroundColor = .white
        jsToNativeMethods.keys.forEach { (methodName) in
            contentController.add(self, name: methodName)
        }
        
        let config = WKWebViewConfiguration()
        config.userContentController = contentController
        webView = WKWebView.init(frame: .zero, configuration: config)
        webView.uiDelegate = self
        webView.navigationDelegate = self
        // 加载
        if p_isLocalFile() {
            webView.loadFileURL(webUrl, allowingReadAccessTo: webUrl)
        } else {
            let request = NSMutableURLRequest(url: webUrl, cachePolicy: .reloadIgnoringLocalAndRemoteCacheData, timeoutInterval: 5)
            webView.load(request as URLRequest)
        }
        if #available(iOS 11.0, *) {
            webView.scrollView.contentInsetAdjustmentBehavior = .never
        } else {
            webView.scrollView.contentInset = .zero
        }
        view.addSubview(webView)
    }
    
    func p_isLocalFile() -> Bool {
        if let scheme = webUrl.scheme, scheme == "file", FileManager.default.fileExists(atPath: webUrl.path) {
            return true
        } else {
            return false
        }
    }
}

extension BaseWebViewController: WKScriptMessageHandler, WKUIDelegate, WKNavigationDelegate {

    // MARK: WKNavigationDelegate
    func webView(_ webView: WKWebView, didStartProvisionalNavigation navigation: WKNavigation!) {
        setTheCookieOfWebView()
        if isNeedShowProgress {
            if progressView.isHidden {
                progressView.isHidden = false
            }
            UIView.animate(withDuration: 0.3) {
                self.progressView.alpha = 1.0
            }
        }
    }
    
    func webView(_ webView: WKWebView, didCommit navigation: WKNavigation!) {
        setTheCookieOfWebView()
    }
    
    func setTheCookieOfWebView(){
        /**
        let cookieStorage : HTTPCookieStorage = HTTPCookieStorage.shared
        let headerField = "Authorization"
        var headerValue = ""
        if let oauthInfo = SSSharedInstance.shared.getOauth2Info() {
            headerValue = oauthInfo.accessToken
        }

        // 创建一个HTTPCookie对象
        var props = Dictionary<HTTPCookiePropertyKey, Any>()
        props[HTTPCookiePropertyKey.name] = headerField
        props[HTTPCookiePropertyKey.value] = headerValue
        props[HTTPCookiePropertyKey.path] = "/"
        props[HTTPCookiePropertyKey.domain] = AppConfig.kH5Host
        let cookie = HTTPCookie(properties: props)

        // 通过setCookie方法把Cookie保存起来
        cookieStorage.setCookie(cookie!)
        for cookie in (cookieStorage.cookies ?? nil)!{
            let nameStr = "\"document.cookie=\(cookie.name)=\(cookie.value)"
            self.webView.evaluateJavaScript(nameStr, completionHandler: nil)
        }
        */
    }
    func webView(_ webView: WKWebView, didFinish navigation: WKNavigation!) {
        if isNeedShowProgress {
            UIView.animate(withDuration: 0.3, animations: {
                self.progressView.alpha = 0.0
            }) { (isFinished) in
                self.progressView.isHidden = isFinished
            }
        }
    }
    
    func webView(_ webView: WKWebView, didFail navigation: WKNavigation!, withError error: Error) {
        webView.reload()
    }
    
    // MARK: WKScriptMessageHandler
    func userContentController(_ userContentController: WKUserContentController, didReceive message: WKScriptMessage) {
        jsToNativeMethods.enumerated().forEach { (index, element) in
            if message.name == element.key {
                let callBackParams = element.value(message.body)
                self.webView.evaluateJavaScript("\(kNativeToJsCallBackMethodName)(\(callBackParams))", completionHandler: nil)
            }
        }
    }
    
    // MARK: WKUIDelegate
    func webView(_ webView: WKWebView, runJavaScriptAlertPanelWithMessage message: String, initiatedByFrame frame: WKFrameInfo, completionHandler: @escaping () -> Void) {
        let alertVC = UIAlertController(title: nil, message: message, preferredStyle: .alert)
        alertVC.addAction(UIAlertAction(title: "确定", style: .default, handler: { (action) in
            alertVC.dismiss(animated: true, completion: nil)
        }))
        self.present(alertVC, animated: true)
        completionHandler()
    }
    
    func webViewDidClose(_ webView: WKWebView) {
        newWebView?.removeFromSuperview()
    }

    func webView(_ webView: WKWebView, createWebViewWith configuration: WKWebViewConfiguration, for navigationAction: WKNavigationAction, windowFeatures: WKWindowFeatures) -> WKWebView? {
        if let url = navigationAction.request.url {
                // 创建一个新的WKWebView来打开弹出窗口
             newWebView = WKWebView(frame: CGRect(x: 0, y: 100, width: self.view.bounds.width, height: self.view.bounds.height), configuration: configuration)
                newWebView?.uiDelegate = self
                self.view.addSubview(newWebView!)
                return newWebView
            }
        return nil
    }
    
    func webView(_ webView: WKWebView, runJavaScriptConfirmPanelWithMessage message: String, initiatedByFrame frame: WKFrameInfo, completionHandler: @escaping (Bool) -> Void) {
        let alertVC = UIAlertController(title: nil, message: message, preferredStyle: .alert)
        alertVC.addAction(UIAlertAction(title: "确定", style: .default, handler: { (action) in
            alertVC.dismiss(animated: true, completion: {
                completionHandler(true)
            })
        }))
        alertVC.addAction(UIAlertAction(title: "取消", style: .default, handler: { (action) in
            alertVC.dismiss(animated: true, completion: {
                completionHandler(false)
            })
        }))
        self.present(alertVC, animated: true)
    }
}
