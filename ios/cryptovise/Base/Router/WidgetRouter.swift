//
//  File.swift
//  cryptovise
//
//  Created by fd on 2022/12/8.
//

import Foundation

struct WidgetRouter {
    var url: URL

    var urlComponents: URLComponents?

    init(url: URL) {
        self.url = url
        urlComponents = URLComponents(url: url, resolvingAgainstBaseURL: false)
    }

    func router() {
        if let nav = getWindow()?.rootViewController as? YNavigationController {
            if nav.visibleViewController is HomeCurrencyViewController,
               let homeVC = nav.visibleViewController as? HomeCurrencyViewController,
               homeVC.isFirstAppear {
                DispatchQueue.main.asyncAfter(deadline: .now() + 1) {
                    self.handle(urlComponents: self.urlComponents)
                }
            } else {
                handle(urlComponents: urlComponents)
            }
        }
    }

    func handle(urlComponents: URLComponents?) {
        guard let urlComponents
        else { return }
        if urlComponents.scheme == "widget" {
            if urlComponents.host == "homelist" {
                if let token = urlComponents.path.components(separatedBy: "/").last {
                    NotificationCenter.default.post(name: Notification.Name(widgetDidTappedConvertLineNotification), object: token)
                    return
                }
            }

            if urlComponents.host == "detail" {
                if let token = urlComponents.path.components(separatedBy: "/").last {
                    NotificationCenter.default.post(name: Notification.Name(widgetDidTappedQuotationNotification), object: token)
                    return
                }
            }
        }
        debugPrint(urlComponents)
    }
}
