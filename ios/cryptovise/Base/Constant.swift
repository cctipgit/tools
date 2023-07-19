//
//  Constant.swift
//  cryptovise
//
//  Created by fd on 2022/12/5.
//

import Foundation

let selectIndexPathCacheKey = "SelectIndexPath.Row"
let selectTokenCacheKey = "SelectTokenCacheKey"
let showListTokensCacheKey = "ShowInHomeListTokens"

let currencyDetailCurrencyResponseCacheKey = "currencyDetailCurrencyResponseCacheKey"
let compareCurrencyDestinationCacheKey = "compareCurrencyDestinationCacheKey"

let widgetDidTappedConvertLineNotification = "widgetDidTappedConvertLineNotification"
let widgetDidTappedQuotationNotification = "widgetDidTappedQuotationNotification"

let drawPrizeChanceChangedNofification = "drawPrizeChanceChangedNofification"

class AppInstance: NSObject {
    public static let shared = AppInstance()
    public var appFlyerConversionInfo: [AnyHashable : Any] = [AnyHashable : Any]()
    
    private override init() {
        super.init()
    }
}
