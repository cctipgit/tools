//
//  AnyMessage.swift
//  SocketTask
//
//  Created by fd on 2023/1/6.
//

import Foundation
import SwiftProtobuf

public struct AnyMessage<T: Message>: Message {
    
    typealias Item = T
    
    public init() {
        _message = T()
    }

    public init(with base: T) {
        _message = base
    }

    public static var protoMessageName: String {
        return T.protoMessageName
    }

    public var unknownFields: SwiftProtobuf.UnknownStorage = UnknownStorage()

    mutating public func decodeMessage<D>(decoder: inout D) throws where D: SwiftProtobuf.Decoder {
        try _message.decodeMessage(decoder: &decoder)
    }

    public func traverse<V>(visitor: inout V) throws where V: SwiftProtobuf.Visitor {
        return try _message.traverse(visitor: &visitor)
    }

    public func isEqualTo(message: SwiftProtobuf.Message) -> Bool {
        return _message.isEqualTo(message: message)
    }

    public var _message: T
}
