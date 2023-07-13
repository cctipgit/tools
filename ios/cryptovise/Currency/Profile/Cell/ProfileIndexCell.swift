//
//  ProfileIndexCell.swift
//  cryptovise
//
//  Created by admin on 2023/6/28.
//

import UIKit
import Then
import SnapKit

class ProfileIndexCell: UITableViewCell {

    var titleLabel = UILabel().then { label in
        label.font = .robotoBold(with: 18)
        label.textColor = .basicBlk
    }

    var arrowImageView = UIImageView().then { imv in
        imv.image = UIImage(named: "n_cell_access_right")
    }
    
    // MARK: Super Method
    override init(style: UITableViewCell.CellStyle, reuseIdentifier: String?) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)
        p_setElements()
        updateConstraints()
    }

    required init?(coder: NSCoder) {
        super.init(coder: coder)
    }
    
    override func updateConstraints() {
        super.updateConstraints()
        titleLabel.snp.makeConstraints { make in
            make.left.equalToSuperview().offset(20)
            make.bottom.equalToSuperview()
            make.top.equalToSuperview().offset(12)
            make.right.equalTo(arrowImageView.snp.left).offset(-20)
        }
        arrowImageView.snp.makeConstraints { make in
            make.size.equalTo(24)
            make.centerY.equalTo(titleLabel)
            make.right.equalToSuperview().offset(-20)
        }
    }
    
    // MARK: Private Method
    private func p_setElements() {
        contentView.addSubviews([titleLabel, arrowImageView])
        self.backgroundColor = .backgroundColor
    }
    
    // MARK: Public Method
    public func setData(title: String) {
        titleLabel.text = title
    }
}
