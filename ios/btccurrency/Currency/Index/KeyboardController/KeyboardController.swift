//
//  KeyboardController.swift
//  btccurrency
//
//  Created by fd on 2022/10/26.
//

import Foundation


@objc protocol KeyboardControllerDelegate: AnyObject {
    func didTappedKeyboard()
}

class KeyboardController: NSObject {
    weak var valueScrollField: ScrollableTextField?
    weak var expressionScrollField: ScrollableTextField?
    weak var currentTextField: UITextField?

    weak var lastContentExpressionScrollField: ScrollableTextField?
    
    weak var delegate: KeyboardControllerDelegate?

    var numberFormatter: NumberFormatter = NumberFormatter().then {
        $0.maximumFractionDigits = 8
        $0.minimumFractionDigits = 0
    }

    var lastOperators: [String] = []
    var operators: [String] = []

    func bind(cell: HomeTableViewCell) {
        if !operators.isEmpty {
            lastOperators = operators
        }

        if lastContentExpressionScrollField == cell.expressionTextField {
            operators = lastOperators
            lastContentExpressionScrollField?.isHidden = false
        } else {
            operators.removeAll()
            lastContentExpressionScrollField?.isHidden = true
        }

        valueScrollField = cell.valueTextField
        expressionScrollField = cell.expressionTextField
        if cell.expressionInnerTextField.text.or("").isEmpty {
            currentTextField = cell.valueTextField.textField
        } else {
            currentTextField = cell.expressionInnerTextField
        }
    }

    func checkCurrentTextFieldContent() {
        if lastContentExpressionScrollField != expressionScrollField {
            lastOperators.removeAll()
            lastContentExpressionScrollField?.textField.text?.removeAll()
            lastContentExpressionScrollField?.textField.insert(text: "")

            lastContentExpressionScrollField = expressionScrollField
        }
    }

    @objc func handle(touched button: CalculatorButton) {
        guard let inputView = currentTextField?.inputView as? TextFieldInputView
        else { return }
        
        self.delegate?.didTappedKeyboard()

        inputView.button.sendActions(for: .allTouchEvents)

        checkCurrentTextFieldContent()

        switch button.key.rawValue {
        case CalculatorKey.zero.rawValue ... CalculatorKey.nine.rawValue:
            processNumber(with: button.key)

        case CalculatorKey.decimal.rawValue:
            processDecimal()

        case CalculatorKey.delete.rawValue:
            processDelete()
        case CalculatorKey.subtract.rawValue,
             CalculatorKey.multiply.rawValue,
             CalculatorKey.divide.rawValue,
             CalculatorKey.add.rawValue:

            processOperator(with: button.key)
        default:
            break
        }

        if !operators.isEmpty {
            var text = currentTextField?.text
            if lastCharIsOpeartor() {
                text?.removeLast()
            }
            let result = calculatorExpression(text: text ?? "")
            self.handleCalculate(result: result)
        }
    }
    
    func handleCalculate(result:Value?) {
        switch result {
        case let resultValue as DoubleValue:
            valueScrollField?.textField?.text?.removeAll()
            let value = numberFormatter.string(from: NSDecimalNumber(value: resultValue.value))
            valueScrollField?.textField?.insertText(value ?? String(resultValue.value))
//                valueScrollField?.textField.sendActions(for: .allEvents)
        default:
            break
        }
    }


    func processNumber(with key: CalculatorKey) {
        guard let text = currentTextField?.text
        else {
            return
        }

        if operators.isEmpty {
            if text == "0" {
                currentTextField?.text?.removeAll()
            }

            limitNumberCount(with: text, key: key)
        } else {
            if lastCharIsOpeartor() || text.isEmpty {
                currentTextField?.insertText(key.toString())
                return
            }

            guard let symbol = operators.last,
                    let operatorIndex = text.lastIndex(of: symbol.first!)
            else {
                return
            }

            let nextNumberIndex = text.index(after: operatorIndex)
            let subString = text[nextNumberIndex ..< text.endIndex]
            if subString == "0" {
                currentTextField?.deleteBackward()
                limitNumberCount(with: "", key: key)
            } else {
                limitNumberCount(with: String(subString), key: key)
            }
        }
    }

