//
//  TokenKind.swift
//  cryptovise
//
//  Created by fd on 2022/10/25.
//

import Foundation
enum TokenKind: Hashable {
    /// Unknown token
    case illegal

    /// End of file
    case eof

    /// 1234567890
    case double

    /// +
    case plus

    /// -
    case minus

    /// *
    case asterisk

    /// /
    case slash

    /// (
    case lParen

    /// )
    case rParen
    /// ,
    case comma
}
