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
    
    func decimalFormat() -> String {
        let numberFormatter = NumberFormatter()
        numberFormatter.numberStyle = .decimal
        if let number = numberFormatter.number(from: self), let result = numberFormatter.string(from: number) {
            return result
        }
        return self
    }
}

extension TimeInterval {
    func customJoinTime() -> String {
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "MM/dd/yyyy"
        let date = Date(timeIntervalSince1970: self)
        let formattedString = dateFormatter.string(from: date)
        return "Member since " + formattedString
    }
    
    func customTaskLeftTime() -> String {
        let formatter = DateComponentsFormatter()
        formatter.allowedUnits = [.hour, .minute, .second]
        formatter.unitsStyle = .positional
        formatter.zeroFormattingBehavior = .pad
        if let resString = formatter.string(from: self) {
            let resArray = resString.components(separatedBy: ":")
            if resArray.count == 3 {
                return resArray[0] + "h:" + resArray[1] + "m:" + resArray[2] + "s"
            }
        }
        return "\(self)" + "s"
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
