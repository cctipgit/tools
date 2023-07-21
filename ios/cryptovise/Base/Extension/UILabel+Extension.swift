//
//  UILabel+Extension.swift
//  cryptovise
//
//  Created by fd on 2022/10/24.
//

import UIKit

class LabelInset: UILabel {
    
    var contentInset: UIEdgeInsets = .zero {
        didSet {
            invalidateIntrinsicContentSize()
            setNeedsLayout()
        }
    }
  
    override func drawText(in rect: CGRect) {
        super.drawText(in: rect.inset(by: contentInset))
    }
  
    override var intrinsicContentSize: CGSize {
        var size = super.intrinsicContentSize
        size.height += contentInset.top + contentInset.bottom
        size.width += contentInset.left + contentInset.right
        return size
    }
}

extension UILabel {
    var bounceText: String? {
        set {
            text = newValue
            makeBounce()
        }

        get {
            return text
        }
    }

    func makeBounce() {
        alpha = 0
        transform = CGAffineTransform(translationX: 0, y: -height)
        UIView.animate(withDuration: 0.5) {
            self.alpha = 1
            self.transform = CGAffineTransformIdentity
        }
    }
}

extension UILabel {
    func getTimeDurationFromNum(num: Double) -> Double {
        return 0.5
    }

    func animation(_ fromNum: Double, toNum: Double, duration: Double) {
        text = String(format: "%0.4f", fromNum)
        let totalCountInt = getCountFromNum(num: fabs(toNum - fromNum))
        let totalCount = Double(totalCountInt)
        let delayTime = duration / totalCount
        var mediumNumArr: [String] = [String]()
        for i in 0 ..< Int(totalCount) {
            if (toNum - fromNum) > 0 {
                mediumNumArr.append(String(format: "%.4f", Double(i) * ((toNum - fromNum) / totalCount) + fromNum))
            } else {
                mediumNumArr.append(String(format: "%.4f", fromNum - Double(i) * ((fromNum - toNum) / totalCount)))
            }
            if i == Int(totalCount) - 1 {
                mediumNumArr.append(String(format: "%.4f", toNum))
            }
        }
        changeLabelTitle(delayTime, mediumArr: &mediumNumArr)
    }

    func getCountFromNum(num: Double) -> Int {
        if num <= 0 {
            return 1
        } else if num < 1000 {
            return 100
        } else {
            return Int(num / 20)
        }
    }

    func changeLabelTitle(_ delayTime: Double, mediumArr: inout [String]) {
        if mediumArr.isEmpty {
            return
        }

        text = mediumArr.first
        mediumArr.removeFirst()
        var temp = mediumArr
        DispatchQueue.main.asyncAfter(deadline: DispatchTime.now() + delayTime, execute: {
            self.changeLabelTitle(delayTime, mediumArr: &temp)
        })
    }
}
