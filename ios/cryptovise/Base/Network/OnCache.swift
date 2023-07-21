//
//  OnCache.swift
//  binary
//
//  Created by fd on 2023/3/16.
//

import DataCache
import Foundation
import Moya
import RxSwift

struct OnCache<Target: TargetType, C: Codable> {
    let target: Target
    let keyPath: String?
    let decoder: JSONDecoder

    init(target: Target, keyPath: String?, decoder: JSONDecoder) {
        self.target = target
        self.keyPath = keyPath
        self.decoder = decoder
    }

    func request() -> Single<C> {
        return target.request()
            .storeCachedResponse(for: target)
            .map(C.self, atKeyPath: keyPath, using: decoder)
    }
}
