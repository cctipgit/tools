//
//  Node.swift
//  cryptovise
//
//  Created by fd on 2022/10/25.
//

import Foundation
protocol Node: CustomStringConvertible {}

struct Program: Node {
    var statements: [Statement]

    var description: String {
        return statements.lazy
            .map { $0.description }
            .joined()
    }

    init(statements: [Statement]) {
        self.statements = statements
    }
}