    func limitNumberCount(with string: String, key: CalculatorKey) {
        if let decimalIndex = string.lastIndex(of: ".") {
            let float = string[decimalIndex ..< string.endIndex]
            if float.count <= 8 {
                currentTextField?.insertText(key.toString())
            } else {
                currentTextField?.shakeHorizontal()
            }
        } else {
            if string.count <= 15 {
                currentTextField?.insertText(key.toString())
            } else {
                currentTextField?.shakeHorizontal()
            }
        }
    }

    func processDelete() {
        if currentTextField?.isEmpty ?? true {
            if operators.isEmpty && currentTextField == valueScrollField?.textField {
                currentTextField?.shakeHorizontal()
            }
            return
        }

        if lastCharIsOpeartor() {
            operators.removeLast()
            currentTextField?.deleteBackward()
            if lastCharIsRightParenthesis() {
                processDelete()
                return
            }
        } else if lastCharIsRightParenthesis() {
            let isStartWithLeftParenthesis = currentTextField?.text?.starts(with: "(")
            if isStartWithLeftParenthesis.or(false) {
                currentTextField?.text?.removeFirst()
            }
            currentTextField?.deleteBackward()
        } else {
            currentTextField?.deleteBackward()
        }

        if operators.isEmpty && currentTextField == expressionScrollField?.textField {
            switchCurrentTextInputView(endSwitchHandler: {
                self.expressionScrollField?.textField.text?.removeAll()
                self.expressionScrollField?.textField.insertText("")
            })
        }
    }

    func lastCharIsRightParenthesis() -> Bool {
        guard let text = currentTextField?.text else {
            return false
        }
        return text.ends(with: ")")
    }

    func processDecimal() {
        if operators.isEmpty {
            guard let currentTextField else {
                return
            }

            if currentTextField.isEmpty {
                currentTextField.insertText("0.")
                return
            }

            let isContainDecimal = currentTextField.text?.contains(CalculatorKey.decimal.toString())
            if isContainDecimal.or(false) {
                currentTextField.shakeHorizontal()
            } else {
                currentTextField.insertText(CalculatorKey.decimal.toString())
            }
        } else {
            if operators.isEmpty {
                return
            }

            if lastCharIsOpeartor() {
                currentTextField?.insertText("0.")
                return
            }

            let text = currentTextField?.text ?? ""

            guard let index = text.lastIndex(of: (operators.last?.first)!) else {
                return
            }

            if text[index ..< text.endIndex].contains(CalculatorKey.decimal.toString()) {
                currentTextField?.shakeHorizontal()
            } else {
                currentTextField?.insertText(CalculatorKey.decimal.toString())
            }
        }
    }

    func processOperator(with key: CalculatorKey) {
        if operators.isEmpty {
            switchCurrentTextInputView(beginSwitchHandler: {
                if self.expressionScrollField?.textField.text?.isEmpty ?? true {
                    self.expressionScrollField?.textField.insert(text: "")
                }
                guard let text = self.currentTextField?.text else {
                    return
                }

                if text.ends(with: CalculatorKey.decimal.toString()) {
                    self.currentTextField?.deleteBackward()
                }

                if text.isEmpty {
                    self.currentTextField?.insertText("0")
                }
            })
        }

        let endWithDecimal = currentTextField?.text?.ends(with: CalculatorKey.decimal.toString())
        if endWithDecimal.or(false) {
            currentTextField?.insert(text: "0")
        }

        if !lastCharIsOpeartor() {
            operators.append(key.toString())
            currentTextField?.insert(text: key.toString())
        } else {
            if operators.last == key.toString() {
                currentTextField?.shakeHorizontal()
            } else {
                operators.removeLast()
                operators.append(key.toString())
                currentTextField?.deleteBackward()
                currentTextField?.insert(text: key.toString())
            }
        }

       
    }

    func lastCharIsOpeartor() -> Bool {
        guard let text = currentTextField?.text else {
            return false
        }

        let hasSubstract = text.ends(with: CalculatorKey.subtract.toString())
        let hasAdd = text.ends(with: CalculatorKey.add.toString())
        let hasMultiply = text.ends(with: CalculatorKey.multiply.toString())
        let hasDivision = text.ends(with: CalculatorKey.divide.toString())
        if hasSubstract
            || hasAdd
            || hasDivision
            || hasMultiply {
            return true
        }
        return false
    }

