//
//  DelaytCancel.swift
//  SocketTask
//
//  Created by fd on 2022/12/28.
//

import Foundation

extension DispatchWorkItem {
    typealias CancelBlock = (_ cancel: Bool) -> Void

    @discardableResult
    static func delay(time: TimeInterval,
                      task: DispatchWorkItem,
                      queue: DispatchQueue = .main) -> CancelBlock? {
        func dispatchLater(block: DispatchWorkItem) {
            queue.asyncAfter(deadline: .now() + time, execute: block)
        }

        var closure: DispatchWorkItem? = task
        var result: CancelBlock?

        let delayedClosure: CancelBlock = {
            cancel in
            if let internalClosure = closure {
                if cancel == false {
                    queue.async(execute: internalClosure)
                }
            }
            closure = nil
            result = nil
        }

        result = delayedClosure
        let item = DispatchWorkItem {
            if let delayedClosure = result {
                delayedClosure(false)
            }
        }

        dispatchLater(block: item)
        return result
    }

    static func cancel(task: CancelBlock?) {
        task?(true)
    }
}
