//
//  CurrencyChartInfoView.swift
//  btccurrency
//
//  Created by fd on 2022/10/21.
//

import UIKit

class CurrencyChartInfoView: UIView {
    var maxItemView: CurrencyChartInfoItemView = {
        CurrencyChartInfoItemView().then { item in
            item.titleLabel.text = "High".localized()
            item.valueLabel.text = "--"
        }
    }()

    var minItemView: CurrencyChartInfoItemView = {
        CurrencyChartInfoItemView().then { item in
            item.titleLabel.text = "Low".localized()
            item.valueLabel.text = "--"
        }
    }()

    var avgItemView: CurrencyChartInfoItemView = {
        CurrencyChartInfoItemView().then { item in
            item.titleLabel.text = "Average".localized()
            item.valueLabel.text = "--"
        }
    }()

    var quoteChangeItemView: CurrencyChartInfoItemView = {
        CurrencyChartInfoItemView().then { item in
            item.titleLabel.text = "Change".localized()
            item.valueLabel.text = "--"
        }
    }()

    var infoLabel: UILabel = {
        UILabel().then { label in
            label.textColor = .primaryTextColor
            label.font = .mediumPoppin(with: 18)
        }
    }()

    override init(frame: CGRect) {
        super.init(frame: frame)
        makeUI()
    }

    required init?(coder: NSCoder) {
        super.init(coder: coder)
        makeUI()
    }

    func makeUI() {
        addSubviews([infoLabel,
                     maxItemView,
                     minItemView,
                     avgItemView,
                     quoteChangeItemView])
    }

    override func layoutSubviews() {
        super.layoutSubviews()
        performLayout()
    }

    func performLayout() {
        infoLabel.pin.top().horizontally().margin(24)
            .sizeToFit()
        
        if self.width <= 0 {
            return
        }
        let width = (self.width - 24 * 2 - 10) / 2
        
        maxItemView.pin.below(of: infoLabel, aligned: .left)
            .marginTop(16).width(width).sizeToFit(.width)
        minItemView.pin.after(of: maxItemView, aligned: .top)
            .marginLeft(10).width(width).sizeToFit(.width)

        avgItemView.pin.below(of: maxItemView, aligned: .left)
            .marginTop(20).width(width).sizeToFit(.width)
        quoteChangeItemView.pin.after(of: avgItemView, aligned: .top)
            .marginLeft(10).width(width).sizeToFit(.width)
    }

    override func sizeThatFits(_ size: CGSize) -> CGSize {
        return autoSizeThatFits(size, layoutClosure: performLayout)
    }
}