    func switchCurrentTextInputView(beginSwitchHandler: (() -> Void)? = nil,
                                    endSwitchHandler: (() -> Void)? = nil) {
        beginSwitchHandler?()
        if currentTextField == valueScrollField?.textField {
            expressionScrollField?.textField.text?.removeAll()
            expressionScrollField?.textField.insert(text: currentTextField?.text)
            currentTextField = expressionScrollField?.textField

        } else {
            valueScrollField?.textField.text?.removeAll()
            valueScrollField?.textField.insert(text: currentTextField?.text)
            currentTextField = valueScrollField?.textField
        }
        popupKeyboard()

        endSwitchHandler?()
    }

    @objc func handleLongPressDelete(gestureRecognizer: UILongPressGestureRecognizer) {
        if gestureRecognizer.state == .began {
            guard let inputView = currentTextField?.inputView as? TextFieldInputView
            else { return }
            inputView.button.sendActions(for: .allTouchEvents)
            self.delegate?.didTappedKeyboard()

            UIImpactFeedbackGenerator(style: .light).impactOccurred()

            checkCurrentTextFieldContent()
            if currentTextField?.text?.isEmpty ?? false {
                currentTextField?.shakeHorizontal()
                return
            }
            currentTextField?.text?.removeAll()
            currentTextField?.insertText("")

            if !operators.isEmpty {
                operators.removeAll()
                switchCurrentTextInputView()
            }
        }
    }

    @objc func handleLongPressOperator(gestureRecognizer: UILongPressGestureRecognizer) {
        if gestureRecognizer.state != .began {
            return
        }
        guard let button = gestureRecognizer.view as? CalculatorButton else {
            return
        }

        guard let inputView = currentTextField?.inputView as? TextFieldInputView
        else { return }
        
        inputView.button.sendActions(for: .allTouchEvents)
        self.delegate?.didTappedKeyboard()
        
        let key = button.key

        UIImpactFeedbackGenerator(style: .light).impactOccurred()
        checkCurrentTextFieldContent()
        if operators.isEmpty {
            let lastIsDecimal = currentTextField?.text?.ends(with: ".") ?? false
            let isEmpty = currentTextField?.text?.isEmpty ?? false
            if isEmpty {
                currentTextField?.shakeHorizontal()
                return
            }

            switchCurrentTextInputView {
                if lastIsDecimal {
                    self.currentTextField?.deleteBackward()
                }
            } endSwitchHandler: {
                if self.currentTextField?.text == "0" {
                    self.currentTextField?.insertText(".0")
                    self.insertParenthesis(with: key)
                } else {
                    self.insertParenthesis(with: key)
                }
            }

        } else {
            if lastCharIsOpeartor() {
                if operators.last == key.toString() {
                    currentTextField?.shakeHorizontal()
                } else {
                    operators.removeLast()
                    currentTextField?.deleteBackward()

                    let endWithRightParenthesis = currentTextField?.text?.ends(with: ")")
                    if endWithRightParenthesis.or(false) {
                        currentTextField?.insertText(key.toString())
                        operators.append(key.toString())
                    } else {
                        insertParenthesis(with: key)
                    }
                }
            } else {
                insertParenthesis(with: key)
            }
        }
    }

    func insertParenthesis(with key: CalculatorKey) {
        let oldText = currentTextField?.text ?? ""
        currentTextField?.text?.removeAll()
        let newText = makeParenthesis(with: key, text: oldText)
        currentTextField?.insertText(newText)
        operators.append(key.toString())
    }

    func makeParenthesis(with key: CalculatorKey, text: String) -> String {
        var string = "(".appending(text)
        string.append(")")
        string.append(key.toString())
        return string
    }

    func popupKeyboard() {
        expressionScrollField?.isHidden = false
        UIView.performWithoutAnimation {
            currentTextField?.becomeFirstResponder()
        }
    }

    func popdownKeyboard() {
        expressionScrollField?.isHidden = true
    }

    func removeExpression() {
        expressionScrollField?.textField.text?.removeAll()
        operators.removeAll()
        currentTextField = valueScrollField?.textField
    }

    func clean() {
        removeExpression()
    }
}
