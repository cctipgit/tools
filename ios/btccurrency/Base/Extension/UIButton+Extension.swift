//
//  UIButton+Extension.swift
//  btccurrency
//
//  Created by admin on 2023/7/10.
//

import Foundation
import UIKit

private var key: Void?

extension UIButton {
    // Add associate object
    var assoObj: Any? {
        get {
            return objc_getAssociatedObject(self, &key)
        }
        
        set {
            objc_setAssociatedObject(self, &key, newValue, .OBJC_ASSOCIATION_RETAIN_NONATOMIC)
        }
    }
}
