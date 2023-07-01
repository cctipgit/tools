//
//  Statement.swift
//  btccurrency
//
//  Created by fd on 2022/10/25.
//

import Foundation

protocol Statement: Node {
    var token: Token { get }
}

struct ExpressionStatement: Statement {
    public var token: Token
    public var expression: Expression

    public var description: String {
        return expression.description
    }

    public init(token: Token, expression: Expression) {
        self.token = token
        self.expression = expression
    }
}
