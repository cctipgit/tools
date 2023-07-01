//
//  Configuration.swift
//  binary
//
//  Created by fd on 2023/3/16.
//

import Foundation
import Moya

extension Network {
    class Configuration {
        static var `default`: Configuration = Configuration()

        var addingHeaders: (TargetType) -> [String: String] = { _ in [:] }

        var replacingTask: (TargetType) -> Task = { $0.task }

        var timeoutInterval: TimeInterval = 60

        var plugins: [PluginType] = []

        init() {}
    }
}
