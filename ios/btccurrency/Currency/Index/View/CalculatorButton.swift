//
//  CalculatorButton.swift
//  xCurrency
//
//  Created by fd on 2022/10/18.
//

import Device
import UIKit

class CalculatorButton: UIButton {
    var key: CalculatorKey

    init(with key: CalculatorKey) {
        self.key = key
        super.init(frame: .zero)
        makeTitleAndImage()
        makeEvent()
    }

    func makeTitleAndImage() {
        let imageName = getImageName(with: key)
        setImage(UIImage(named: imageName), for: .normal)
        setImage(UIImage(named: imageName), for: .highlighted)

        makeBackgroundColor()
    }

    func makeBackgroundColor() {
        switch key {
        case .add, .subtract, .multiply, .divide:
            backgroundColor = .keyboardOpeartorColor
        default:
            backgroundColor = .keyboardNumberColor
        }
    }

    func makeEvent() {
        addTarget(self, action: #selector(didTouchDown(button:)), for: .touchDown)

        addTarget(self, action: #selector(didTouchOutside(button:)), for: .touchUpOutside)
        addTarget(self, action: #selector(didTouchOutside(button:)), for: .touchUpInside)
        addTarget(self, action: #selector(didTouchOutside(button:)), for: .touchCancel)
    }

    override func layoutSubviews() {
        super.layoutSubviews()
        cornerRadius = height / 2
        clipsToBounds = true
    }

    @objc func didTouchDown(button: CalculatorButton) {
        switch button.key {
        case .add, .subtract, .multiply, .divide:
            backgroundColor = .keyboardOpeartorPressedColor
        default:
            backgroundColor = .keyboardNumberPressedColor
        }
    }

    @objc func didTouchOutside(button: CalculatorButton) {
        makeBackgroundColor()
    }

    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    func getImageName(with key: CalculatorKey) -> String {
        return "pad_\(key.rawValue)"
    }
}
