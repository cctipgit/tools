//
//  Single+Cache.swift
//  binary
//
//  Created by fd on 2023/3/16.
//

import CleanJSON
import DataCache
import Foundation
import Moya
import RxSwift

extension PrimitiveSequence where Trait == SingleTrait, Element == Moya.Response {
    func storeCachedResponse<Target>(for target: Target)
        -> Single<Element>
        where Target: TargetType
    {
        return map { response -> Element in
            if response.statusCode == 200 {
                try? target.storeCachedResponse(response)
            }
            return response
        }
    }
}

extension PrimitiveSequence where Trait == SingleTrait, Element == Response {
    func mapObject<T: Codable>(_ type: T.Type) -> Single<T> {
        return map { try $0.mapObject(type) }
    }

    func mapCode() -> Single<Int> {
        return map { try $0.mapCode() }
    }

    func mapContainer<T: Codable>(_ type: T.Type) -> Single<Network.APIResponse<T>> {
        return map { try $0.mapContainer(type) }
    }
}

extension ObservableType where Element == Response {
    func mapObject<T: Codable>(_ type: T.Type) -> Observable<T> {
        return map { try $0.mapObject(type) }
    }

    func mapCode() -> Observable<Int> {
        return map { try $0.mapCode() }
    }

    func mapContainer<T: Codable>(_ type: T.Type) -> Observable<Network.APIResponse<T>> {
        return map { try $0.mapContainer(type) }
    }
}

extension Moya.Response {
    func mapObject<T: Codable>(_: T.Type) throws -> T {
        let decoder = CleanJSONDecoder()
        decoder.keyDecodingStrategy = .convertFromSnakeCase
        decoder.valueNotFoundDecodingStrategy = .custom(CustomJsonAdaptor())
        
        let response = try map(Network.APIResponse<T>.self, using: decoder)
        if response.success && statusCode == 200 {
            return response.data
        }
        if response.code != 0 {
            throw Network.Error.status(code: response.code, msg: response.msg)
        } else {
            let errorResponse = try map(Network.APIErrorResponse.self, using: CleanJSONDecoder())
            throw Network.Error.status(code: statusCode, msg: errorResponse.error + " " + errorResponse.path)
        }
    }

    func mapContainer<T: Codable>(_: T.Type) throws -> Network.APIResponse<T> {
        let decoder = CleanJSONDecoder()
        decoder.valueNotFoundDecodingStrategy = .custom(CustomJsonAdaptor())
        let response = try map(Network.APIResponse<T>.self, using: decoder)
        if response.success && statusCode == 200{
            return response
        }
        
        if response.code != 0 {
            throw Network.Error.status(code: response.code, msg: response.msg)
        } else {
            let errorResponse = try map(Network.APIErrorResponse.self, using: CleanJSONDecoder())
            throw Network.Error.status(code: statusCode, msg: errorResponse.error + " " + errorResponse.path)
        }
    }

    func mapCode() throws -> Int {
        return try map(Int.self, atKeyPath: "code")
    }

    func mapMessage() throws -> String {
        return try map(String.self, atKeyPath: "message")
    }
}
