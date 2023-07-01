//
//  Precedence.swift
//  btccurrency
//
//  Created by fd on 2022/10/25.
//

import Foundation
enum Precedence: Int, Comparable {
    case lowest
    case sum
    case product
    case prefix
    case call
    case index

    static func < (lhs: Precedence, rhs: Precedence) -> Bool {
        return lhs.rawValue < rhs.rawValue
    }
}
