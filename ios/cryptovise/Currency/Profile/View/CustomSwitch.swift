//
//  CustomSwitch.swift
//  cryptovise
//
//  Created by fd on 2022/11/3.
//

import Foundation
class CustomSwitch: UIControl {
    
    var bgView: UIView!
    var pointView: UIView!
    
    var isLocked = false
    var lockedHandler:(() -> Void)?
    
    var valueChangedHandler:((Bool) -> Void)?
    
    var isOn: Bool = false
    
    override init(frame: CGRect) {
    
        super.init(frame: frame)
        
        bgView = UIView()
        addSubview(bgView)
        bgView.layer.masksToBounds = true
        bgView.isUserInteractionEnabled = false
        
        pointView = UIView()
        addSubview(pointView)
        pointView.layer.masksToBounds = true
        pointView.isUserInteractionEnabled = false
        
        addTarget(self, action: #selector(stateChanges), for: .touchUpInside)
        borderColor = .primaryTextColor
        borderWidth = 1
        
    }
    
    override func layoutSubviews() {
    
        super.layoutSubviews()
        pointView.layer.cornerRadius = 9
        
        bgView.pin.all()
        
        if self.isOn {
            bgView.backgroundColor = .primaryTextColor
            pointView.backgroundColor = .primaryBlue
        } else {
            bgView.backgroundColor = .backgroundColor
            pointView.backgroundColor = .primaryTextColor
        }
        
        let pointWidth = 18.0
        if isOn {
            pointView.pin.vCenter()
                .size(pointWidth)
                .right(4)
        } else {
            pointView.pin.vCenter()
                .size(pointWidth)
                .left(4)
        }

    }
    
    required init?(coder aDecoder: NSCoder) {
    
        fatalError("init(coder:) has not been implemented")
    }
    
    
    @objc func stateChanges() {
        if self.isLocked {
            lockedHandler?()
            return
        }
        
        isOn = !isOn
        isOn ? changeToOnAnimation() : changeToOffAnimation()
        valueChangedHandler?(isOn)
    }
}

extension CustomSwitch {
    private func changeToOnAnimation() {
        UIView.animate(withDuration: 0.25, animations: {
            self.pointView.pin.right(4)
                .vertically(4)
            self.changeToOnPointViewStyle()
        })
    }
    
    
    private func changeToOffAnimation() {
        UIView.animate(withDuration: 0.25, animations: {
            self.pointView.pin.left(4)
                .vertically(4)
            self.changeToOffPointViewStyle()
        })
    }
    
    private func changeToOnPointViewStyle() {
        bgView.backgroundColor = .primaryTextColor
        pointView.backgroundColor = .primaryBlue
    }
    
    private func changeToOffPointViewStyle() {
        bgView.backgroundColor = .backgroundColor
        self.pointView.backgroundColor = .primaryTextColor
    }
}
