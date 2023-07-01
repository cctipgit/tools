//
//  CurrencyChartInfoItemView.swift
//  btccurrency
//
//  Created by fd on 2022/10/21.
//


import UIKit
class CurrencyChartInfoItemView: UIView {
    var titleLabel: UILabel = {
        UILabel().then { label in
            label.textColor = .textColorOf60Alpha
            label.font = .regularPoppin(with: 16)
        }
    }()

    var valueLabel: UILabel = {
        UILabel().then { label in
            label.textColor = .primaryTextColor
            label.font = .mediumPoppin(with: 18)
        }
    }()

    init() {
        super.init(frame: .zero)
        makeUI()
    }

    required init?(coder: NSCoder) {
        super.init(coder: coder)
        makeUI()
    }

    func makeUI() {
        addSubviews([titleLabel,
                    valueLabel])
    }

    override func layoutSubviews() {
        super.layoutSubviews()
        performLayout()
    }
    
    func performLayout() {
        titleLabel.pin.top().left().right()
            .sizeToFit()
        valueLabel.pin.below(of: titleLabel,aligned: .left)
            .marginTop(4)
            .horizontally()
            .height(valueLabel.font.lineHeight)
    }
    override func sizeThatFits(_ size: CGSize) -> CGSize {
        return autoSizeThatFits(size, layoutClosure: performLayout)
    }
    
}
