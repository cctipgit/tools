//
//  Evaluator.swift
//  cryptovise
//
//  Created by fd on 2022/10/25.
//

import Foundation
struct Evaluator {
    public var program: Program

    public init(program: Program) {
        self.program = program
    }

    public func evaluate() -> Value? {
        var result: Value?

        for statement in program.statements {
            result = evaluate(statement: statement)
            if result?.isError ?? false {
                break
            }
        }

        return result
    }
}

private extension Evaluator {
    func evaluate(statement: Statement) -> Value? {
        switch statement {
        case let statement as ExpressionStatement:
            return evaluate(expression: statement.expression)
        default:
            return nil
        }
    }

    func evaluate(expression: Expression) -> Value {
        switch expression {
        case let expression as DoubleExpression:
            return DoubleValue(value: expression.value)

        case let expression as PrefixExpression:
            return evaluatePrefix(expression: expression)

        case let expression as InfixExpression:
            return evaluateInfix(expression: expression)

        case let expression as CallExpression:
            return evaluateCall(expression: expression)

        default:
            return Null()
        }
    }

    func evaluatePrefix(expression: PrefixExpression) -> Value {
        let right = evaluate(expression: expression.right)

        guard !right.isError else {
            return right
        }

        switch (right, expression.token.kind) {
        case let (node as DoubleValue, .minus):
            return DoubleValue(value: -node.value)
        default:
            return CalulatorError.unknownPrefixOperator(operator: expression.operator, right: right.type)
        }
    }

    func evaluateInfix(expression: InfixExpression) -> Value {
        let left = evaluate(expression: expression.left)

        guard !left.isError else {
            return left
        }

        let right = evaluate(expression: expression.right)

        guard !right.isError else {
            return right
        }

        switch (left, right) {
        case let (left as DoubleValue, right as DoubleValue):
            return evaluateInfixInteger(left: left, right: right, expression: expression)

        default:
            return CalulatorError.unknownInfixOperator(left: left.type, operator: expression.operator, right: right.type)
        }
    }

    func evaluateInfixInteger(left: DoubleValue, right: DoubleValue, expression: InfixExpression) -> Value {
        switch expression.token.kind {
        case .plus:
            return DoubleValue(value: left.value + right.value)

        case .minus:
            return DoubleValue(value: left.value - right.value)

        case .asterisk:
            return DoubleValue(value: left.value * right.value)

        case .slash:
            if right.value == 0 {
                return DoubleValue(value: 0)
            }
            
            return DoubleValue(value: left.value / right.value)

        default:
            return CalulatorError.unknownInfixOperator(left: left.type, operator: expression.operator, right: right.type)
        }
    }

    func evaluateCall(expression: CallExpression) -> Value {
        let value = evaluate(expression: expression.function)

        guard !value.isError else {
            return value
        }

        switch value {
        case let builtin as Builtin:
            var arguments = [Value]()

            for argument in expression.arguments {
                let argument = evaluate(expression: argument)

                if argument.isError {
                    return argument
                }

                arguments.append(argument)
            }

            return builtin.function(arguments)

        default:
            return CalulatorError.callNonFunctionValue(type: value.type)
        }
    }
}
