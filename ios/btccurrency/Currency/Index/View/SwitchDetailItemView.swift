//
//  SwitchDetailItemView.swift
//  btccurrency
//
//  Created by fd on 2022/10/28.
//

import UIKit

class SwitchDetailItemView: UIView {
    var imageView = {
        UIImageView().then { imageView in
            imageView.image = UIImage(named: "switch_detail")
        }
    }()

    var nameLabel = {
        UILabel().then { label in
            label.textColor = .primaryTextColor
            label.font = .semiboldPoppin(with: 16)
            label.text = "Rate Detail".localized()
        }
    }()

    override init(frame: CGRect) {
        super.init(frame: frame)
        backgroundColor = .primaryBlue
        addSubviews([imageView,nameLabel])
    }

    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    override func layoutSubviews() {
        super.layoutSubviews()
//        roundCorners([.topLeft, .bottomLeft], radius: 16)
        
        imageView.pin.size(24)
            .left(14)
            .vCenter()
        
        nameLabel.pin.sizeToFit()
            .after(of: imageView).marginLeft(8)
            .vCenter()
    }
}
