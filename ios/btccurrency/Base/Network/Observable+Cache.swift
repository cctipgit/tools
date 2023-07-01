//
//  Observable+Cache.swift
//  binary
//
//  Created by fd on 2023/3/16.
//

import Foundation
import Moya
import RxSwift
import CleanJSON

extension ObservableType where Element: TargetType {
    func request() -> Observable<Moya.Response> {
        return flatMap { target -> Observable<Moya.Response> in
            let source = target.request()
                .storeCachedResponse(for: target)
                .asObservable()
            if let response = try? target.cachedResponse() {
                return source.startWith(response)
            }

            return source
        }
    }
}
