//
//  Constant.swift
//  cryptovise
//
//  Created by fd on 2022/12/5.
//

import Foundation
import Alamofire

let selectIndexPathCacheKey = "SelectIndexPath.Row"
let selectTokenCacheKey = "SelectTokenCacheKey"
let showListTokensCacheKey = "ShowInHomeListTokens"

let currencyDetailCurrencyResponseCacheKey = "currencyDetailCurrencyResponseCacheKey"
let compareCurrencyDestinationCacheKey = "compareCurrencyDestinationCacheKey"

let widgetDidTappedConvertLineNotification = "widgetDidTappedConvertLineNotification"
let widgetDidTappedQuotationNotification = "widgetDidTappedQuotationNotification"

let drawPrizeChanceChangedNofification = "drawPrizeChanceChangedNofification"

let networkManager = NetworkReachabilityManager(host: "www.apple.com")

class AppInstance: NSObject {
    public static let shared = AppInstance()
    public var appFlyerConversionInfo: [AnyHashable : Any] = [AnyHashable : Any]()
    
    private override init() {
        super.init()
    }
}
