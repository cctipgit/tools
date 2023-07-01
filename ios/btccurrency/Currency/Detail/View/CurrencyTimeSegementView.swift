//
//  CurrencyTimeSegementView.swift
//  btccurrency
//
//  Created by fd on 2022/10/29.
//

import BetterSegmentedControl
import UIKit

class CurrencyTimeSegementView: UIView {
    var backgroundView = {
        UIImageView(image: UIImage(named: "grid_background"))
    }()

    var segement = {
        BetterSegmentedControl(frame: .zero).then { control in
            control.setOptions([
                .cornerRadius(8.0),
                .animationSpringDamping(1.0),
                .indicatorViewBackgroundColor(.primaryTextColor)])
            control.backgroundColor = .backgroundColor
            control.layer.cornerRadius = 8
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
        addSubviews([backgroundView, segement])
        segement.segments = LabelSegment.segments(withTitles: ["1D", "1W", "1M", "1Y", "ALL"],
                                                  normalBackgroundColor: .backgroundColor,
                                                  normalFont: .regularPoppin(with: 14),
                                                  normalTextColor: .primaryTextColor,
                                                  selectedBackgroundColor: .segementViewSelectBackgroundColor,
                                                  selectedFont: .mediumPoppin(with: 14),
                                                  selectedTextColor: .segementViewSelectTitleColor)
    }

    override func layoutSubviews() {
        super.layoutSubviews()
        backgroundView.pin.all()
        segement.pin.horizontally(16).vCenter().height(43)
    }
}
