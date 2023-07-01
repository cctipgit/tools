//
//  Lexer.swift
//  btccurrency
//
//  Created by fd on 2022/10/25.
//

import Foundation
struct Lexer {
    var input: String
    var position: String.Index
    var readPosition: String.Index
    var character: Character?

    init(input: String) {
        self.input = input
        position = input.startIndex
        readPosition = input.startIndex >= input.endIndex ? input.endIndex : input.index(after: input.startIndex)
        character = input.first
    }

    mutating func nextToken() -> Token {
        skipWhiteSpace()

        guard let character = character else {
            return Token(kind: .eof, literal: "")
        }

        let kind: TokenKind

        switch character {
        case "+":
            kind = .plus

        case "-":
            kind = .minus

        case "÷":
            kind = .slash

        case "×":
            kind = .asterisk

        case "(":
            kind = .lParen

        case ")":
            kind = .rParen

        default:
            if character.isNumber {
                return Token(kind: .double, literal: readNumber())
            } else {
                kind = .illegal
            }
        }

        readChar()

        return Token(kind: kind, literal: String(character))
    }
}

private extension Lexer {
    mutating func readChar() {
        position = readPosition

        if readPosition >= input.endIndex {
            character = nil
        } else {
            character = input[readPosition]
            readPosition = input.index(after: readPosition)
        }
    }

    mutating func readNumber() -> String {
        let start = position

        while let character = character,
              character.isNumber
              || character == "."
              || character == "," {
            readChar()
        }

        return String(input[start ..< position])
    }

    mutating func skipWhiteSpace() {
        while let character = character,
              character.isWhitespace
                || character.isNewline
                || character == "," {
            readChar()
        }
    }
}
