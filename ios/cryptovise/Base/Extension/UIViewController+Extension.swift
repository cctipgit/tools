//
//  UIViewController+Extension.swift
//  cryptovise
//
//  Created by fd on 2022/12/5.
//

import Foundation
func getWindow() -> UIWindow? {
    if let window = UIApplication.shared.connectedScenes
        .filter({ $0.activationState == .foregroundActive })
        .map({ $0 as? UIWindowScene })
        .compactMap({ $0 })
        .last?.windows
        .filter({ $0.isKeyWindow })
        .last {
        return window
    }

    if let windown = UIApplication.shared.windows.filter({ $0.isKeyWindow }).last {
        return windown
    }
    return UIApplication.shared.keyWindow
}

extension UIViewController {
    func findTopViewController() -> UIViewController {
        var currentVC = self
        while true {
            if currentVC.presentedViewController != nil {
                currentVC = currentVC.presentedViewController!
            } else {
                if currentVC.isKind(of: UINavigationController.self) {
                    currentVC = (currentVC as! UINavigationController).visibleViewController!
                } else if currentVC.isKind(of: UITabBarController.self) {
                    currentVC = (currentVC as! UITabBarController).selectedViewController!
                } else {
                    break
                }
            }
        }
        return currentVC
    }
}
