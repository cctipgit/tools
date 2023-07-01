//
//  Request.swift
//  SocketTask
//
//  Created by fd on 2022/12/28.
//

import Foundation
import SwiftProtobuf

public protocol UniqueIdRepresentable: Hashable {
    var cid: String { get }
}

public struct SocketRequestHeader {
    let requestCmd: Int32
    let responseCmd: Int32
    let cid: String
}

public struct SocketRequestOption {
    let timeOutInterval: TimeInterval
    var isKeepReceiveData: Bool
    var isWaitSocketConnected: Bool
    var isServerPush: Bool
    var deleteWhenRequest: Bool

    public static var `default`: SocketRequestOption {
        return SocketRequestOption(timeOutInterval: 15,
                                   isKeepReceiveData: false,
                                   isWaitSocketConnected: true,
                                   isServerPush: false,
                                   deleteWhenRequest: false)
    }

    public static var keepOption: SocketRequestOption {
        return SocketRequestOption(timeOutInterval: 15,
                                   isKeepReceiveData: true,
                                   isWaitSocketConnected: true,
                                   isServerPush: false,
                                   deleteWhenRequest: false)
    }

    public static var serverPush: SocketRequestOption {
        return .init(timeOutInterval: Double.infinity,
                     isKeepReceiveData: true,
                     isWaitSocketConnected: true,
                     isServerPush: true,
                     deleteWhenRequest: false)
    }
    
    public static var deleteWhenRequest: SocketRequestOption {
        return .init(timeOutInterval: 10,
                     isKeepReceiveData: false,
                     isWaitSocketConnected: true,
                     isServerPush: false,
                     deleteWhenRequest: true)
    }
    
}

public struct SocketRequest: UniqueIdRepresentable {
    public var cid: String
    let header: SocketRequestHeader
    let option: SocketRequestOption
    let data: Data?
}

public extension SocketRequest {
    static func == (lhs: SocketRequest, rhs: SocketRequest) -> Bool {
        return lhs.cid == rhs.cid
    }

    func hash(into hasher: inout Hasher) {
        cid.hash(into: &hasher)
    }
}
