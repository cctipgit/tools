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
    var data: [String: Any] = [String: Any]() {
        didSet {
            print("hello")
        }
    }
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
    
    private let backBtn = UIButton().then { v in
        v.setImage(UIImage(named: "n_back"), for: .normal)
        v.setImage(UIImage(named: "n_back"), for: .highlighted)
        v.isHidden = true
    }
    
    // MARK: Super
    override init(frame: CGRect) {
        super.init(frame: frame)
        p_setElements()
        p_makeEvents()
        updateConstraints()
    }
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    override func updateConstraints() {
        super.updateConstraints()
        backBtn.snp.makeConstraints { make in
            make.size.equalTo(24)
            make.left.equalToSuperview().offset(20)
            make.top.equalToSuperview().offset(UIDevice.kStatusBarHeight())
        }
        webView.snp.makeConstraints { (make) in
            make.left.right.bottom.equalToSuperview()
            make.top.equalTo(backBtn.snp.bottom).offset(20)
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
        self.backgroundColor = .white
        let config = WKWebViewConfiguration()
        // window.jsbridge.postMessage('jack')
        contentController.add(self, name: "jsbridge")
        config.userContentController = contentController
        webView = WKWebView(frame: .zero, configuration: config)
        webView.uiDelegate = self
        webView.navigationDelegate = self
        webView.scrollView.contentInsetAdjustmentBehavior = .never
        self.addSubviews([backBtn, webView])
    }
    private func p_makeEvents() {
        backBtn.rx.tap.subscribe(onNext: { [weak self] _ in
            guard let self = self else { return }
            if webView.canGoBack {
                webView.goBack()
            }
        }).disposed(by: rx.disposeBag)
    }
}

extension ToolWebView: WKScriptMessageHandler, WKUIDelegate, WKNavigationDelegate {
    // MARK: WKNavigationDelegate
    func webView(_ webView: WKWebView, didStartProvisionalNavigation navigation: WKNavigation!) {
        self.makeToastActivity(.center)
        backBtn.isHidden = !webView.canGoBack
    }
    func webView(_ webView: WKWebView, didCommit navigation: WKNavigation!) {
        self.hideAllToasts(includeActivity: true)
        backBtn.isHidden = !webView.canGoBack
    }
    func webView(_ webView: WKWebView, didFinish navigation: WKNavigation!) {
        self.hideAllToasts(includeActivity: true)
        backBtn.isHidden = !webView.canGoBack
    }
    func webView(_ webView: WKWebView, didFail navigation: WKNavigation!, withError error: Error) {
        backBtn.isHidden = !webView.canGoBack
        self.hideAllToasts(includeActivity: true)
        webView.reload()
    }
    // MARK: WKScriptMessageHandler
    func userContentController(_ userContentController: WKUserContentController, didReceive message: WKScriptMessage) {
        jsToNativeBridges.forEach { bridgeName in
            if message.name == bridgeName, let msgFunc = onMessage {
                msgFunc(["onMessage": message.body])
            }
        }
    }
    // MARK: WKUIDelegate
    func webView(_ webView: WKWebView, runJavaScriptAlertPanelWithMessage message: String, initiatedByFrame frame: WKFrameInfo, completionHandler: @escaping () -> Void) {
        let alertVC = UIAlertController(title: nil, message: message, preferredStyle: .alert)
        alertVC.addAction(UIAlertAction(title: "确定", style: .default, handler: { (action) in
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
            modelWebView = WKWebView(frame: CGRect(x: 0, y: 0, width: UIDevice.kScreenWidth(), height: UIDevice.kScreenHeight()), configuration: configuration)
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
        UIApplication.shared.windows.first?.rootViewController?.present(alertVC, animated: true)
    }
}
