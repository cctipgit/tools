//
//  TextFieldInputView.swift
//  cryptovise
//
//  Created by fd on 2022/11/4.
//

import UIKit

class TextFieldInputView: UIView {
    var button = UIButton()

    override init(frame: CGRect) {
        super.init(frame: frame)
        addSubview(button)
        button.addTarget(self, action: #selector(didTapped(ok:)), for: .touchUpInside)
    }

    @objc func didTapped(ok: UIButton) {
        UIDevice.current.playInputClick()
    }

    required init?(coder: NSCoder) {
        super.init(coder: coder)
    }

    override func layoutSubviews() {
        super.layoutSubviews()
        button.pin.all()
    }
}

extension TextFieldInputView: UIInputViewAudioFeedback {
    var enableInputClicksWhenVisible: Bool {
        return AppSetting.shared.keyboardSound.value
    }
}
