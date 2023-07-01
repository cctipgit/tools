//
//  SocketLayer.swift
//  SocketTask
//
//  Created by fd on 2022/12/28.
//

import Foundation
import Starscream
import SwiftProtobuf

protocol SocketConnectionProtocol {
    var isNetworkReachable: Bool { get }

    func openConnection()
    func closeConnection()
    func sendData(_ data: Data)
}

protocol SocketConnectionObservable: AnyObject {
    func onWebSocketReceiveData(_ data: Data?, client: WebSocketClient)
    func onWebSocketOpened(_ client: WebSocketClient)
    func onWebSocketClosed(_ client: WebSocketClient, error: Error?)
    func onWebSocketReceivePong(_ data: Data?, client: WebSocketClient)
}

class SocketLayer: NSObject {
    lazy var connection: SocketConnection = {
        let connection = SocketConnection()
        connection.delegate = self
        return connection
    }()

    lazy var taskManager: TaskManager = {
        let manager = TaskManager()
        manager.delegate = self
        return manager
    }()

    var resendRequests: [SocketRequest: ResponseCompletion] = [:]

    static let shared = SocketLayer()
    override private init() {
        super.init()
        _ = connection
    }
}

extension SocketLayer {
    func sendRequest(_ request: SocketRequest,
                     completion: ResponseCompletion?) -> TaskCancellable? {
        let delaySend = request.option.isWaitSocketConnected

        if !(connection.isConnected) {
            if delaySend {
                if !resendRequests.keys.contains(where: { $0.cid == request.cid }) {
                    resendRequests[request] = completion
                    let cancellable = taskManager.insert(request: request, completion: completion)
                    return cancellable
                }
                return nil
            }

            let errorType: SocketErrorType = connection.isNetworkReachable ? .serverApiError : .reachableError
            let response = SocketResponse.response(with: request.cid,
                                                   responseCmd: request.header.responseCmd,
                                                   errorType: errorType)
            completion?(response)
            return nil
        }

        if let data = request.data {
            connection.sendData(data)
            let cancellable = taskManager.insert(request: request, completion: completion)

            if request.option.deleteWhenRequest {
                taskManager.removeTask(with: request.cid)
                return nil
            }
            return cancellable
        }
        return nil
    }

    func listen(on request: SocketRequest, completion: ResponseCompletion?) -> TaskCancellable? {
        return taskManager.insert(request: request, completion: completion)
    }
}

extension SocketLayer: SocketConnectionObservable {
    func onWebSocketReceiveData(_ data: Data?, client: Starscream.WebSocketClient) {
        guard let data,
              let head = try? CommandHead(serializedData: data)
        else { return }
        var task = SocketLayer.shared.taskManager.getTask(with: head.cid)

        if task == nil {
            task = SocketLayer.shared.taskManager.getTask(with: head.cmd)
        }
//        var content: Message? = nil
//        if let task,
//           task.request.type is Message.Type {
//            let t = task.raequest.type as! Message.Type
//            content = try? t.init(serializedData: data)
//        }

        let response = SocketResponse(cid: head.cid,
                                      responseCmd: head.cmd,
                                      data: data,
                                      error: nil)

        DispatchWorkItem.cancel(task: task?.timeoutBlock)
        if !(task?.isInvalid ?? false) {
            task?.completionBlock?(response)
        }

        if task?.request.option.isKeepReceiveData ?? false {
            return
        }

        taskManager.removeTask(with: response.cid)
    }

    func onWebSocketOpened(_ client: Starscream.WebSocketClient) {
        if !resendRequests.isEmpty {
            resendRequests.forEach { [weak self] request, completion in
                _ = self?.sendRequest(request, completion: completion)
            }
            resendRequests.removeAll()
        }
        NotificationCenter.default.post(Notification(name: SocketConstant.socketDidOpendNotification))
    }

    func onWebSocketClosed(_ client: Starscream.WebSocketClient, error: Error?) {
        let set = taskManager.requestTaskPool
        for task in set {
            let errorType: SocketErrorType = connection.isNetworkReachable ? .serverApiError : .reachableError
            let response = SocketResponse.response(with: task.request.cid,
                                                   responseCmd: task.request.header.responseCmd,
                                                   errorType: errorType)
            DispatchWorkItem.cancel(task: task.timeoutBlock)
            if !task.isInvalid {
                task.completionBlock?(response)
                taskManager.removeTask(with: task.request.cid)
            }
        }
    }

    func onWebSocketReceivePong(_ data: Data?, client: Starscream.WebSocketClient) {
    }
}

extension SocketLayer: TaskTimeoutDelegate {
    func websocketRequestDidTimeout(_ request: SocketRequest) {
        let response = SocketResponse.response(with: request.cid,
                                               responseCmd: request.header.responseCmd,
                                               errorType: .timeout)
        let task = taskManager.getTask(with: request.cid)
        if !(task?.isInvalid ?? false) {
            task?.completionBlock?(response)
        }
        taskManager.removeTask(with: request.cid)
    }
}
