//
//  CurrencyChartInfoView.swift
//  cryptovise
//
//  Created by fd on 2022/10/21.
//

import UIKit
import SnapKit

class CurrencyChartInfoView: UIView {
    var maxItemView: CurrencyChartInfoItemView = {
        CurrencyChartInfoItemView().then { item in
            item.titleLabel.text = "High".localized()
            item.valueLabel.text = "--"
            item.layer.cornerRadius = 16
            item.layer.borderColor = UIColor.black.alpha(0.04).cgColor
            item.layer.borderWidth = 1
        }
    }()

    var minItemView: CurrencyChartInfoItemView = {
        CurrencyChartInfoItemView().then { item in
            item.titleLabel.text = "Low".localized()
            item.valueLabel.text = "--"
            item.layer.cornerRadius = 16
            item.layer.borderColor = UIColor.black.alpha(0.04).cgColor
            item.layer.borderWidth = 1
        }
    }()

    var avgItemView: CurrencyChartInfoItemView = {
        CurrencyChartInfoItemView().then { item in
            item.titleLabel.text = "Average".localized()
            item.valueLabel.text = "--"
            item.layer.cornerRadius = 16
            item.layer.borderColor = UIColor.black.alpha(0.04).cgColor
            item.layer.borderWidth = 1
        }
    }()

    var quoteChangeItemView: CurrencyChartInfoItemView = {
        CurrencyChartInfoItemView().then { item in
            item.titleLabel.text = "Change".localized()
            item.valueLabel.text = "--"
            item.layer.cornerRadius = 16
            item.layer.borderColor = UIColor.black.alpha(0.04).cgColor
            item.layer.borderWidth = 1
        }
    }()

    var infoLabel: UILabel = {
        UILabel().then { label in
            label.textColor = .basicBlk
            label.font = .robotoBold(with: 18)
            label.text = "Statistics".localized()
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
        updateConstraints()
    }
    
    override func updateConstraints() {
        super.updateConstraints()
        infoLabel.snp.makeConstraints { make in
            make.height.equalTo(24)
            make.left.equalToSuperview().offset(20)
            make.top.right.equalToSuperview()
        }
        maxItemView.snp.makeConstraints { make in
            make.left.equalTo(infoLabel)
            make.right.equalTo(self.snp.centerX).offset(-4)
            make.height.equalTo(56)
            make.top.equalTo(infoLabel.snp.bottom).offset(16)
        }
        minItemView.snp.makeConstraints { make in
            make.width.height.top.centerY.equalTo(maxItemView)
            make.left.equalTo(maxItemView.snp.right).offset(8)
        }
        avgItemView.snp.makeConstraints({ make in
            make.left.right.height.equalTo(maxItemView)
            make.top.equalTo(maxItemView.snp.bottom).offset(8)
        })
        quoteChangeItemView.snp.makeConstraints { make in
            make.left.right.height.equalTo(minItemView)
            make.top.equalTo(minItemView.snp.bottom).offset(8)
        }
    }

    override func sizeThatFits(_ size: CGSize) -> CGSize {
        return CGSize(width: UIDevice.kScreenWidth(), height: 100)
    }
}
