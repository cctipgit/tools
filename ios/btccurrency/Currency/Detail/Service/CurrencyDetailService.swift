//
//  CurrencyDetailService.swift
//  tcurrency
//
//  Created by admin on 2023/6/20.
//

import Foundation
import Moya
import RxSwift
import Then
import MJExtension

enum CurrencyDetailAPI {
    case queryDetail(tokenFrom: String, tokenTo: String, dateUnitType: SegementTapedKind)
}

extension CurrencyDetailAPI: Moya.TargetType {
    var baseURL: URL {
        return URL(string: AppConfig.baseURLString)!
    }
    
    var path: String {
        switch self {
        case .queryDetail(_, _, _):
            return "/walletsolidity/getnowblock"
        }
    }
    
    var method: Moya.Method {
        switch self {
        case .queryDetail(_, _, _):
            return .get
        }
    }
    
    var headers: [String : String]? {
        return ["Content-Type": "application/json",
                "Accept": "application/json"]
    }
    
    var task: Moya.Task {
        switch self {
        case let .queryDetail(tokenFrom, tokenTo, dateUnitType):
            return .requestParameters(parameters: ["tokenFrom": tokenFrom, "tokenTo": tokenTo, "dateUnitType": dateUnitType.toString()], encoding: URLEncoding.default)
        }
    }
}

class CurrencyDetailService {
    func queryDetail(tokenFrom: String, tokenTo: String, dateUnitType: SegementTapedKind) -> Observable<CurrencyDetailModel?> {
        return CurrencyDetailAPI
            .queryDetail(tokenFrom: tokenFrom, tokenTo: tokenTo, dateUnitType: dateUnitType)
            .cache
            .request()
            .map( { CurrencyDetailModel.mj_object(withKeyValues: $0.data) } )
            .asObservable()
    }
}

@objcMembers
class CurrencyDetailModel: NSObject, Codable {
    var blockID: String = ""
}
