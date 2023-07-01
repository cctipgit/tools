//
//  SwitchCurrencyItemView.swift
//  btccurrency
//
//  Created by fd on 2022/10/28.
//

import UIKit

class SwitchCurrencyItemView: UIView {
    var imageView = {
        UIImageView().then { imageView in
            imageView.image = UIImage(named: "switch_horizontal")
        }
    }()

    var nameLabel = {
        UILabel().then { label in
            label.textColor = .switchLeftTitleColor
            label.font = .semiboldPoppin(with: 16)
            label.text = "Switch Currency".localized()
        }
    }()

    override init(frame: CGRect) {
        super.init(frame: frame)

        backgroundColor = .switchLeftBackgroundColor
        addSubviews([nameLabel, imageView])
    }

    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    override func layoutSubviews() {
        super.layoutSubviews()
//        roundCorners([.topRight, .bottomRight], radius: 16)

        imageView.pin.right(14).vCenter()
            .size(24)
        nameLabel.pin.before(of: imageView)
            .marginRight(8)
            .vCenter()
            .sizeToFit()
    }
}
