//
//  File.swift
//  btccurrency
//
//  Created by fd on 2022/10/27.
//

import Foundation
extension String {
    static func == (lhs: String, rhs: String?) -> Bool {
        guard let rhs else {
            return false
        }

        return lhs == rhs
    }
}

extension Optional<Bool> {
    static func == (lhs: Bool, rhs: Bool?) -> Bool {
        guard let rhs else {
            return false
        }

        return lhs == rhs
    }
}

extension Optional {
    var isNone: Bool {
        switch self {
        case .none:
            return true
        case .some:
            return false
        }
    }

    var isSome: Bool {
        return !isNone
    }

    func or(_ default: Wrapped) -> Wrapped {
        return self ?? `default`
    }

    func or(else: @autoclosure () -> Wrapped) -> Wrapped {
        return self ?? `else`()
    }

    func or(else: () -> Wrapped) -> Wrapped {
        return self ?? `else`()
    }

    func and<B>(_ optional: B?) -> B? {
        guard self != nil else { return nil }
        return optional
    }

    func and<T>(then: (Wrapped) throws -> T?) rethrows -> T? {
        guard let unwrapped = self else { return nil }
        return try then(unwrapped)
    }

    func zip2<A>(with other: Optional<A>) -> (Wrapped, A)? {
        guard let first = self, let second = other else { return nil }
        return (first, second)
    }

    func zip3<A, B>(with other: Optional<A>, another: Optional<B>) -> (Wrapped, A, B)? {
        guard let first = self,
              let second = other,
              let third = another else { return nil }
        return (first, second, third)
    }

    func filter(_ predicate: (Wrapped) -> Bool) -> Wrapped? {
        guard let unwrapped = self,
              predicate(unwrapped) else { return nil }
        return self
    }

    func expect(_ message: String) -> Wrapped {
        guard let value = self else { fatalError(message) }
        return value
    }
}

extension Bool? {
    static func == (lhs:Bool?,rhs:Bool) -> Bool {
        guard let lhs else {
            return false
        }
        return lhs == rhs
    }
}
