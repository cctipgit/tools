//
//  SocketConnection.swift
//  SocketTask
//
//  Created by fd on 2022/12/28.
//

import Foundation
import Reachability
import Starscream

struct SocketConnectionOption {
    let timeoutInterval: TimeInterval

    let reconnectInterval: TimeInterval

    let reconnectMaxCount: Int

    let heartbeatInterval: TimeInterval

    static var `default`: SocketConnectionOption {
        return SocketConnectionOption(timeoutInterval: 5.0,
                                      reconnectInterval: 3.0,
                                      reconnectMaxCount: 20,
                                      heartbeatInterval: 15.0)
    }
}

class SocketConnection {
    weak var delegate: SocketConnectionObservable?

    var socket: WebSocket?
    var reconnectCount = 0

    var isConnected = false
    let networkReachable: NetworkMonitor
    var nextHeartbeatingBlock: DispatchWorkItem.CancelBlock?

    init() {
        var host: String = ""
        if #available(iOS 16.0, *) {
            host = URL.socketBaseURL.host()!
        } else {
            host = URL.socketBaseURL.host!
        }
        networkReachable = NetworkMonitor(host: host)
        networkReachable.delegate = self

        NotificationCenter.default.addObserver(self,
                                               selector: #selector(willEnterForeground(notification:)),
                                               name: UIApplication.willEnterForegroundNotification,
                                               object: nil)
    }
}

extension SocketConnection {
    @objc func willEnterForeground(notification: Notification) {
        if isConnected {
            return
        } else {
            retryConnect()
        }
    }

    func retryConnect() {
        closeConnection()
        if isNetworkReachable == false && reconnectCount > 5 {
            return
        }

        if reconnectCount > SocketConnectionOption.default.reconnectMaxCount {
            return
        }

        let reconnectDelay = SocketConnectionOption.default.reconnectInterval
        DispatchQueue.main.asyncAfter(deadline: .now() + reconnectDelay) {
            [weak self] in
            self?.openConnection()
        }
        reconnectCount += 1
        debugPrint("\(#function) count: \(reconnectCount)")
    }

    func enableHeartBeating(_ enable: Bool) {
        if enable {
            let data = "heart".data(using: .utf8)
            socket?.write(ping: data!, completion: nil)

            let delay = SocketConnectionOption.default.heartbeatInterval
            nextHeartbeatingBlock = DispatchWorkItem.delay(time: delay,
                                                           task: DispatchWorkItem(block: {
                                                               [weak self] in
                                                               self?.enableHeartBeating(true)
                                                           }))

        } else {
            DispatchWorkItem.cancel(task: nextHeartbeatingBlock)
        }
    }
}

extension SocketConnection: NetworkStateChangeDelegate {
    func networkStateChanged(_ state: Reachability.Connection) {
        switch state {
        case .unavailable:
            enableHeartBeating(false)
        default:
            openConnection()
        }
    }
}

extension SocketConnection: SocketConnectionProtocol {
    var isNetworkReachable: Bool {
        return networkReachable.isNetworkReachable
    }

    func openConnection() {
        if isConnected {
            return
        }

        let timeout = SocketConnectionOption.default.timeoutInterval
        let request = URLRequest(url: .socketBaseURL,
                                 cachePolicy: .useProtocolCachePolicy,
                                 timeoutInterval: timeout)
        socket = WebSocket(request: request)
        socket?.delegate = self
        socket?.connect()
        debugPrint("try to connect socket ... ")
    }

    func closeConnection() {
        socket?.disconnect()
        socket = nil
        debugPrint("try to close socket ... ")
    }

    func sendData(_ data: Data) {
        if !isConnected {
            return
        }

        socket?.write(data: data, completion: {})
    }
}

extension SocketConnection: WebSocketDelegate {
    func didReceive(event: Starscream.WebSocketEvent, client: Starscream.WebSocket) {
        switch event {
        case let .connected(dict):
            isConnected = true
            delegate?.onWebSocketOpened(client)

            reconnectCount = 0
            enableHeartBeating(true)

            debugPrint(dict)
            debugPrint("socket connect successfull!")

        case let .disconnected(text, code):
            isConnected = false
            delegate?.onWebSocketClosed(client, error: nil)
            debugPrint("socket disconnected " + text + " " + "\(code)")
            retryConnect()

        case let .binary(data):
            delegate?.onWebSocketReceiveData(data, client: client)

        case let .text(msg):
            debugPrint(msg)
            break

        case let .pong(data):
            let desc = String(data: data ?? Data(), encoding: .utf8) ?? ""
            debugPrint(desc)
            delegate?.onWebSocketReceivePong(data, client: client)

        case .ping:
            break
        case let .error(error):
            isConnected = false
            delegate?.onWebSocketClosed(client, error: error)
            debugPrint("socket disconnected " + error.debugDescription)
            retryConnect()

        case .viabilityChanged:
            break
        case .reconnectSuggested:
            break
        case .cancelled:
            break
        }
    }
}
