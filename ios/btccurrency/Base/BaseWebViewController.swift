//
//  BaseWebViewController.swift
//  btccurrency
//
//  Created by admin on 2023/6/28.
//

import UIKit
import SnapKit
import WebKit

class BaseWebViewController: YBaseViewController {
    private var webView: WKWebView!
    private let contentController = WKUserContentController()
    
    public var webUrl: URL {
        get {
            let urlStr = Bundle.main.path(forResource: "index", ofType: "html")
            let mUrl = URL.init(fileURLWithPath: urlStr!)
            return mUrl
        }
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        p_setElements()
        updateViewConstraints()
    }
    
    override func updateViewConstraints() {
        super.updateViewConstraints()
        webView.snp.makeConstraints { make in
            make.left.right.bottom.equalToSuperview()
            make.top.equalTo(navigationView.snp.bottom).offset(16)
        }
    }
    
    private func p_setElements() {
        view.backgroundColor = .white
        let request = NSMutableURLRequest(url: webUrl, cachePolicy: .reloadIgnoringLocalAndRemoteCacheData, timeoutInterval: 5)
        let config = WKWebViewConfiguration()
        config.userContentController = contentController
        webView = WKWebView.init(frame: .zero, configuration: config)
        webView.uiDelegate = self
        webView.navigationDelegate = self
        webView.load(request as URLRequest)
        view.addSubview(webView)
    }
}

extension BaseWebViewController: WKScriptMessageHandler, WKUIDelegate, WKNavigationDelegate {
    // MARK: WKNavigationDelegate
    func webView(_ webView: WKWebView, didStartProvisionalNavigation navigation: WKNavigation!) {

    }
    
    func webView(_ webView: WKWebView, didCommit navigation: WKNavigation!) {
        
    }
    
    func webView(_ webView: WKWebView, decidePolicyFor navigationAction: WKNavigationAction, decisionHandler: @escaping (WKNavigationActionPolicy) -> Void) {
        decisionHandler(.allow)
    }

    func webView(_ webView: WKWebView, didFinish navigation: WKNavigation!) {

    }
    
    func webView(_ webView: WKWebView, didFail navigation: WKNavigation!, withError error: Error) {
        webView.reload()
    }
    
    // MARK: WKScriptMessageHandler
    func userContentController(_ userContentController: WKUserContentController, didReceive message: WKScriptMessage) {
        
    }
    
    // MARK: WKUIDelegate
    func webView(_ webView: WKWebView, runJavaScriptAlertPanelWithMessage message: String, initiatedByFrame frame: WKFrameInfo, completionHandler: @escaping () -> Void) {
        completionHandler()
    }
 
    func webView(_ webView: WKWebView, runJavaScriptConfirmPanelWithMessage message: String, initiatedByFrame frame: WKFrameInfo, completionHandler: @escaping (Bool) -> Void) {
        completionHandler(true)
    }
}
