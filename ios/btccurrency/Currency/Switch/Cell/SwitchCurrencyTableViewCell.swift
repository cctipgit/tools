//
//  SwitchCurrencyTableViewCell.swift
//  btccurrency
//
//  Created by fd on 2022/10/21.
//

import UIKit
import SnapKit

class SwitchCurrencyTableViewCell: UITableViewCell {
    var flagIconView: UIImageView = UIImageView().then { imv in
        imv.layer.cornerRadius = 16
        imv.layer.masksToBounds = true
    }
    var titleLabel: UILabel = {
        UILabel().then { label in
            label.textColor = .basicBlk
            label.font = .robotoBold(with: 16)
        }
    }()

    var symbolLabel: UILabel = {
        UILabel().then { label in
            label.textColor = .contentSecondary
            label.font = .robotoRegular(with: 14)
        }
    }()

    var locationIcon: UIImageView = {
        UIImageView().then { imageView in
            imageView.image = UIImage(named: "location")
        }
    }()

    var selectedImageView: UIImageView = {
        UIImageView().then { imv in
            imv.image = UIImage(named: "n_checked")
        }
    }()

    var touchedBackgroundView: UIView = {
        UIView().then { view in
            view.backgroundColor = UIColor(red: 0.98, green: 0.98, blue: 0.98, alpha: 1)
            view.cornerRadius = 16
        }
    }()

    var isSearch:Bool = false

    override init(style: UITableViewCell.CellStyle, reuseIdentifier: String?) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)
        makeUI()
    }

    required init?(coder: NSCoder) {
        super.init(coder: coder)
        makeUI()
    }

    func makeUI() {
        contentView.addSubviews([touchedBackgroundView,
                                 flagIconView,
                                 titleLabel,
                                 symbolLabel,
                                 locationIcon,
                                 selectedImageView])

        selectionStyle = .none
        touchedBackgroundView.alpha = 0
        contentView.backgroundColor = .backgroundColor
    }

    func config(with item: SwitchCurrencyCellViewModel, isSearch: Bool) {
        self.isSearch = isSearch
        titleLabel.text = item.title
        symbolLabel.text = item.currency
        locationIcon.isHidden = !item.isShowLocation
        selectedImageView.isHidden = !item.isSelect
        
        flagIconView.loadImage(with: item.flag)
        if item.isSelect {
            titleLabel.textColor = .basicBlk
            symbolLabel.textColor = .contentSecondary
        } else {
            titleLabel.textColor = .basicBlk
            symbolLabel.textColor = .contentSecondary
        }
    }
    
    override func layoutSubviews() {
        super.layoutSubviews()
        touchedBackgroundView.snp.makeConstraints { make in
            make.left.top.bottom.equalToSuperview().offset(4)
            make.right.equalToSuperview().offset(-2)
        }
        flagIconView.snp.makeConstraints { make in
            make.size.equalTo(32)
            make.centerY.equalToSuperview()
            make.left.equalToSuperview().offset(16)
        }
        titleLabel.snp.makeConstraints { make in
            make.height.equalTo(20)
            make.left.equalTo(flagIconView.snp.right).offset(8)
            make.top.equalTo(flagIconView).offset(-1)
            make.width.greaterThanOrEqualTo(40)
        }
        symbolLabel.snp.makeConstraints { make in
            make.height.equalTo(16)
            make.top.equalTo(titleLabel.snp.bottom).offset(1)
            make.left.equalTo(titleLabel)
            make.width.greaterThanOrEqualTo(65)
        }
        locationIcon.snp.makeConstraints { make in
            make.size.equalTo(18)
            make.left.equalTo(titleLabel.snp.right).offset(8)
            make.centerY.equalTo(titleLabel)
        }
        selectedImageView.snp.makeConstraints { make in
            make.size.equalTo(24)
            make.centerY.equalToSuperview()
            make.right.equalToSuperview().offset(-16)
        }
    }

    override func setHighlighted(_ highlighted: Bool, animated: Bool) {
        super.setHighlighted(highlighted, animated: animated)
        if highlighted {
            touchedBackgroundView.alpha = 0
            touchedBackgroundView.isHidden = false
            UIView.animate(withDuration: 0.2) {
                self.touchedBackgroundView.alpha = 1
            }

        } else {
            touchedBackgroundView.alpha = 1
            UIView.animate(withDuration: 0.2) {
                self.touchedBackgroundView.alpha = 0
            } completion: { _ in
                self.touchedBackgroundView.isHidden = true
            }
        }
    }
}
