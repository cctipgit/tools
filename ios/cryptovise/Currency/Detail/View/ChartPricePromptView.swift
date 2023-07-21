//
//  ChatPricePromptView.swift
//  cryptovise
//
//  Created by fd on 2023/1/1.
//

import UIKit

extension UIColor {
    static var promptColor: UIColor {
        return UIColor.color(with: "#000000",
                             darkHexString: "#727272")
    }

    static var promptLineColor: UIColor {
        return UIColor.color(with: "#000000", lightAlpha: 0.3,
                             darkHexString: "#FFFFFF", darkAlpha: 0.3)
    }
}

class ChartPricePromptView: UIView {
    var line = UIView().then {
        $0.backgroundColor = .promptLineColor
    }

    var contentView = UIView().then {
        $0.backgroundColor = .promptColor
        $0.cornerRadius = 13
        $0.clipsToBounds = false
    }

    var leftArrow = UIImageView().then {
        let image = UIImage(named: "float_arrow")
        $0.image = UIImage(cgImage: image!.cgImage!, scale: image!.scale, orientation: .down)
    }

    var rightArrow = UIImageView().then {
        $0.image = UIImage(named: "float_arrow")
    }

    var textLabel = UILabel().then {
        $0.font = .mediumPoppin(with: 12)
        $0.textAlignment = .center
    }

    override init(frame: CGRect) {
        super.init(frame: frame)
        makeUI()
    }

    required init?(coder: NSCoder) {
        super.init(coder: coder)
        makeUI()
    }

    func makeUI() {
        addSubview(line)
        contentView.addSubviews([leftArrow,
                                   textLabel,
                                   rightArrow])
        addSubview(contentView)
    }

    override func layoutSubviews() {
        super.layoutSubviews()

        line.pin
            .height(1)
            .horizontally()
            .vCenter()

        textLabel.pin
            .horizontally(8)
            .vCenter()
            .sizeToFit()
        
        contentView.pin
            .center()
            .height(26)
            .width(textLabel.width + 16)
        
        leftArrow.pin
            .left(-5)
            .vCenter()
            .sizeToFit()

        rightArrow.pin
            .right(-5)
            .vCenter()
            .sizeToFit()
        
        textLabel.pin
            .vCenter()
    }
}
