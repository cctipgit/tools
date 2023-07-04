//
//  TaskIndexCell.swift
//  btccurrency
//
//  Created by admin on 2023/6/29.
//

import UIKit
import SnapKit
import Then

class TaskIndexCell: UITableViewCell {
 
    var titleLabel = UILabel().then { v in
        v.textColor = .basicBlk
        v.font = .robotoBold(with: 16)
    }
    
    var iconImageView = UIImageView().then { v in
        v.layer.cornerRadius = 11
        v.clipsToBounds = true
    }
    
    var descLabel = UILabel().then { v in
        v.font = .robotoRegular(with: 14)
        v.textColor = .primaryYellow
    }
    
    var statusBtn = UIButton(type: .custom).then { v in
        v.layer.cornerRadius = 4
        v.clipsToBounds = true
    }
    
    var lineView = UIView().then { v in
        v.backgroundColor = .separatorDefault
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
            make.left.equalToSuperview().offset(36)
            make.height.equalTo(20)
            make.top.equalToSuperview().offset(8)
            make.right.equalTo(statusBtn.snp.left).offset(-16)
        }
        
        iconImageView.snp.makeConstraints { make in
            make.size.equalTo(22)
            make.left.equalTo(titleLabel)
            make.top.equalTo(titleLabel.snp.bottom).offset(8)
        }
        
        descLabel.snp.makeConstraints { make in
            make.centerY.equalTo(iconImageView)
            make.left.equalTo(iconImageView.snp.right).offset(2)
            make.width.greaterThanOrEqualTo(66)
            make.height.equalTo(16)
        }
        
        statusBtn.snp.makeConstraints { make in
            make.width.equalTo(80)
            make.height.equalTo(28)
            make.right.equalToSuperview().offset(-36)
            make.top.equalTo(titleLabel)
        }
        
        lineView.snp.makeConstraints { make in
            make.left.equalTo(titleLabel)
            make.right.equalTo(statusBtn)
            make.height.equalTo(1)
            make.bottom.equalToSuperview().offset(-4)
        }
    }
    
    // MARK: Private Method
    private func p_setElements() {
        contentView.addSubviews([titleLabel, iconImageView, descLabel, statusBtn, lineView])
        self.backgroundColor = .white
        contentView.backgroundColor = .white
        statusBtn.titleLabel?.font = .robotoRegular(with: 14) ?? UIFont.systemFont(ofSize: 14)
        statusBtn.titleLabel?.textColor = .white
    }
    
    // MARK: Public Method
    public func setData(item: TaskListItem) {
        if item.done {
            statusBtn.isEnabled = false
            statusBtn.setTitle("Completed".localized(), for: .normal)
            statusBtn.setTitle("Completed".localized(), for: .disabled)
            statusBtn.backgroundColor = .contentDisabled
        } else {
            statusBtn.isEnabled = true
            statusBtn.setTitle("Go".localized(), for: .normal)
            statusBtn.setTitle("Go".localized(), for: .disabled)
            statusBtn.backgroundColor = .primaryBranding
        }
        titleLabel.text = item.taskName
        iconImageView.image = UIImage(named: "n_game_icon")
        descLabel.text = "+\(item.spinTimes) chance"
    }
}
