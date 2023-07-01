//
//  DataParser.swift
//  SocketTask
//
//  Created by fd on 2022/12/28.
//

import Foundation
import SwiftProtobuf

protocol SockcetDataParseable {
    static func parseData(_ data: Data?) -> SocketResponse?

    static func buildRequest(_ request: SocketRequest) -> Data?
}

struct SocketDataParser: SockcetDataParseable {
    static func parseData(_ data: Data?) -> (SocketResponse)? {
        guard let data
        else { return nil }

        if let head = try? CommandHead(serializedData: data) {
            return SocketResponse(cid: head.cid,
                                  responseCmd: head.cmd,
                                  data: data,
                                  error: nil)
        }

        return nil
    }

    static func buildRequest(_ request: SocketRequest) -> Data? {
        if let message = request.data {
            return message
        }
        debugPrint("\(#function): build request error!")
        return nil
    }
}
