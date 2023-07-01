//
//  TargetType.swift
//  SocketTask
//
//  Created by fd on 2022/12/28.
//

import Foundation
import SwiftProtobuf

public typealias Message = SwiftProtobuf.Message

public protocol BaseTargetTypeConvertible {
    var baseURL: URL { get }
}

public protocol TargetType: BaseTargetTypeConvertible {
    var baseURL: URL { get }
    var responseCmd: ResponseCmd { get }
    var requestContent: [String: AnyHashable] { get }
    var request: Message { get }
}

public extension Protocol {
    var method: SocketMethod {
        return .GET
    }
}

public enum MultiTarget: TargetType {
    case target(TargetType)

    init(_ target: TargetType) {
        self = MultiTarget.target(target)
    }

    var target: any TargetType {
        switch self {
        case let .target(target):
            return target
        }
    }
}

public extension MultiTarget {
    var baseURL: URL {
        return target.baseURL
    }

    var responseCmd: ResponseCmd {
        return target.responseCmd
    }
    var requestContent: [String: AnyHashable] {
        return target.requestContent
    }

    var request: Message {
        return target.request
    }
}
