//
//  SocketProvider+Extension.swift
//  cryptovise
//
//  Created by fd on 2023/1/8.
//

import Foundation
import RxSwift
import SocketTask

extension SocketProvider: ReactiveCompatible {}

extension Reactive where Base: SocketProviderType {
    func request(_ target: Base.Target,
                             option: SocketRequestOption = .keepOption) -> Observable<SocketResponse> {
        
        Observable.create { [weak base] observer in

            let cancellableToken = base?.request(target,
                                                 option: option,
                                                 completion: { (result: Result<SocketResponse, Error>) in
                                                     switch result {
                                                     case let .success(response):
                                                         observer.onNext(response)
                                                     case let .failure(error):
                                                         observer.onError(error)
                                                     }
                                                 })

            return Disposables.create {
                cancellableToken?.cancel()
            }
        }
    }
}
