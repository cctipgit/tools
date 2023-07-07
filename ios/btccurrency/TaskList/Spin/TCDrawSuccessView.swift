//
//  TCDrawSuccessView.swift
//  btccurrency
//
//  Created by admin on 2023/7/6.
//

import UIKit
import Then
import SnapKit

class TCDrawSuccessView: UIView {
    private let bgAlphaView = UIView().then { v in
        v.backgroundColor = .basicBlk
        v.alpha = 0.3
    }
    
    public let closeBtn = UIButton(type: .custom).then { v in
        v.setImage(UIImage(named: "n_close_alert"), for: .normal)
        v.setImage(UIImage(named: "n_close_alert"), for: .highlighted)
        v.isUserInteractionEnabled = true
    }
    
    private let contentBGClear = UIImageView().then { v in
        v.image = UIImage(named: "n_wheel_alert_bg")
        v.contentMode = .scaleAspectFill
        v.isUserInteractionEnabled = true
    }
    
    private let contentBG = UIView().then { v in
        v.backgroundColor = .white
        v.layer.cornerRadius = 24
        v.clipsToBounds = true
    }
    
    private let prizeIcon = UIImageView()
    private let prizeDesc = UILabel().then { v in
        v.textAlignment = .center
        v.font = .robotoRegular(with: 20)
        v.textColor = .basicBlk
    }
    public let prizeShareBtn = UIButton().then { v in
        v.setTitle("Share to friends".localized(), for: .normal)
        v.setTitle("Share to friends".localized(), for: .highlighted)
        v.backgroundColor = .primaryBranding
        v.layer.cornerRadius = 8
        v.clipsToBounds = true
    }
  
    private var iconName: String = ""
    private var desc: String = ""
    
    override init(frame: CGRect) {
        super.init(frame: frame)
        p_setElements()
        updateConstraints()
    }
    
    convenience init(iconName: String, desc: String) {
        self.init(frame: CGRect(x: 0, y: 0, width: UIDevice.kScreenWidth(), height: UIDevice.kScreenHeight()))
        self.backgroundColor = UIColor.clear
        self.iconName = iconName
        self.desc = desc
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
        closeBtn.snp.makeConstraints { make in
            make.size.equalTo(56)
            make.right.equalTo(contentBGClear)
            make.bottom.equalTo(contentBGClear.snp.top).offset(-8)
        }
        contentBGClear.snp.makeConstraints { make in
            make.centerY.equalToSuperview()
            make.left.equalToSuperview().offset(20)
            make.right.equalToSuperview().offset(-20)
            make.height.equalTo(284.0 * (UIDevice.kScreenWidth() - 40) / 387.0)
        }
        contentBG.snp.makeConstraints { make in
            make.left.equalTo(contentBGClear).offset(32)
            make.right.equalTo(contentBGClear).offset(-32)
            make.top.equalTo(contentBGClear).offset(60)
            make.bottom.equalTo(contentBGClear).offset(-2)
        }
        prizeIcon.snp.makeConstraints { make in
            make.size.equalTo(64)
            make.centerX.equalToSuperview()
            make.bottom.equalTo(prizeDesc.snp.top).offset(-2)
        }
        prizeDesc.snp.makeConstraints { make in
            make.left.right.equalToSuperview()
            make.height.equalTo(28)
            make.bottom.equalTo(prizeShareBtn.snp.top).offset(-24)
        }
        prizeShareBtn.snp.makeConstraints { make in
            make.left.equalToSuperview().offset(50)
            make.right.equalToSuperview().offset(-50)
            make.bottom.equalToSuperview().offset(-24)
            make.centerX.equalToSuperview()
            make.height.equalTo(50)
        }
    }
    
    // MARK: Private
    func p_setElements() {
        contentBGClear.addSubviews([prizeIcon, prizeDesc, prizeShareBtn])
        self.addSubviews([bgAlphaView, contentBG, closeBtn, contentBGClear])
        setData(iconName: iconName, desc: desc)
    }
    
    // MARK: Public
    func setData(iconName: String, desc: String) {
        self.prizeDesc.text = "You win".localized() + " " + desc
        self.prizeIcon.image = UIImage(named: iconName)
    }
}
