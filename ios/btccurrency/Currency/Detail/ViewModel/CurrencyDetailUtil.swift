//
//  CurrencyDetailUtil.swift
//  btccurrency
//
//  Created by fd on 2022/11/14.
//

import DataCache
import Foundation
import SocketTask

func saveDefaultRateCompare(model: GetCurrencyTokensResponse) {
    UserDefaults.standard.setValue(model.token, forKey: compareCurrencyDestinationCacheKey)
    UserDefaults.standard.synchronize()
}

func readDefaultRateCompare() -> GetCurrencyTokensResponse? {
    let token = UserDefaults.standard.string(forKey: compareCurrencyDestinationCacheKey) ?? ""
    if let model = GetCurrencyTokensResponse.fetchOneCurrency(with: token) {
        return model
    }
    return nil
}

func readCurrencyDetail(tokenFrom: String, tokenTo: String) -> GetQuotationResponse? {
    if let data = DataCache.instance.readData(forKey: tokenFrom + tokenTo + currencyDetailCurrencyResponseCacheKey),
       let response = try? GetQuotationResponse(serializedData: data) {
        return response
    }
    return nil
}

func saveCurrencyDetail(tokenFrom: String,
                        tokenTo: String,
                        response: GetQuotationResponse?) {
    if let data = try? response?.serializedData() {
        DataCache.instance.write(data: data, forKey: tokenFrom + tokenTo + currencyDetailCurrencyResponseCacheKey)
    }
}
