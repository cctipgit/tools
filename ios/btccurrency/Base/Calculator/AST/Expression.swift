//
//  Expression.swift
//  btccurrency
//
//  Created by fd on 2022/10/25.
//

import Foundation

protocol Expression: Node {
    var token: Token { get }
}

struct DoubleExpression: Expression {
    public var token: Token
    public var value: Double

    public var description: String {
        return token.literal
    }

    public init(token: Token, value: Double) {
        self.token = token
        self.value = value
    }
}

struct PrefixExpression: Expression {
    var token: Token
    var `operator`: String
    var right: Expression

    var description: String {
        return "(\(self.operator)\(right.description))"
    }

    init(token: Token, operator: String, right: Expression) {
        self.token = token
        self.operator = `operator`
        self.right = right
    }
}

struct IndexExpression: Expression {
    var token: Token
    var left: Expression
    var index: Expression

    var description: String {
        return "(\(left.description)[\(index.description)])"
    }

    init(
        token: Token,
        left: Expression,
        index: Expression
    ) {
        self.token = token
        self.left = left
        self.index = index
    }
}

struct CallExpression: Expression {
    var token: Token
    var function: Expression
    var arguments: [Expression]

    var description: String {
        let arguments = self.arguments.lazy
            .map { $0.description }
            .joined(separator: ", ")
        return "\(function.description)(\(arguments))"
    }

    init(
        token: Token,
        function: Expression,
        arguments: [Expression]
    ) {
        self.token = token
        self.function = function
        self.arguments = arguments
    }
}

struct InfixExpression: Expression {
    var token: Token
    var left: Expression
    var `operator`: String
    var right: Expression

    var description: String {
        return "(\(left.description) \(self.operator) \(right.description))"
    }

    init(token: Token, left: Expression, operator: String, right: Expression) {
        self.token = token
        self.left = left
        self.operator = `operator`
        self.right = right
    }
}
