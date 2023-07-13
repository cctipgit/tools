//
//  RedeemPointCell.swift
//  cryptovise
//
//  Created by admin on 2023/6/28.
//

import UIKit
import SnapKit
import Then

class RedeemPointCell: UITableViewCell {

    var titleLabel = UILabel().then { v in
        v.font = .robotoRegular(with: 16)
        v.textColor = .basicBlk
    }

    var timeLabel = UILabel().then { v in
        v.font = .robotoRegular(with: 14)
        v.textColor = .contentSecondary
    }
    
    var pointImgView = UIImageView().then { v in
        v.image = UIImage(named: "n_point_icon")
    }
    
    var pointLabel = UILabel().then { v in
        v.font = .robotoRegular(with: 16)
        v.textColor = .primaryYellow
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
            make.height.equalTo(20)
            make.left.equalToSuperview().offset(20)
            make.width.greaterThanOrEqualTo(117)
            make.top.equalToSuperview().offset(16)
        }

        timeLabel.snp.makeConstraints { make in
            make.left.equalTo(titleLabel)
            make.height.equalTo(16)
            make.top.equalTo(titleLabel.snp.bottom).offset(4)
            make.width.greaterThanOrEqualTo(55)
        }
        
        pointImgView.snp.makeConstraints { make in
            make.size.equalTo(20)
            make.centerY.equalTo(pointLabel)
            make.right.equalTo(pointLabel.snp.left).offset(-6)
        }
        
        pointLabel.snp.makeConstraints { make in
            make.height.equalTo(20)
            make.centerY.equalTo(titleLabel.snp.bottom).offset(2)
            make.width.greaterThanOrEqualTo(20)
            make.right.equalToSuperview().offset(-20)
        }
        
        lineView.snp.makeConstraints { make in
            make.left.equalToSuperview().offset(20)
            make.right.equalToSuperview().offset(-20)
            make.height.equalTo(0.5)
            make.bottom.equalToSuperview().offset(-4)
        }
    }
    
    // MARK: Private Method
    private func p_setElements() {
        contentView.addSubviews([titleLabel, timeLabel, pointImgView, pointLabel, lineView])
        self.backgroundColor = .backgroundColor
    }
    
    // MARK: Public Method
    public func setData(item: PointListItem) {
        titleLabel.text = item.title
        timeLabel.text = TimeInterval(item.time / 1000).customPointDetailTime()
        pointLabel.text = item.pointChange
    }
}
