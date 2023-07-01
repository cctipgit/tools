//
//  EmptyView.swift
//  btccurrency
//
//  Created by fd on 2022/10/29.
//

import UIKit

class CurrencySearchEmptyView: UIView {
    var imageView: UIImageView = {
        UIImageView().then { imv in
            imv.image = UIImage(named: "empty_tip")
        }
    }()

    var titleLabel: UILabel = {
        UILabel().then { label in
            label.font = .mediumPoppin(with: 14)
            label.textColor = .textColorOf60Alpha
            label.text = "Currency not found".localized()
        }
    }()

    override init(frame: CGRect) {
        super.init(frame: frame)
        addSubviews([titleLabel, imageView])
    }

    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    override func layoutSubviews() {
        super.layoutSubviews()

        imageView.pin.top()
            .sizeToFit()
            .hCenter()

        titleLabel.pin.below(of: imageView)
            .marginTop(20)
            .horizontally()
            .sizeToFit()
    }
}
