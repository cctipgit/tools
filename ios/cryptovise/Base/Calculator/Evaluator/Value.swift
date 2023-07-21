//
//  Value.swift
//  cryptovise
//
//  Created by fd on 2022/10/25.
//

import Foundation

protocol Value: CustomStringConvertible {}

extension Value {
    var type: Value.Type {
        return Swift.type(of: self)
    }

    var isError: Bool {
        return self is Error
    }
}

struct DoubleValue: Value, Hashable {
    var value: Double

    var description: String {
        return value.description
    }

    init(value: Double) {
        self.value = value
    }
}

struct Null: Value {
    var description: String {
        return "null"
    }

    init() {}
}

struct Builtin: Value {
    var function: ([Value]) -> Value

    var description: String {
        return "Builtin function"
    }
}

enum CalulatorError: Value {
    case unknownInfixOperator(left: Value.Type, operator: String, right: Value.Type)
    case unknownPrefixOperator(operator: String, right: Value.Type)
    case undefinedIdentifier(name: String)
    case callNonFunctionValue(type: Value.Type)
    case invalidArgument(type: Value.Type, functionName: String)
    case invalidNumberOfArguments(functionName: String, expected: Int, got: Int)
    case indexOperatorNotSupported(type: Value.Type)
    case invalidHashKey(type: Value.Type)

    var description: String {
        let detail: String

        switch self {
        case let .unknownInfixOperator(left, `operator`, right):
            detail = "unknown operator - \(left) \(`operator`) \(right)"

        case let .unknownPrefixOperator(`operator`, right):
            detail = "unknown operator - \(`operator`)\(right)"

        case let .undefinedIdentifier(name):
            detail = "undefined identifier - \(name)"

        case let .callNonFunctionValue(type):
            detail = "call non function value - \(type)"

        case let .invalidArgument(type, functionName):
            detail = "invalid argument - type: \(type), function name: \(functionName)"

        case let .invalidNumberOfArguments(functionName, expected, got):
            detail = "invalid number of arguments - function name: \(functionName), expected: \(expected), got: \(got)"

        case let .indexOperatorNotSupported(type):
            detail = "index operator not supported - type: \(type)"

        case let .invalidHashKey(type):
            detail = "invalid hash key - type: \(type)"
        }

        return "Error: \(detail)"
    }
}
