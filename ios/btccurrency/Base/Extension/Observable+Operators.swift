//
//  Observable+Operators.swift
//  btccurrency
//
//  Created by fd on 2022/11/3.
//

import Foundation
import RxCocoa
import RxSwift

extension SharedSequenceConvertibleType {
    func mapToVoid() -> SharedSequence<SharingStrategy, Void> {
        return map { _ in }
    }
}

extension ObservableType {
    func asDriverOnErrorJustComplete() -> Driver<Element> {
        return asDriver { error in
            assertionFailure("Error \(error)")
            return Driver.empty()
        }
    }

    func mapToVoid() -> Observable<Void> {
        return map { _ in }
    }

    func ignoreWhen(_ predicate: @escaping (Element) throws -> Bool) -> Observable<Element> {
        return asObservable().filter { try !predicate($0) }
    }

    public func unwrap<Result>() -> Observable<Result> where Element == Result? {
        return compactMap { $0 }
    }
}

extension ObservableType where Element: Equatable {
    public func ignore(_ valuesToIgnore: Element...) -> Observable<Element> {
        return asObservable().filter { !valuesToIgnore.contains($0) }
    }

    public func ignore<Sequence: Swift.Sequence>(_ valuesToIgnore: Sequence) -> Observable<Element> where Sequence.Element == Element {
        return asObservable().filter { !valuesToIgnore.contains($0) }
    }
}

extension ObservableType where Element == Bool {
    func not() -> Observable<Bool> {
        return map(!)
    }
}
