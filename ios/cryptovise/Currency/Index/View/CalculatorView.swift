//
//  CalculatorView.swift
//  xCurrency
//
//  Created by fd on 2022/10/18.
//

import UIKit

class CalculatorView: UIView {
    var zeroButton = CalculatorButton(with: .zero)
    var oneButton = CalculatorButton(with: .one)
    var twoButton = CalculatorButton(with: .two)
    var threeButton = CalculatorButton(with: .three)
    var fourButton = CalculatorButton(with: .four)
    var fiveButton = CalculatorButton(with: .five)
    var sixButton = CalculatorButton(with: .six)
    var sevenButton = CalculatorButton(with: .seven)
    var eightButton = CalculatorButton(with: .eight)
    var nineButton = CalculatorButton(with: .nine)

    var deleteButton = CalculatorButton(with: .delete)
    var multiplyButton = CalculatorButton(with: .multiply)
    var substractButton = CalculatorButton(with: .subtract)
    var addButton = CalculatorButton(with: .add)
    var divideButton = CalculatorButton(with: .divide)
    var decimalButton = CalculatorButton(with: .decimal)

    var calculatorButtons: [CalculatorButton] = []

    init() {
        super.init(frame: .zero)
        backgroundColor = .backgroundColor
        calculatorButtons.append(contentsOf: [zeroButton, oneButton,
                                              twoButton, threeButton,
                                              fourButton, fiveButton,
                                              sixButton, sevenButton,
                                              eightButton, nineButton,
                                              decimalButton, deleteButton,
                                              addButton, substractButton,
                                              multiplyButton, divideButton])

        let lineOne = [sevenButton,
                       eightButton,
                       nineButton,
                       divideButton]

        let lineTwo = [fourButton,
                       fiveButton,
                       sixButton,
                       multiplyButton]
        let lineThree = [oneButton,
                         twoButton,
                         threeButton,
                         substractButton]
        let lineFour = [decimalButton,
                        zeroButton,
                        deleteButton,
                        addButton]

        for line in [lineOne, lineTwo, lineThree, lineFour] {
            let view = UIView().then { view in
                view.backgroundColor = .backgroundColor
            }
            view.addSubviews(line)
            addSubview(view)
        }
    }

    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    override func layoutSubviews() {
        super.layoutSubviews()

        let factor = screenWidthFactor()
        let marginSpace = 30 * factor
        let innerSpace = 8 * factor
        let buttonSize = calculatorItemSize()

        let horizontaSpace = (width - (marginSpace * 2) - (4 * buttonSize)) / 3

        for (index, line) in subviews.enumerated() {
            let top = CGFloat(index) * buttonSize + (CGFloat(index) + 1) * innerSpace

            line.pin.horizontally(marginSpace)
                .height(buttonSize)
                .top(top)

            for (index, button) in line.subviews.enumerated() {
                button.pin.vertically()
                    .size(buttonSize)
                    .left(CGFloat(index) * (horizontaSpace + buttonSize))
            }
        }
    }
}
