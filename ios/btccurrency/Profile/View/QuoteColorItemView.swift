//
//  QuoteColorItemView.swift
//  btccurrency
//
//  Created by fd on 2022/11/3.
//

import UIKit
import SnapKit

class QuoteColorItemView: UIView {
    let button = UIButton()

    let bgView = UIView().then {
        $0.backgroundColor = .bgSecondary
        $0.cornerRadius = 16
    }

    let imageView1 = UIImageView()
    let imageView2 = UIImageView()
    
    let iconView = UIImageView()

    let label = UILabel().then {
        $0.font = .robotoRegular(with: 16)
        $0.textColor = .contentSecondary
        $0.numberOfLines = 0
        $0.textAlignment = .left
    }

    var selected: Bool = false {
        didSet {
            if selected {
                iconView.image = UIImage(named: "n_circle_checked")
            } else {
                iconView.image = UIImage(named: "n_circle_unchecked")
            }
        }
    }

    override init(frame: CGRect) {
        super.init(frame: frame)
        makeUI()
    }

    required init?(coder: NSCoder) {
        super.init(coder: coder)
        makeUI()
    }

    func makeUI() {
        let subViews = [bgView, imageView1, imageView2, iconView, label, button]
        
        addSubviews(subViews)
        updateConstraints()
        subViews.forEach({ $0.isUserInteractionEnabled = true })
    }

    override func updateConstraints() {
        super.updateConstraints()
        bgView.snp.makeConstraints { make in
            make.edges.equalToSuperview()
        }
        imageView1.snp.makeConstraints { make in
            make.left.equalToSuperview().offset(8)
            make.right.equalToSuperview().offset(-8)
            make.top.equalToSuperview().offset(16)
            make.height.greaterThanOrEqualTo(40)
        }
        imageView2.snp.makeConstraints { make in
            make.left.right.equalTo(imageView1)
            make.top.equalTo(imageView1.snp.bottom).offset(8)
            make.height.greaterThanOrEqualTo(40)
        }
        iconView.snp.makeConstraints { make in
            make.size.equalTo(24)
            make.left.equalToSuperview().offset(18)
            make.top.equalTo(imageView2.snp.bottom).offset(16)
        }
        label.snp.makeConstraints { make in
            make.height.equalTo(20)
            make.left.equalTo(iconView.snp.right).offset(8)
            make.centerY.equalTo(iconView)
            make.right.equalToSuperview()
        }
        button.snp.makeConstraints { make in
            make.edges.equalToSuperview()
        }
    }
}
