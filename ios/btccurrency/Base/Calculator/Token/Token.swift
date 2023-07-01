//
//  Token.swift
//  btccurrency
//
//  Created by fd on 2022/10/25.
//

import Foundation
struct Token: Hashable {
    public var kind: TokenKind
    public var literal: String

    public init(kind: TokenKind, literal: String) {
        self.kind = kind
        self.literal = literal
    }
}
