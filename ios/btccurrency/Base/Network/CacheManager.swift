//
//  CacheManager.swift
//  binary
//
//  Created by fd on 2023/3/16.
//

import CommonCrypto
import DataCache
import Foundation
import Moya

class CacheManager {
    let cache = DataCache(name: "tcurrency")

    static let shared = CacheManager()

    private init() {
        cache.maxCachePeriodInSecond = 7 * 86400 // 1 week
        cache.maxDiskCacheSize = 1000 * 1024 * 1024 // 100 MB
    }

    func clean() {
        cache.cleanAll()
    }

    func cachedResponse(_ target: TargetType) -> Response? {
        if let data = cache.readData(forKey: target.cachedKey) {
            return Response(statusCode: 200, data: data)
        }
        return nil
    }

    func storeCachedResponse(_ response: Response, target: TargetType) {
        let data = response.data
        cache.write(data: data, forKey: target.cachedKey)
    }

    func removeCachedResponse(for target: TargetType) {
        cache.clean(byKey: target.cachedKey)
    }
}
