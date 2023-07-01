//
//  Parser.swift
//  btccurrency
//
//  Created by fd on 2022/10/25.
//

import Foundation

struct Parser {
    public var lexer: Lexer
    public var currentToken: Token
    public var peekToken: Token
    public var errors: [ParseError]

    init(input: String) {
        let lexer = Lexer(input: input)
        self.init(lexer: lexer)
    }

    init(lexer: Lexer) {
        self.lexer = lexer
        currentToken = self.lexer.nextToken()
        peekToken = self.lexer.nextToken()
        errors = []
    }

    mutating func parse() -> Program {
        var statements = [Statement]()

        while currentToken.kind != .eof {
            if let statement = parseStatement() {
                statements.append(statement)
            }

            nextToken()
        }

        return Program(statements: statements)
    }
}

private extension Parser {
    mutating func nextToken() {
        currentToken = peekToken
        peekToken = lexer.nextToken()
    }

    mutating func parseStatement() -> Statement? {
        return parseExpressionStatement()
    }

    mutating func parseExpressionStatement() -> ExpressionStatement? {
        let token = currentToken
        let expression = parseExpression(precendence: .lowest)

        return expression.map { expression in
            ExpressionStatement(token: token, expression: expression)
        }
    }

    mutating func parseExpression(precendence: Precedence) -> Expression? {
        var expression: Expression?

        switch currentToken.kind {
        case .double:
            expression = parseDoubleExpression()
        case .minus:
            expression = parsePrefixExpression()
        case .lParen:
            expression = parseGroupedExpreession()
        default:
            return nil
        }

        while let left = expression, precendence < peekToken.kind.precedence {
            switch peekToken.kind {
            case .plus,
                 .minus,
                 .slash,
                 .asterisk:

                nextToken()
                expression = parseInfixExpression(left: left)

            default:
                return expression
            }
        }

        return expression
    }

    mutating func parseGroupedExpreession() -> Expression? {
        nextToken()

        let expression = parseExpression(precendence: .lowest)

        guard expectPeek(kind: .rParen) else {
            return nil
        }

        return expression
    }

    mutating func parseDoubleExpression() -> DoubleExpression? {
        let token = currentToken

        let textString = token.literal
        let newText = textString.split(separator: ",").joined()
        
        guard let value = Double(newText) else {
            errors.append(.illegalCharacterFoundInIntegerLiteral(token.literal))
            return nil
        }

        return DoubleExpression(token: token, value: value)
    }

    mutating func parsePrefixExpression() -> PrefixExpression? {
        let token = currentToken
        let `operator` = token.literal

        nextToken()

        guard let right = parseExpression(precendence: .prefix) else {
            errors.append(.prefixOperatorRightValueNotFound(operator: `operator`))
            return nil
        }

        return PrefixExpression(token: token, operator: `operator`, right: right)
    }

    mutating func parseInfixExpression(left: Expression) -> InfixExpression? {
        let token = currentToken
        let `operator` = token.literal
        let precedence = currentToken.kind.precedence

        nextToken()

        guard let right = parseExpression(precendence: precedence) else {
            errors.append(.infixOperatorRightValueNotFound(operator: `operator`))
            return nil
        }

        return InfixExpression(token: token, left: left, operator: `operator`, right: right)
    }

    mutating func expectPeek(kind: TokenKind) -> Bool {
        if peekToken.kind == kind {
            nextToken()
            return true
        } else {
            errors.append(.invalidTokenFound(expected: kind, got: peekToken.kind))
            return false
        }
    }
}

private extension TokenKind {
    var precedence: Precedence {
        switch self {
        case .plus, .minus:
            return .sum

        case .slash, .asterisk:
            return .product

        case .lParen:
            return .call

        default:
            return .lowest
        }
    }
}
