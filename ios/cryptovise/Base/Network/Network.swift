//
//  Network.swift
//  binary
//
//  Created by fd on 2023/3/16.
//

import Alamofire
import Foundation
import Moya
import RxSwift

// MARK: - Network

class Network {
    // MARK: Lifecycle

    init(configuration: Configuration) {
        provider = MoyaProvider(configuration: configuration)
    }

    // MARK: Internal

    static let shared = Network(configuration: Configuration.default)

    let provider: MoyaProvider<MultiTarget>
}

extension MoyaProvider {
    convenience init(configuration: Network.Configuration) {
        let endpointClosure = { target -> Endpoint in
            MoyaProvider.defaultEndpointMapping(for: target)
                .adding(newHTTPHeaderFields: configuration.addingHeaders(target))
                .replacing(task: configuration.replacingTask(target))
        }

        let requestClosure = { (endpoint: Endpoint, closure: RequestResultClosure) in
            do {
                var request = try endpoint.urlRequest()
                request.timeoutInterval = configuration.timeoutInterval
                closure(.success(request))
            } catch let MoyaError.requestMapping(url) {
                closure(.failure(.requestMapping(url)))
            } catch let MoyaError.parameterEncoding(error) {
                closure(.failure(.parameterEncoding(error)))
            } catch {
                closure(.failure(.underlying(error, nil)))
            }
        }

        let sslSession: Session = {
            let trustPolicy = DefaultTrustEvaluator(validateHost: false)
        
            let urlChart = URLComponents(string: AppConfig.baseURLForChart)!
            let urlAPi = URLComponents(string: AppConfig.baseURLForAPI)!
            let trustManager = ServerTrustManager(allHostsMustBeEvaluated: false,
                                                  evaluators: [
                                                    urlChart.host!: trustPolicy,
                                                    urlAPi.host!: trustPolicy,
                                                  ])
            
            return Session(
                configuration: URLSessionConfiguration.af.default,
                startRequestsImmediately: false,
                serverTrustManager: trustManager
            )
        }()

        let plugin = UserAgentPlugin()
        configuration.plugins.append(plugin)

        self.init(
            endpointClosure: endpointClosure,
            requestClosure: requestClosure,
            session: sslSession,
            plugins: configuration.plugins
        )
    }
}

extension TargetType {
    func request() -> Single<Response> {
        return Network.shared.provider.rx.request(.target(self))
    }
}

extension Network {
    struct APIErrorResponse: Codable {
        var timestamp: String
        var status: String
        var error: String
        var path: String
    }

    struct APIResponse<T>: Codable where T: Codable {
        var msg: String
        var data: T
        var code: Int

        var success: Bool {
            return code == 200 || code == 0
        }
    }

    enum Error: Swift.Error {
        case status(code: Int, msg: String)

        // MARK: Internal

        var code: Int {
            switch self {
            case let .status(code, _):
                return code
            }
        }

        var msg: String {
            switch self {
            case let .status(_, msg):
                return msg
            }
        }
    }
}
