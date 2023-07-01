//
//  File.swift
//  btccurrency
//
//  Created by fd on 2022/10/24.
//

import UIKit

extension UITextField {
    var bounceText: String? {
        set {
            text = newValue
            makeBounce()
        }

        get {
            return text
        }
    }

    func insert(text: String?) {
        if let text {
            insertText(text)
        }
    }

    func makeBounce() {
        alpha = 0
        transform = CGAffineTransform(translationX: 0, y: -height)
        UIView.animate(withDuration: 0.1) {
            self.alpha = 1
            self.transform = CGAffineTransformIdentity
        }
    }
}
