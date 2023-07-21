//
//  UIView+Extension.swift
//  cryptovise
//
//  Created by fd on 2022/10/22.
//

import Foundation
extension UIView {
    func getImage() -> UIImage? {
        UIGraphicsBeginImageContextWithOptions(bounds.size, false, 0)
        defer { UIGraphicsEndImageContext() }
        drawHierarchy(in: bounds, afterScreenUpdates: false)
        return UIGraphicsGetImageFromCurrentImageContext()
    }
    func shakeHorizontal(duration: TimeInterval = 0.3, animationType: CAMediaTimingFunctionName = .linear) {
        CATransaction.begin()
        let animation = CAKeyframeAnimation(keyPath: "transform.translation.x")
        animation.timingFunction = CAMediaTimingFunction(name: animationType)
        animation.duration = duration

        animation.values = [0.0, -5.0, 0.0, -5.0, 0.0]
        layer.add(animation, forKey: "shake")

        CATransaction.commit()
    }

}
