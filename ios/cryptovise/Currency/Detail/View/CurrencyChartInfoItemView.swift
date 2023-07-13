//
//  CurrencyChartInfoItemView.swift
//  cryptovise
//
//  Created by fd on 2022/10/21.
//


import UIKit
class CurrencyChartInfoItemView: UIView {
    var titleLabel: UILabel = {
        UILabel().then { label in
            label.textColor = .contentSecondary
            label.font = .robotoBold(with: 14)
        }
    }()

    var valueLabel: UILabel = {
        UILabel().then { label in
            label.textColor = .basicBlk
            label.font = .robotoRegular(with: 14)
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
        addSubviews([titleLabel, valueLabel])
    }

    override func layoutSubviews() {
        super.layoutSubviews()
        performLayout()
    }
    
    func performLayout() {
        titleLabel.pin.top(8).left(16).right().height(16)
            .sizeToFit()
        valueLabel.pin.below(of: titleLabel, aligned: .left)
            .marginTop(8)
            .horizontally()
            .height(16)
    }
    override func sizeThatFits(_ size: CGSize) -> CGSize {
        return autoSizeThatFits(size, layoutClosure: performLayout)
    }
}
