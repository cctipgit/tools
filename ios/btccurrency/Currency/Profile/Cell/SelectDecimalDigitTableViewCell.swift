//
//  SelectDecimalDigitTableViewCell.swift
//  btccurrency
//
//  Created by fd on 2022/11/3.
//

import UIKit

class SelectDecimalDigitTableViewCell: UITableViewCell {
    @IBOutlet var iconView: UIImageView!
    @IBOutlet var countLabel: UILabel!
    @IBOutlet var line: UIView!
    @IBOutlet var titleLabel: UILabel!
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
        iconView.image = UIImage(named: "n_checked")
        line.backgroundColor = .separatorColor
        backgroundColor = .backgroundColor
    }

    override func layoutSubviews() {
        super.layoutSubviews()
        titleLabel.pin.vCenter()
            .sizeToFit()
            .left(24)

        countLabel.pin.vCenter()
            .sizeToFit()
            .left(82)

        iconView.pin.right(24)
            .sizeToFit()
            .vCenter()

        line.pin.horizontally(24)
            .bottom()
            .height(0.5)
    }

    func makeSelect(isMatch: Bool) {
        iconView.isHidden = !isMatch
        if isMatch {
            titleLabel.textColor = .primaryBranding
            countLabel.textColor = .primaryBranding
        } else {
            countLabel.textColor = .contentSecondary
            titleLabel.textColor = .contentSecondary
        }
    }

    override func sizeThatFits(_ size: CGSize) -> CGSize {
        return CGSizeMake(width, 56)
    }
}
