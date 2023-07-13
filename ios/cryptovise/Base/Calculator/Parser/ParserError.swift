//
//  ParserError.swift
//  cryptovise
//
//  Created by fd on 2022/10/25.
//

import Foundation
enum ParseError: Swift.Error, CustomStringConvertible {
    case invalidTokenFound(expected: TokenKind, got: TokenKind)
    case returnValueNotFound
    case prefixOperatorRightValueNotFound(operator: String)
    case infixOperatorRightValueNotFound(operator: String)
    case illegalCharacterFoundInIntegerLiteral(String)
    case illegalCharacterFoundInBooleanLiteral(String)

    public var description: String {
        switch self {
        case let .invalidTokenFound(expected, got):
            return "Invalid token is found. Expected to be: \(expected), got: \(got)"
        case .returnValueNotFound:
            return "Return value is not found after 'return'"

        case let .prefixOperatorRightValueNotFound(op):
            return "Right value is not found after prefix operator: \(op)"

        case let .infixOperatorRightValueNotFound(op):
            return "Right value is not found after infix operator: \(op)"

        case let .illegalCharacterFoundInIntegerLiteral(literal):
            return "Illegal character found in integer literal: \(literal)"

        case let .illegalCharacterFoundInBooleanLiteral(literal):
            return "Illegal character found in boolean literal: \(literal)"
        }
    }
}
