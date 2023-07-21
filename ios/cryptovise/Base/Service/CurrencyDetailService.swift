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

enum kCustomChartItemType: Int {
    case currency = 1
    case token = 2
    
    static func initBy(str: String) -> kCustomChartItemType {
        if str == "currency" {
            return .currency
        }
        return .token
    }
}

enum kCustomChartTimeType: Int {
    case day = 1
    case week = 2
    case month = 3
    case year = 4
    
    static func initBy(type: SegementTapedKind) -> kCustomChartTimeType {
        switch type {
        case .day:
            return .day
        case .week:
            return .week
        case .month:
            return .month
        case .year:
            return .year
        case .all:
            return .year
        }
    }
}

enum CurrencyDetailAPI {
    case queryDetail(fromSymbol: String,
                     fromType: kCustomChartItemType,
                     toSymbol: String,
                     toType: kCustomChartItemType,
                     interval: kCustomChartTimeType
    )
}

extension CurrencyDetailAPI: Moya.TargetType {
    var baseURL: URL {
        return URL(string: AppConfig.baseURLForChart)!
    }
    
    var path: String {
        switch self {
        case .queryDetail(_, _, _, _, _):
            return "/charts"
        }
    }
    
    var method: Moya.Method {
        return .post
    }
    
    var headers: [String : String]? {
        return ["Content-Type": "application/json",
                "Accept": "application/json"]
    }
    
    var task: Moya.Task {
        switch self {
        case let .queryDetail(fromSymbol, fromType, toSymbol, toType, interval):
            return .requestParameters(parameters: ["fromSymbol": fromSymbol,
                                                   "fromType": fromType.rawValue,
                                                   "toSymbol": toSymbol,
                                                   "toType": toType.rawValue,
                                                   "interval": interval.rawValue],
                                      encoding: JSONEncoding.default)
        }
    }
}

class CurrencyDetailService {
    func queryDetail(fromSymbol: String,
                     fromType: kCustomChartItemType,
                     toSymbol: String,
                     toType: kCustomChartItemType,
                     interval: kCustomChartTimeType) -> Observable<ResultCurrencyDetail?> {
        return CurrencyDetailAPI
            .queryDetail(fromSymbol: fromSymbol,
                         fromType: fromType,
                         toSymbol: toSymbol,
                         toType: toType,
                         interval: interval)
            .cache
            .request()
            .map( { ResultCurrencyDetail.mj_object(withKeyValues: $0.data) } )
            .asObservable()
    }
}

@objcMembers
class ResultCurrencyDetail: NSObject {
    var code: Int = 0
    var message = ""
    var data = NSArray()
    
    @nonobjc
    var isSuccess: Bool {
        return code == 0
    }
    
    override class func mj_objectClassInArray() -> [AnyHashable : Any]! {
        return ["data": ResultCurrencyDetailListItem.self]
    }
}

@objcMembers
class ResultCurrencyDetailListItem: NSObject {
    var c = "0"
    var t: TimeInterval = Date().timeIntervalSince1970
}
