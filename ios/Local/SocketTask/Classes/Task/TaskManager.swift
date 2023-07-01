//
//  TaskManager.swift
//  SocketTask
//
//  Created by fd on 2022/12/28.
//

import Foundation

public typealias ResponseCompletion = (SocketResponse) -> Void

public protocol TaskCancellable {
    var isCancelled: Bool { get }
    func cancel()
}

public class RequestTask {
    var request: SocketRequest
    let completionBlock: ResponseCompletion?
    let timeoutBlock: DispatchWorkItem.CancelBlock?

    public fileprivate(set) var isCancelled = false
    public fileprivate(set) var isRemoved = false

    init(request: SocketRequest,
         completionBlock: ((SocketResponse) -> Void)?,
         timeoutBlock: DispatchWorkItem.CancelBlock?) {
        self.request = request
        self.completionBlock = completionBlock
        self.timeoutBlock = timeoutBlock
    }

    deinit {
        debugPrint("task deinit \(self)")
    }
}

extension RequestTask: TaskCancellable {
    public var isInvalid: Bool {
        isCancelled || isRemoved
    }

    public func cancel() {
        if isInvalid {
            return
        }

        DispatchWorkItem.cancel(task: timeoutBlock)
        let response = SocketResponse.response(with: request.cid,
                                               responseCmd: request.header.responseCmd,
                                               errorType: .timeout)
        completionBlock?(response)
    }

    func makeTaskRemoved() {
        isRemoved = true
    }
}

extension RequestTask: Hashable {
    public static func == (lhs: RequestTask, rhs: RequestTask) -> Bool {
        return lhs.request.cid == rhs.request.cid
    }

    public func hash(into hasher: inout Hasher) {
        request.cid.hash(into: &hasher)
    }
}

public protocol TaskTimeoutDelegate: AnyObject {
    func websocketRequestDidTimeout(_ request: SocketRequest)
}

public class TaskManager {
    weak var delegate: TaskTimeoutDelegate?
    var requestTaskPool = Set<RequestTask>()
}

public extension TaskManager {
    func insert(request: SocketRequest, completion: ResponseCompletion?) -> TaskCancellable {
        var timeoutBlock: DispatchWorkItem.CancelBlock?

        if !request.option.isKeepReceiveData {
            let block = DispatchWorkItem {
                [weak self] in
                self?.delegate?.websocketRequestDidTimeout(request)
            }

            timeoutBlock = DispatchWorkItem.delay(time: request.option.timeOutInterval,
                                                  task: block)
        }

        let task = RequestTask(request: request,
                               completionBlock: completion,
                               timeoutBlock: timeoutBlock)
        if !(requestTaskPool.contains(task)) {
            requestTaskPool.insert(task)
        }
        return task
    }

    func insert(forListen request: SocketRequest,
                completion: ResponseCompletion?) -> TaskCancellable {
        let task = RequestTask(request: request,
                               completionBlock: completion,
                               timeoutBlock: nil)

        if !(requestTaskPool.contains(task)) {
            requestTaskPool.insert(task)
        }

        return task
    }

    func removeTask(with uniqueID: String) {
        if let task = getTask(with: uniqueID) {
            task.makeTaskRemoved()
            requestTaskPool.remove(task)
        }
    }

    func getTask(with uniqueID: String) -> RequestTask? {
        return requestTaskPool.filter { $0.request.cid == uniqueID }.first
    }

    func getTask(with cmd: Int32) -> RequestTask? {
        return requestTaskPool
            .filter { $0.request.header.responseCmd == cmd
                || $0.request.header.requestCmd == cmd
            }
            .first
    }

    func removeAll(cmd: Int32) {
        requestTaskPool
            .filter { $0.request.header.requestCmd == cmd
                || $0.request.header.responseCmd == cmd
            }
            .forEach { task in
                task.makeTaskRemoved()
                requestTaskPool.remove(task)
            }
    }

    func removeAll() {
        requestTaskPool.forEach { $0.makeTaskRemoved() }
        requestTaskPool.removeAll()
    }
}
