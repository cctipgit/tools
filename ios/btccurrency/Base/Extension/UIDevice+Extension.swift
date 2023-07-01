//
//  UIDevice+Extension.swift
//  btccurrency
//
//  Created by admin on 2023/6/28.
//

import Foundation

extension UIDevice {
    static func kStatusBarHeight() -> CGFloat {
        let scene = UIApplication.shared.connectedScenes.first
        guard let windowScene = scene as? UIWindowScene else { return 0 }
        guard let statusBarManager = windowScene.statusBarManager else { return 0 }
        return statusBarManager.statusBarFrame.height
    }
    
    static func kScreenWidth() -> CGFloat {
        return UIScreen.main.bounds.width
    }
    
    static func kScreenHeight() -> CGFloat {
        return UIScreen.main.bounds.height
    }
}
