//
//  CustomAlertView.swift
//  cryptovise
//
//  Created by fd on 2022/11/3.
//

import UIKit

class CustomAlertView: UIView {
    var cancelButton = UIButton().then {
        $0.setTitle("Cancel".localized(), for: .normal)
        $0.setTitleColor(.primaryTextColor, for: .normal)
        $0.titleLabel?.font = .mediumPoppin(with: 16)
        $0.backgroundColor = .grayColor
        $0.cornerRadius = 16
    }

    var okButton = UIButton().then {
        $0.setTitle("Recovery".localized(), for: .normal)
        $0.setTitleColor(.primaryTextColor, for: .normal)
        $0.titleLabel?.font = .mediumPoppin(with: 16)
        $0.backgroundColor = .primaryBlue
        $0.cornerRadius = 16
    }

    var label = UILabel().then {
        $0.font = .mediumPoppin(with: 16)
        $0.textColor = .primaryTextColor
        $0.text = "Restore the default currency list?".localized()
        $0.numberOfLines = 0
    }

    var handler: (() -> Void)?

    override init(frame: CGRect) {
        super.init(frame: frame)

        addSubviews([label,
                     cancelButton,
                     okButton])

        okButton.addTarget(self, action: #selector(didTapped(ok:)), for: .touchUpInside)
        cancelButton.addTarget(self, action: #selector(didTapped(cancel:)), for: .touchUpInside)
        backgroundColor = .backgroundColor
        cornerRadius = 16
    }

    @objc func didTapped(ok button: UIButton) {
        handler?()
    }

    @objc func didTapped(cancel button: UIButton) {
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
        label.pin.top(40)
            .horizontally(40)
            .sizeToFit()

        let margin = 16.0
        cancelButton.pin.bottom(18)
            .left(margin)
            .width(148)
            .height(53)
            .marginTop(24)

        okButton.pin.after(of: cancelButton)
            .marginLeft(margin)
            .marginRight(margin)
            .bottom(18)
            .width(148)
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
            self.pin
                .width(343)
                .sizeToFit()
                .center()
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
