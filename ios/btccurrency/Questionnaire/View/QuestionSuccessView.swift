//
//  QuestionSuccessView.swift
//  btccurrency
//
//  Created by admin on 2023/7/11.
//

import UIKit
import Then
import SnapKit

class QuestionSuccessView: UIView {
    private let bgAlphaView = UIView().then { v in
        v.backgroundColor = .basicBlk
        v.alpha = 0.3
    }
    
    private let contentBG = UIView().then { v in
        v.backgroundColor = .white
        v.layer.cornerRadius = 24
        v.clipsToBounds = true
    }
    
    public let closeBtn = UIButton(type: .custom).then { v in
        v.setImage(UIImage(named: "n_quiz_close"), for: .normal)
        v.setImage(UIImage(named: "n_quiz_close"), for: .highlighted)
        v.isUserInteractionEnabled = true
    }
    
    private let checkedImgView = UIImageView().then { v in
        v.image = UIImage(named: "n_checked")
    }
    
    private let titleLabel = UILabel().then { v in
        v.font = .robotoBold(with: 20)
        v.textAlignment = .center
        v.text = "Congratulations".localized()
        v.textColor = .basicBlk
    }
    
    private let descLabel = UILabel().then { v in
        v.font = .robotoRegular(with: 14)
        v.textAlignment = .left
        v.numberOfLines = 2
        v.text = "You were successfully completed all those questions.".localized()
        v.textColor = .basicBlk
    }
    
    public let doneBtn = UIButton(type: .custom).then { v in
        v.setTitle("Done".localized(), for: .normal)
        v.setTitle("Done".localized(), for: .highlighted)
        v.backgroundColor = .primaryBranding
        v.layer.cornerRadius = 8
        v.clipsToBounds = true
    }
    
    override init(frame: CGRect) {
        super.init(frame: CGRect(x: 0, y: 0, width: UIDevice.kScreenWidth(), height: UIDevice.kScreenHeight()))
        p_setElements()
        updateConstraints()
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    override func updateConstraints() {
        super.updateConstraints()
        bgAlphaView.snp.makeConstraints { make in
            make.edges.equalToSuperview()
        }
        contentBG.snp.makeConstraints { make in
            make.left.equalToSuperview().offset(20)
            make.right.equalToSuperview().offset(-20)
            make.centerY.equalToSuperview()
            make.height.equalTo(282)
        }
        closeBtn.snp.makeConstraints { make in
            make.size.equalTo(24)
            make.top.equalToSuperview().offset(16)
            make.right.equalToSuperview().offset(-20)
        }
        checkedImgView.snp.makeConstraints { make in
            make.size.equalTo(56)
            make.top.equalToSuperview().offset(60)
            make.centerX.equalToSuperview()
        }
        titleLabel.snp.makeConstraints { make in
            make.left.right.equalToSuperview()
            make.height.equalTo(28)
            make.top.equalTo(checkedImgView.snp.bottom).offset(16)
        }
        descLabel.snp.makeConstraints { make in
            make.left.equalToSuperview().offset(26)
            make.right.equalToSuperview().offset(-26)
            make.top.equalTo(titleLabel.snp.bottom).offset(8)
            make.height.greaterThanOrEqualTo(16)
        }
        doneBtn.snp.makeConstraints { make in
            make.height.equalTo(50)
            make.left.equalToSuperview().offset(16)
            make.right.equalToSuperview().offset(-16)
            make.centerX.equalToSuperview()
            make.bottom.equalToSuperview().offset(-24)
        }
    }
    
    // MARK: Private
    func p_setElements() {
        self.backgroundColor = UIColor.clear
        contentBG.addSubviews([closeBtn, checkedImgView, titleLabel, descLabel, doneBtn])
        self.addSubviews([bgAlphaView, contentBG])
    }
}
