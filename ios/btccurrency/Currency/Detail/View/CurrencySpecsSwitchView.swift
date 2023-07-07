//
//  CurrencySpecsSwitchView.swift
//  btccurrency
//
//  Created by fd on 2022/10/21.
//

import FluentDarkModeKit
import UIKit

class CurrencySpecsSwitchView: UIView {
    var forwardButton = {
        UIButton().then {
            $0.setImage(UIImage(named: "n_forward_icon"), for: .normal)
        }
    }()

    var currencyItem = CurrencySpecsSwitchItemView().then { item in
        item.isPureCurrencySpecItem = true
        item.selectIconView.isHidden = true
    }

    var switchItem = CurrencySpecsSwitchItemView().then { item in
        item.isPureCurrencySpecItem = false
        item.selectIconView.isHidden = false
    }

    override init(frame: CGRect) {
        super.init(frame: frame)
        borderWidth = 0.5
        borderColor = .b6cefeColorOf20Alpha
        if DMTraitCollection.override.userInterfaceStyle == .dark {
            backgroundColor = .bgSecondary
        } else {
            backgroundColor = .bgSecondary
        }
        makeUI()
    }

    override func traitCollectionDidChange(_ previousTraitCollection: UITraitCollection?) {
        if DMTraitCollection.override.userInterfaceStyle == .dark {
            backgroundColor = .backgroundColor
        } else {
            backgroundColor = .b6cefeColorOf20Alpha
        }
    }

    func makeUI() {
        addSubviews([currencyItem,
                     forwardButton,
                     switchItem])
        performLayout()
    }

    required init?(coder: NSCoder) {
        super.init(coder: coder)
    }

    func changeCurrentSpecOrder(_ isReverse: Bool) {
        switchItem.selectIconView.isHidden = isReverse
        currencyItem.selectIconView.isHidden = !isReverse

        switchItem.makeConstraint()
        currencyItem.makeConstraint()
    }

    override func layoutSubviews() {
        super.layoutSubviews()

        cornerRadius = height / 2
    }

    func performLayout() {
        let currencySize = currencyItem.systemLayoutSizeFitting(UIView.layoutFittingCompressedSize)
        let switchSize = switchItem.systemLayoutSizeFitting(UIView.layoutFittingCompressedSize)

        var leftMargin: CGFloat = 12
        var rightMargin: CGFloat = 12
        if currencySize.width > switchSize.width {
            rightMargin += currencySize.width - switchSize.width
        } else {
            leftMargin += switchSize.width - currencySize.width
        }

        translatesAutoresizingMaskIntoConstraints = false
        currencyItem.snp.remakeConstraints {
            $0.right.equalTo(forwardButton.snp.left).offset(-12)
            $0.centerY.equalToSuperview()
            $0.left.equalToSuperview().offset(leftMargin)
            $0.height.equalTo(24)
        }

        forwardButton.snp.remakeConstraints {
            $0.size.equalTo(20)
            $0.top.bottom.equalToSuperview().inset(12)
            $0.centerX.equalToSuperview()
        }

        switchItem.snp.remakeConstraints {
            $0.left.equalTo(forwardButton.snp.right).offset(12)
            $0.centerY.equalToSuperview()
            $0.right.equalToSuperview().inset(rightMargin)
            $0.height.equalTo(24)
        }
    }
}
