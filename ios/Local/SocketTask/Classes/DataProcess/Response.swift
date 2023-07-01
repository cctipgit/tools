//
//  Response.swift
//  SocketTask
//
//  Created by fd on 2022/12/28.
//

import Foundation
import SwiftProtobuf

public enum SocketErrorType: Int {
    case timeout = -11
    case reachableError = -12
    case serverApiError = -13
    case responseInvalid = -14
    case requestCancelled = -15
}

extension SocketErrorType: CustomStringConvertible {
    public var description: String {
        switch self {
        case .timeout:
            return "request timeout"
        case .reachableError:
            return "network connection failure"
        case .serverApiError:
            return "service api error"
        case .responseInvalid:
            return "response invalid"
        case .requestCancelled:
            return "request has been cancelled"
        }
    }
}

public struct ResponseError: Error {
    let code: Int
    let desc: String?
}

public struct SocketResponse: UniqueIdRepresentable {
    public var cid: String
    let responseCmd: Int32
    let data: Data?
    let error: ResponseError?
}

public extension SocketResponse {
    func hash(into hasher: inout Hasher) {
        hasher.combine(cid.hashValue)
    }

    static func == (lhs: SocketResponse, rhs: SocketResponse) -> Bool {
        return lhs.cid == rhs.cid
    }
}

public extension SocketResponse {
    static func response(with cid: String,
                         responseCmd: Int32,
                         errorType: SocketErrorType) -> SocketResponse {
        let error = ResponseError(code: errorType.rawValue, desc: errorType.description)
        return SocketResponse(cid: cid,
                              responseCmd: responseCmd,
                              data: nil,
                              error: error)
    }
}
