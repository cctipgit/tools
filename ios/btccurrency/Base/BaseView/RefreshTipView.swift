//
//  RefreshTipView.swift
//  btccurrency
//
//  Created by fd on 2022/11/3.
//

import UIKit

class RefreshTipView: UIView {

    var iconView = UIImageView().then{
        $0.image = UIImage(named: "refresh")
    }
    
    var tipLabel = UILabel().then {
        $0.font = .regularPoppin(with: 11)
        $0.textColor = .primaryTextColor.alpha(0.6)
    }
    
    override init(frame: CGRect) {
        super.init(frame: frame)
        addSubviews([iconView,
                    tipLabel])
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    override func layoutSubviews() {
        super.layoutSubviews()
        performLayout()
    }
    
    func performLayout() {
        iconView.pin.size(12)
            .left()
            .vCenter()
        
        tipLabel.pin.after(of: iconView)
            .marginLeft(4)
            .vCenter()
            .sizeToFit()
    }
    
    override func sizeThatFits(_ size: CGSize) -> CGSize {
        return autoSizeThatFits(size, layoutClosure: performLayout)
    }

}
