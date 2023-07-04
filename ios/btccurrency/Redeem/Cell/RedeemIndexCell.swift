//
//  RedeemIndexCell.swift
//  btccurrency
//
//  Created by admin on 2023/6/28.
//

import UIKit
import SnapKit
import Then
import Kingfisher

class RedeemIndexCell: UITableViewCell {
    var cellBGView = UIView().then { v in
        v.backgroundColor = .white
        v.layer.cornerRadius = 24
        v.clipsToBounds = true
    }
    
    var bannberImageView = UIImageView().then { v in
        v.layer.cornerRadius = 24
    }
    
    var titleLabel = UILabel().then { v in
        v.font = .robotoBold(with: 28)
        v.textColor = .white
        v.textAlignment = .center
    }
    
    var iconImageView = UIImageView().then { v in
        v.layer.cornerRadius = 44
        v.layer.borderWidth = 2
        v.layer.borderColor = UIColor.white.cgColor
    }
    
    var nameLabel = UILabel().then { v in
        v.font = .robotoRegular(with: 16)
        v.textColor = .basicBlk
    }

    var redeemBtn = UIButton().then { v in
        v.backgroundColor = .bgDisabled
        v.layer.cornerRadius = 4
        v.setTitle("Redeem".localized(), for: .normal)
    }
    
    var leftDesc = UILabel().then { v in
        v.font = .robotoRegular(with: 14)
        v.textColor = .primaryBranding
    }
    
    var rightDesc = UILabel().then { v in
        v.textAlignment = .right
    }
    
    var progressFullView = UIView().then { v in
        v.backgroundColor = .bgGroupBranding
        v.layer.cornerRadius = 4
    }
    
    var progressCurrentView = UIView().then { v in
        v.backgroundColor = .primaryBranding
        v.layer.cornerRadius = 4
    }
    
    // MARK: Super Method
    override init(style: UITableViewCell.CellStyle, reuseIdentifier: String?) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)
        p_setElements()
        updateConstraints()
        self.contentView.backgroundColor = .bgSecondary
    }

    required init?(coder: NSCoder) {
        super.init(coder: coder)
    }
    
    override func updateConstraints() {
        super.updateConstraints()
        cellBGView.snp.makeConstraints { make in
            make.left.equalToSuperview().offset(20)
            make.right.equalToSuperview().offset(-20)
            make.top.equalToSuperview()
            make.bottom.equalToSuperview().offset(-8)
        }
        bannberImageView.snp.makeConstraints { make in
            make.left.equalToSuperview().offset(20)
            make.right.equalToSuperview().offset(-20)
            make.top.equalToSuperview()
            make.height.equalTo(80)
        }
        titleLabel.snp.makeConstraints { make in
            make.edges.equalTo(bannberImageView)
        }
        iconImageView.snp.makeConstraints { make in
            make.size.equalTo(44)
            make.left.equalTo(bannberImageView).offset(16)
            make.top.equalTo(bannberImageView.snp.bottom).offset(-23)
        }
        nameLabel.snp.makeConstraints { make in
            make.left.equalTo(bannberImageView).offset(16)
            make.top.equalTo(iconImageView.snp.bottom).offset(6)
            make.height.equalTo(20)
            make.width.greaterThanOrEqualTo(40)
        }
        redeemBtn.snp.makeConstraints { make in
            make.width.equalTo(72)
            make.height.equalTo(28)
            make.right.equalTo(bannberImageView).offset(-16)
            make.centerY.equalTo(nameLabel)
        }
        leftDesc.snp.makeConstraints { make in
            make.height.equalTo(16)
            make.left.equalTo(nameLabel)
            make.top.equalTo(nameLabel.snp.bottom).offset(16)
            make.width.greaterThanOrEqualTo(112)
        }
        rightDesc.snp.makeConstraints { make in
            make.right.equalTo(redeemBtn)
            make.centerY.height.equalTo(leftDesc)
            make.width.greaterThanOrEqualTo(83)
        }
        progressFullView.snp.makeConstraints { make in
            make.height.equalTo(10)
            make.left.equalTo(leftDesc)
            make.right.equalTo(rightDesc)
            make.top.equalTo(leftDesc.snp.bottom).offset(8)
        }
        progressCurrentView.snp.makeConstraints { make in
            make.height.centerY.left.equalTo(progressFullView)
            make.width.equalTo(progressFullView).multipliedBy(0.5)
        }
    }
    
    // MARK: Private Method
    private func p_setElements() {
        contentView.addSubviews([cellBGView,
                                 bannberImageView,
                                 titleLabel,
                                 iconImageView,
                                 nameLabel,
                                 redeemBtn,
                                 leftDesc,
                                 rightDesc,
                                 progressFullView,
                                 progressCurrentView])
        self.backgroundColor = .backgroundColor
    }
    
    // MARK: Public Method
    public func setData(item: RedeemListItem, index: Int) {
        let imgs = ["n_green_banner", "n_yellow_banner"]
        let imgIndex = (index % 2 == 0) ? 0 : 1
        bannberImageView.kf.setImage(with: URL(string: item.pic), placeholder: UIImage(named: imgs[imgIndex]))
        titleLabel.text = "$\(item.reward) \(item.symbol)"
        iconImageView.kf.setImage(with: URL(string: item.ico), placeholder: UIImage(named: "n_usdt_icon"))
        nameLabel.text = item.id
        leftDesc.text = "Redeem for".localized() + " \("\(item.pointRequire)".decimalFormat())"
        rightDesc.text = "\("\(item.left)".decimalFormat()) / \("\(item.total)".decimalFormat())"
    }
}
