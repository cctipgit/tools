//
//  CurrencyBottomSheetCell.swift
//  cryptovise
//
//  Created by admin on 2023/6/27.
//

import UIKit
import Then

class CurrencyBottomSheetCell: UITableViewCell {

    private let titleLabel = UILabel().then { lb in
        lb.font = .robotoLight(with: 16)
        lb.textColor = .contentSecondary
    }
    private let checkBoxView = UIImageView().then { v in
        v.image = UIImage(named: "n_circle_unchecked")
    }
    
    private let bgView = UIView().then { v in
        v.backgroundColor = UIColor(red: 0.98, green: 0.98, blue: 0.98, alpha: 0.98)
        v.layer.cornerRadius = 12
        v.isHidden = true
    }
    
    // MARK: Super Method
    override init(style: UITableViewCell.CellStyle, reuseIdentifier: String?) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)
        self.accessoryType = .none
        p_setElements()
        updateConstraints()
    }
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    override func updateConstraints() {
        super.updateConstraints()
        titleLabel.snp.makeConstraints { make in
            make.left.equalToSuperview().offset(16)
            make.top.bottom.equalToSuperview()
            make.right.equalTo(checkBoxView.snp.left).offset(-16)
        }
        checkBoxView.snp.makeConstraints { make in
            make.width.height.equalTo(20)
            make.centerY.equalToSuperview()
            make.right.equalToSuperview().offset(-16)
        }
        bgView.snp.makeConstraints { make in
            make.edges.equalToSuperview()
        }
    }
    
    // MARK: Private Method
    private func p_setElements() {
        [bgView, titleLabel, checkBoxView].forEach { v in
            contentView.addSubview(v)
        }
    }
    
    // MARK: Public Method
    public func setData(title: String) {
        titleLabel.text = title
    }
    
    public func setSelected(isSelected: Bool) {
        checkBoxView.image = isSelected ? UIImage(named: "n_circle_checked") : UIImage(named: "n_circle_unchecked")
        bgView.isHidden = !isSelected
        titleLabel.font = isSelected ? .robotoBold(with: 16) : .robotoLight(with: 16)
        titleLabel.textColor = isSelected ? .basicBlk : .contentSecondary
    }
}
