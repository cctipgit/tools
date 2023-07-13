//
//  TargetType+Cache.swift
//  binary
//
//  Created by fd on 2023/3/16.
//

import Foundation

import DataCache
import Moya
import RxSwift

extension Moya.TargetType {
    func onCache<C: Codable>(
        _ type: C.Type,
        atKeyPath keypath: String? = nil,
        using decoder: JSONDecoder = .init(),
        _ closure: (C) -> Void) -> OnCache<Self, C> {
        if let object = try? cachedResponse()
            .map(type, atKeyPath: keypath, using: decoder) {
            closure(object)
        }
        return OnCache(target: self, keyPath: keypath, decoder: decoder)
    }

    var cache: Observable<Self> {
        return Observable.just(self)
    }

    func cachedResponse() throws -> Moya.Response {
        if let response = CacheManager.shared.cachedResponse(self) {
            return response
        }

        throw MoyaError.statusCode(Response(statusCode: 100, data: Data()))
    }

    func storeCachedResponse(_ cachedResponse: Moya.Response) throws {
        if cachedResponse.statusCode == 200 {
            CacheManager.shared.storeCachedResponse(cachedResponse, target: self)
        }
    }

    func removeCachedResponse() throws {
        CacheManager.shared.removeCachedResponse(for: self)
    }
}
