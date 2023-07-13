//
//  CalculatorKey.swift
//  xCurrency
//
//  Created by fd on 2022/10/18.
//

import Device
import Foundation

enum CalculatorKey: Int {
    case zero = 0
    case one
    case two
    case three
    case four
    case five
    case six
    case seven
    case eight
    case nine
    case decimal
    case delete
    case add
    case subtract
    case multiply
    case divide

    func toString() -> String {
        switch rawValue {
        case CalculatorKey.zero.rawValue ... CalculatorKey.nine.rawValue:
            return String(rawValue)
        case CalculatorKey.decimal.rawValue:
            return "."
        case CalculatorKey.multiply.rawValue:
            return "×"
        case CalculatorKey.add.rawValue:
            return "+"
        case CalculatorKey.subtract.rawValue:
            return "-"
        case CalculatorKey.divide.rawValue:
            return "÷"
        case CalculatorKey.zero.rawValue,
             CalculatorKey.one.rawValue,
             CalculatorKey.two.rawValue,
             CalculatorKey.three.rawValue,
             CalculatorKey.four.rawValue,
             CalculatorKey.five.rawValue,
             CalculatorKey.six.rawValue,
             CalculatorKey.seven.rawValue,
             CalculatorKey.eight.rawValue,
             CalculatorKey.nine.rawValue:

            return String(rawValue)

        default:
            return ""
        }
    }
}

func calculatorHeight() -> CGFloat {
    let window = getWindow()
    let safeAreaBottom = window?.pin.safeArea.bottom ?? 0

    let factor = screenWidthFactor()
    let innerSpace = 8 * factor

    return ceil(safeAreaBottom + innerSpace * 5 + 4 * calculatorItemSize())
}

func screenWidthFactor() -> CGFloat {
    return UIScreen.main.bounds.width / 375
}

func calculatorItemSize() -> CGFloat {
    let factor = screenWidthFactor()
    return 64 * factor
}

func calculatorExpression(text: String) -> Value? {
    
    var calculatorText = text
    if text.contains(",") {
        calculatorText.removeAll(where: {$0 == ","})
    }
    
    var parser = Parser(input: calculatorText)
    let program = parser.parse()
    let evaluator = Evaluator(program: program)
    return evaluator.evaluate()
}
