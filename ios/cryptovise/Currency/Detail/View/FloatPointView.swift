//
//  FloatPointView.swift
//  cryptovise
//
//  Created by fd on 2022/11/22.
//

import UIKit

class FloatPointView: UIView {
    var pointView = UIView()
    var animationView = UIView()

    override init(frame: CGRect) {
        super.init(frame: .zero)
        addSubview(animationView)
        addSubview(pointView)

        backgroundColor = .backgroundColor
        animationView.backgroundColor = .darkBlueColor
        pointView.backgroundColor = .darkBlueColor
    }

    required init?(coder: NSCoder) {
        super.init(coder: coder)
    }

    func startAnimate() {
        let scale = CABasicAnimation(keyPath: "transform.scale")
        scale.duration = 2.0
        scale.fromValue = 1
        scale.toValue = 10.0
        scale.timingFunction = CAMediaTimingFunction(name: .easeOut)

        let alpha = CABasicAnimation(keyPath: "opacity")
        alpha.fromValue = 1
        alpha.toValue = 0
        alpha.duration = 2
        scale.timingFunction = CAMediaTimingFunction(name: .easeOut)

        let group = CAAnimationGroup()
        group.animations = [scale, alpha]
        group.repeatCount = .greatestFiniteMagnitude
        group.fillMode = .forwards
        group.duration = 2
        group.isRemovedOnCompletion = false
        group.timingFunction = CAMediaTimingFunction(name: .easeOut)
        animationView.layer.add(group, forKey: "group")
    }

    override func layoutSubviews() {
        super.layoutSubviews()

        if width > 0 && height > 0 {
            pointView.pin
                .center()
                .width(width - 4)
                .height(height - 4)
        }

        animationView.pin
            .center()
            .width(of: pointView)
            .height(of: pointView)

        cornerRadius = height / 2
        animationView.cornerRadius = animationView.height / 2
        pointView.cornerRadius = pointView.height / 2
    }
}
