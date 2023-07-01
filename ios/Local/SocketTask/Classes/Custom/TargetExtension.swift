//
//  TargetExtension.swift
//  SocketTask
//
//  Created by fd on 2023/1/7.
//

import Foundation
public extension Message {
    func mapToJSONObject() -> Dictionary<String,AnyHashable> {
        if let data = try? self.jsonUTF8Data(),
           let dict = try? JSONSerialization.jsonObject(with: data,
                                                        options: [.fragmentsAllowed]) {
            return dict as! Dictionary<String,AnyHashable>
        }
        return [:]
    }
}
