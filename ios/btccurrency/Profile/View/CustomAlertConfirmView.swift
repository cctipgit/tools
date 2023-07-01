//
//  CustomAlertConfirmView.swift
//  btccurrency
//
//  Created by fd on 2022/11/29.
//

import UIKit

class CustomAlertConfirmView: UIView {
    var okButton = UIButton().then {
        $0.setTitle("OK".localized(), for: .normal)
        $0.setTitleColor(.primaryTextColor, for: .normal)
        $0.titleLabel?.font = .mediumPoppin(with: 16)
        $0.backgroundColor = .primaryBlue
        $0.cornerRadius = 16
    }
    
    var titleLabel = UILabel().then {
        $0.text = "Location Services".localized()
        $0.font = .mediumPoppin(with: 18)
        $0.textColor = .primaryTextColor
    }

    var label = UILabel().then {
        $0.font = .mediumPoppin(with: 16)
        $0.textColor = .primaryTextColor
        $0.text = "Please open \"Location services\" in the system settings to allow \"Currency\" to determine your location.".localized()
        $0.numberOfLines = 0
    }

    var handler: (() -> Void)?

    override init(frame: CGRect) {
        super.init(frame: frame)

        addSubviews([titleLabel,
                     label,
                     okButton])

        okButton.addTarget(self, action: #selector(didTapped(ok:)), for: .touchUpInside)
        backgroundColor = .backgroundColor
        cornerRadius = 16
    }

    @objc func didTapped(ok button: UIButton) {
        hide()
    }
    
    @objc func didTapped(cancel: UIButton) {
        hide()
    }

    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    override func layoutSubviews() {
        super.layoutSubviews()
        performLayout()
    }

    func performLayout() {
        
        titleLabel.pin.top(20)
            .hCenter()
            .sizeToFit()
        
        label.pin
            .below(of: titleLabel)
            .marginTop(15)
            .horizontally(20)
            .sizeToFit(.width)

        let margin = 16.0

        okButton.pin
            .below(of: label)
            .marginTop(15)
            .horizontally(margin)
            .marginBottom(15)
            .height(53)
    }

    override func sizeThatFits(_ size: CGSize) -> CGSize {
        return autoSizeThatFits(size, layoutClosure: performLayout)
    }

    func show(in view: UIView, handler: (() -> Void)? = nil) {
        alpha = 0
        let control = UIControl().then {
            $0.backgroundColor = .black.alpha(0.3)
            $0.addTarget(self, action: #selector(didTapped(cancel:)), for: .touchUpInside)
        }
        control.alpha = 0
        view.addSubview(control)
        control.addSubview(self)

        control.pin.all()
        pin.center()
        UIView.animate(withDuration: 0.35) {
            control.alpha = 1
            self.alpha = 1
            self.pin.center()
                .width(90%)
                .sizeToFit(.width)
        }
        self.handler = handler
    }

    func hide() {
        UIView.animate(withDuration: 0.35) {
            self.superview?.alpha = 0
            self.alpha = 0
        } completion: { _ in
            self.superview?.removeFromSuperview()
            self.removeFromSuperview()
        }
    }

}
