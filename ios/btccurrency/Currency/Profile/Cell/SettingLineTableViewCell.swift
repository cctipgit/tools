//
//  SettingLineTableViewCell.swift
//  btccurrency
//
//  Created by fd on 2022/11/3.
//

import UIKit

class SettingLineTableViewCell: UITableViewCell {
    var line = UIView().then { view in
        view.backgroundColor = .primaryTextColor.alpha(0.1)
    }

    override init(style: UITableViewCell.CellStyle, reuseIdentifier: String?) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)
        contentView.addSubview(line)
        selectionStyle = .none
        contentView.backgroundColor = .backgroundColor
        self.backgroundColor = .backgroundColor
    }

    required init?(coder: NSCoder) {
        super.init(coder: coder)
    }

    override func layoutSubviews() {
        super.layoutSubviews()
        line.pin.horizontally(20).height(0.5).vCenter()
    }

    override func sizeThatFits(_ size: CGSize) -> CGSize {
        return CGSizeMake(width, 8)
    }
}
