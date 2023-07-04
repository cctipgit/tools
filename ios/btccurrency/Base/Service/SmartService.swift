//
//  SmartService.swift
//  btccurrency
//
//  Created by admin on 2023/7/4.
//

import Foundation
import Moya
import RxSwift
import Then
import MJExtension

enum SmartServiceAPI {
    case pinCheck(Void)
    case pinList(Void)
    case redeemHistory(page: Int)
    case redeemList(Void)
    case redeemPointList(page: Int)
    case redeemRedeem(rid: String)
    case taskCheck(param: String, taskId: String)
    case taskList(Void)
    case userProfile(Void)
}

extension SmartServiceAPI: Moya.TargetType {
    var baseURL: URL {
        return URL(string: AppConfig.baseURLForAPI)!
    }
    
    var path: String {
        switch self {
        case .pinCheck(_):
            return "/pin/check"
        case .pinList(_):
            return "/pin/list"
        case .redeemHistory(_):
            return "/redeem/history"
        case .redeemList(_):
            return "/redeem/list"
        case .redeemPointList(_):
            return "/redeem/point/list"
        case .redeemRedeem(_):
            return "/redeem/redeem"
        case .taskCheck(_, _):
            return "/task/check"
        case .taskList(_):
            return "/task/list"
        case .userProfile(_):
            return "/user/profile"
        }
    }
    
    var method: Moya.Method {
        return .post
    }
    
    var headers: [String : String]? {
        return ["Content-Type": "application/json",
                "Accept": "application/json",
                "Authorization": NSObject().userId()
        ]
    }
    
    var task: Moya.Task {
        switch self {
        case .pinCheck(_):
            return .requestParameters(parameters: [String: String](), encoding: JSONEncoding.default)
        case .pinList(_):
            return .requestParameters(parameters: [String: String](), encoding: JSONEncoding.default)
        case let .redeemHistory(page):
            return .requestParameters(parameters: ["page": page], encoding: JSONEncoding.default)
        case .redeemList(_):
            return .requestParameters(parameters: [String: String](), encoding: JSONEncoding.default)
        case let .redeemPointList(page):
            return .requestParameters(parameters: ["page": page], encoding: JSONEncoding.default)
        case let .redeemRedeem(rid):
            return .requestParameters(parameters: ["id": rid], encoding: JSONEncoding.default)
        case let .taskCheck(param, taskId):
            return .requestParameters(parameters: ["param": param, "task_id": taskId], encoding: JSONEncoding.default)
        case .taskList(_):
            return .requestParameters(parameters: [String: String](), encoding: JSONEncoding.default)
        case .userProfile(_):
            return .requestParameters(parameters: [String: String](), encoding: JSONEncoding.default)
        }
    }
}

class SmartService {
    
    func pinCheck() -> Observable<ResultPinCheck?> {
        return SmartServiceAPI
            .pinCheck(Void())
            .cache
            .request()
            .map({ ResultPinCheck.mj_object(withKeyValues: $0.data) })
            .asObservable()
    }
    
    func pinList() -> Observable<ResultPinList?> {
        return SmartServiceAPI
            .pinList(Void())
            .cache
            .request()
            .map({ ResultPinList.mj_object(withKeyValues: $0.data) })
            .asObservable()
    }
    
    func redeemHistory(page: Int) -> Observable<ResultRedeemHistory?> {
        return SmartServiceAPI
            .redeemHistory(page: page)
            .cache
            .request()
            .map({ ResultRedeemHistory.mj_object(withKeyValues: $0.data) })
            .asObservable()
    }
    
    func redeemList() -> Observable<ResultRedeemList?> {
        return SmartServiceAPI
            .redeemList(Void())
            .cache
            .request()
            .map({ ResultRedeemList.mj_object(withKeyValues: $0.data) })
            .asObservable()
    }
    
    func redeemPointList(page: Int) -> Observable<ResultPointList?> {
        return SmartServiceAPI
            .redeemPointList(page: page)
            .cache
            .request()
            .map({ ResultPointList.mj_object(withKeyValues: $0.data) })
            .asObservable()
    }
    
    func redeemRedeem(rid: String) -> Observable<ResultRedeemRedeem?> {
        return SmartServiceAPI
            .redeemRedeem(rid: rid)
            .cache
            .request()
            .map({ ResultRedeemRedeem.mj_object(withKeyValues: $0.data) })
            .asObservable()
    }
    
    func taskCheck(param: String, taskId: String) -> Observable<ResultTaskCheck?> {
        return SmartServiceAPI
            .taskCheck(param: param, taskId: taskId)
            .cache
            .request()
            .map({ ResultTaskCheck.mj_object(withKeyValues: $0.data) })
            .asObservable()
    }
    
    func taskList() -> Observable<ResultTaskList?> {
        return SmartServiceAPI
            .taskList(Void())
            .cache
            .request()
            .map({ ResultTaskList.mj_object(withKeyValues: $0.data) })
            .asObservable()
    }
    
    func userProfile() -> Observable<ResultUserProfile?> {
        return SmartServiceAPI
            .userProfile(Void())
            .cache
            .request()
            .map({ ResultUserProfile.mj_object(withKeyValues: $0.data) })
            .asObservable()
    }
}
