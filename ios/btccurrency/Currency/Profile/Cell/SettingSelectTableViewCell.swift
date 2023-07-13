//
//  SettingSelectTableViewCell.swift
//  btccurrency
//
//  Created by fd on 2022/11/3.
//

import UIKit

class SettingSelectTableViewCell: UITableViewCell {
    @IBOutlet var lineView: UIView!
    @IBOutlet var selectedImageView: UIImageView!
    @IBOutlet var nameLabel: UILabel!

    override func awakeFromNib() {
        super.awakeFromNib()
        lineView.backgroundColor = .separatorColor
        nameLabel.textColor = .primaryTextColor.alpha(0.6)
        selectedBackgroundView = UIView().then({ view in
            view.backgroundColor = .grayColor
        })
        self.backgroundColor = .backgroundColor
    }

    override func layoutSubviews() {
        super.layoutSubviews()

        lineView.pin.horizontally(20)
            .bottom()
            .height(0.5)

        nameLabel.pin.vCenter()
            .left(20)
            .sizeToFit()

        selectedImageView.pin.vCenter()
            .right(20)
            .sizeToFit()

        selectedBackgroundView?.pin
            .vertically()
            .horizontally(8)
        selectedBackgroundView?.cornerRadius = 16
    }

    override func sizeThatFits(_ size: CGSize) -> CGSize {
        return CGSizeMake(width, 56)
    }
}
