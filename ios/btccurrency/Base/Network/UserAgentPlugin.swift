//
//  UserAgentPlugin.swift
//  binary
//
//  Created by fd on 2023/6/14.
//

import Foundation
import Moya

final class UserAgentPlugin: PluginType {
    func prepare(_ request: URLRequest, target: TargetType) -> URLRequest {
        var req = request

        let product = Bundle.main.infoDictionary?["CFBundleDisplayName"] ?? ""
        let name = """
        \(UIDevice.current.systemName)/\(UIDevice.current.systemVersion)/\(product)
        """
        req.headers.add(name: "User-Agent", value: name)
        return req
    }
}
