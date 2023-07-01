//
//  PluginType.swift
//  SocketTask
//
//  Created by fd on 2022/12/28.
//

import Foundation

public extension SocketResponse {
    func mapJSON(failsOnEmptyData: Bool = true) throws -> Any {
        if let data {
            do {
                return try JSONSerialization.jsonObject(with: data, options: .allowFragments)
            } catch {
                if data.isEmpty && !failsOnEmptyData {
                    return NSNull()
                }
                throw error
            }
        }
        return NSNull()
    }

    func map<M: Message>(_ type: M.Type, failsOnEmpty: Bool) -> M? {
        if data == nil || data!.isEmpty {
            return M()
        }
        return try? M(serializedData: data!)
    }
}

public protocol PluginType {
    func willSend(_ target: some TargetType)

    func willReceive(_ result: Result<SocketResponse, Error>, target: some TargetType)

    func didReceive(_ result: Result<SocketResponse, Error>, target: some TargetType)
}
