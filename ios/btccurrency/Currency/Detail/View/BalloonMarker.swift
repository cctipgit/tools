//
//  BalloonMarker.swift
//  btccurrency
//
//  Created by fd on 2022/11/20.
//

import Charts
import Foundation
class BalloonMarker: UIView {
    var textLabel: UILabel = UILabel().then { label in
        label.font = .mediumPoppin(with: 16)
        label.textAlignment = .center
        label.textColor = .secondaryTextColor
    }

    var dateLabel: UILabel = UILabel().then { label in
        label.font = .mediumPoppin(with: 12)
        label.textAlignment = .center
        label.textColor = .secondaryTextColor
    }

    var contentView = UIView()

    override init(frame: CGRect) {
        super.init(frame: frame)
        addSubview(contentView)

        contentView.addSubview(textLabel)
        contentView.addSubview(dateLabel)

        contentView.backgroundColor = .backgroundColor
        backgroundColor = .backgroundColor
    }

    required init?(coder: NSCoder) {
        super.init(coder: coder)
    }

    override func layoutSubviews() {
        super.layoutSubviews()
        performLayout()
    }

    func performLayout() {
        contentView.pin
            .horizontally(4)
            .vertically(4)
            .wrapContent()

        textLabel.pin
            .hCenter()
            .top()
            .sizeToFit()

        dateLabel.pin
            .hCenter()
            .below(of: textLabel)
            .sizeToFit()
            .bottom()
    }

    override func sizeThatFits(_ size: CGSize) -> CGSize {
        textLabel.sizeToFit()
        dateLabel.sizeToFit()
        let width = textLabel.width > dateLabel.width ? textLabel.width : dateLabel.width
        return CGSizeMake(width + 8, textLabel.height + dateLabel.height + 8)
    }
}
