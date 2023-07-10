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
        backgroundView.isHidden = true
        addSubviews([backgroundView, segement])
        segement.segments = LabelSegment.segments(withTitles: ["1D", "1W", "1M", "1Y"],
                                                  normalBackgroundColor: .bgSecondary,
                                                  normalFont: .robotoBold(with: 14),
                                                  normalTextColor: .contentSecondary,
                                                  selectedBackgroundColor: .primaryBranding,
                                                  selectedFont: .robotoBold(with: 14),
                                                  selectedTextColor: .white)
    }

    override func layoutSubviews() {
        super.layoutSubviews()
        backgroundView.pin.all()
        segement.pin.horizontally(77).vCenter().height(24)
    }
}
