//
// Created by fd on 2023/3/15.
//

import Foundation
import Moya
import RxSwift

extension Task {
    public var parameters: String {
        switch self {
        case let .requestParameters(parameters, _):
            return "\(parameters)"
        case let .requestCompositeData(_, urlParameters):
            return "\(urlParameters)"
        case let .requestCompositeParameters(bodyParameters, _, urlParameters):
            return "\(bodyParameters)\(urlParameters)"
        default: return ""
        }
    }
}

extension TargetType {
    var cachedKey: String {
        return "\(URL(target: self).absoluteString)?\(task.parameters)"
    }
}
