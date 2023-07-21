//
//  SwitchCurrencySectionHeaderView.swift
//  cryptovise
//
//  Created by fd on 2022/10/29.
//

import UIKit
import SnapKit

class SwitchCurrencySectionHeaderView: UITableViewHeaderFooterView {
    let bgView = UIView().then { v in
        v.backgroundColor = UIColor(red: 0.98, green: 0.98, blue: 0.98, alpha: 1)
        v.layer.cornerRadius = 10
    }
    var titleLabel: UILabel = {
        UILabel().then { label in
            label.font = .robotoBold(with: 16)
            label.textColor = UIColor(red: 0.63, green: 0.63, blue: 0.67, alpha: 1)
        }
    }()

    override init(reuseIdentifier: String?) {
        super.init(reuseIdentifier: reuseIdentifier)
        makeUI()
    }

    required init?(coder: NSCoder) {
        super.init(coder: coder)
        makeUI()
    }

    func makeUI() {
        contentView.addSubview(bgView)
        contentView.addSubview(titleLabel)
        contentView.backgroundColor = .backgroundColor
        backgroundView = UIView().then {
            $0.backgroundColor = .backgroundColor
        }
    }

    override func layoutSubviews() {
        super.layoutSubviews()
        
        bgView.snp.makeConstraints { make in
            make.left.top.equalToSuperview()
            make.right.equalToSuperview().multipliedBy(0.7)
            make.height.equalTo(28)
        }
        titleLabel.snp.makeConstraints { make in
            make.left.equalToSuperview().offset(16)
            make.right.top.equalToSuperview()
            make.height.equalTo(28)
        }
    }
}
