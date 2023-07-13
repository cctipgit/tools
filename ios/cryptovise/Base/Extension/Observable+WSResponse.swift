//
//  Single+WSResponse.swift
//  cryptovise
//
//  Created by fd on 2023/1/8.
//

import Foundation
import RxSwift
import SocketTask

public extension Observable where Element == SocketResponse {
    func mapModel<M: Message>(type: M.Type, failsOnEmptyData: Bool = true) -> Observable<M?> {
        map{$0.map(M.self, failsOnEmpty: failsOnEmptyData)}
    }
}
