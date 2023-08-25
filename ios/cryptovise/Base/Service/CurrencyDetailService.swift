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

enum kCustomChartTimeType: String {
    case day = "1d"
    case week = "7d"
    case month = "1m"
    case year = "1y"
    
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
                     toSymbol: String,
                     rangeType: kCustomChartTimeType
    )
}

extension CurrencyDetailAPI: Moya.TargetType {
    var baseURL: URL {
        return URL(string: AppConfig.baseURLForChart)!
    }
    var path: String {
        switch self {
        case .queryDetail(_, _, _):
            return "history"
        }
    }
    var method: Moya.Method {
        return .get
    }
    var headers: [String : String]? {
        return ["Content-Type": "application/json",
                "Accept": "application/json"
        ]
    }
    var task: Moya.Task {
        switch self {
        case let .queryDetail(fromSymbol, toSymbol, range):
                return .requestParameters(parameters: ["range": range.rawValue,
                                                   "currency": "\(fromSymbol)/\(toSymbol)"],
                                      encoding: URLEncoding.default)
        }
    }
}

class CurrencyDetailService {
    func queryDetail(fromSymbol: String,
                     toSymbol: String,
                     rangeType: kCustomChartTimeType) -> Observable<ResultCurrencyDetail?> {
        return CurrencyDetailAPI
            .queryDetail(fromSymbol: fromSymbol,
                         toSymbol: toSymbol,
                         rangeType: rangeType)
            .cache
            .request()
            .map( {
                return ResultCurrencyDetail.mj_object(withKeyValues: $0.data)
            } )
            .asObservable()
    }
}

@objcMembers
class ResultCurrencyDetail: NSObject {
    var code: Int = 0
    var message: String = ""
    var series = NSArray()
    
    @nonobjc
    var isSuccess: Bool {
        return code == 0
    }
    
    override class func mj_objectClassInArray() -> [AnyHashable : Any]! {
        return ["series": ResultCurrencyDetailListItem.self]
    }
    override class func mj_replacedKeyFromPropertyName() -> [AnyHashable : Any]! {
        return ["code": "meta.status",
                "message": "meta.msg",
        ]
    }
}

@objcMembers
class ResultCurrencyDetailListItem: NSObject {
    var price = "0"
    var ts: Int = 0
}
