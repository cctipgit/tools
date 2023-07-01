//
//  Provider.swift
//  SocketTask
//
//  Created by fd on 2022/12/28.
//

import Foundation
extension SocketRequest {
    static func request<T: TargetType>(with target: T,
                                       option: SocketRequestOption) -> SocketRequest {
        let header = SocketRequestHeader(requestCmd: target.requestContent["cmd"] as! Int32,
                                         responseCmd: target.responseCmd.rawValue,
                                         cid: target.requestContent["cid"] as! String)

        let data = try? target.request.serializedData()

        return SocketRequest(cid: target.requestContent["cid"] as! String,
                             header: header,
                             option: option,
                             data: data ?? nil)
    }
}

public protocol SocketProviderType: AnyObject {
    associatedtype Target: TargetType

    func request(_ target: Target,
                 option: SocketRequestOption,
                 completion: @escaping ((Result<SocketResponse, Error>) -> Void)) -> TaskCancellable?
}

public class SocketProvider<Target: TargetType>: SocketProviderType {
    private let accessLayer = SocketLayer.shared

    private var requestMap: [Int32: String] = [:]

    let plugins: [PluginType]

    public init(plugins: [PluginType] = []) {
        self.plugins = plugins
    }

    @discardableResult
    public func request(_ target: Target,
                        option: SocketRequestOption,
                        completion: @escaping ((Result<SocketResponse, Error>) -> Void)) -> TaskCancellable? {
        let request = SocketRequest.request(with: target, option: option)

        requestMap[request.header.requestCmd] = request.cid

        let cancellable = accessLayer.sendRequest(request) { [weak self] rawResponse in
            if let error = rawResponse.error {
                let result = Result<SocketResponse, Error>.failure(error)
                self?.plugins.forEach { $0.willReceive(result, target: target) }
                completion(result)
                self?.plugins.forEach { $0.didReceive(result, target: target) }
                return
            }

            guard let data = rawResponse.data
            else {
                let errorType = SocketErrorType.responseInvalid
                let parseError = ResponseError(code: errorType.rawValue, desc: errorType.description)

                let result = Result<SocketResponse, Error>.failure(parseError)
                self?.plugins.forEach { $0.willReceive(result, target: target) }

                completion(result)

                self?.plugins.forEach { $0.didReceive(result, target: target) }
                return
            }

            let value = SocketResponse(cid: target.requestContent["cid"] as! String,
                                       responseCmd: target.responseCmd.rawValue,
                                       data: data,
                                       error: nil)

            let result = Result<SocketResponse, Error>.success(value)
            self?.plugins.forEach { $0.willReceive(result, target: target) }
            completion(result)
            self?.plugins.forEach { $0.didReceive(result, target: target) }
        }
        return cancellable
    }

    public func register(_ target: Target,
                         option: SocketRequestOption,
                         receiveData: @escaping ((Result<SocketResponse, Error>) -> Void)) -> TaskCancellable? {
        
        
        let request = SocketRequest.request(with: target, option: option)

        let cancelled = accessLayer.listen(on: request, completion: {
            [weak self] (rawResponse: SocketResponse) in

            guard let data = rawResponse.data
            else { return }

            let value = SocketResponse(cid: "",
                                       responseCmd: target.responseCmd.rawValue,
                                       data: data,
                                       error: nil)
            let result = Result<SocketResponse, Error>.success(value)
            self?.plugins.forEach { $0.willReceive(result, target: target) }
            receiveData(result)
            self?.plugins.forEach { $0.didReceive(result, target: target) }

        })

        return cancelled
    }

    public func remove(unused taskID: String) {
        accessLayer.taskManager.removeTask(with: taskID)
    }

    public func removeLast(request cmd: Int32) {
        if let cid = requestMap[cmd] {
            accessLayer.taskManager.removeTask(with: cid)
        }
    }
    
    public func removeAll(requestCmd:RequestCmd) {
        accessLayer.taskManager.removeAll(cmd: requestCmd.rawValue)
    }
}

public struct SocketApi {
    typealias FailureBlock = (_ error: ResponseError) -> Void

    @discardableResult
    static func request(target: TargetType,
                        plugins: [PluginType],
                        failureBlock: FailureBlock? = nil,
                        successBlock: @escaping ((SocketResponse) -> Void)) -> TaskCancellable? {
        let provider = SocketProvider<MultiTarget>.init(plugins: plugins)
        return provider.request(MultiTarget(target), option: .default) { (result: Result<SocketResponse, Error>) in
            switch result {
            case let .success(response):
                successBlock(response)
            case let .failure(error):
                let nserror = (error as NSError)
                let ws_err = (error as? ResponseError) ?? ResponseError(code: nserror.code, desc: nserror.localizedDescription)
                failureBlock?(ws_err)
            }
        }
    }
}

extension SocketApi {
    /// 初始化配置
    static func initConfig() {
        _ = SocketLayer.shared
    }
}
