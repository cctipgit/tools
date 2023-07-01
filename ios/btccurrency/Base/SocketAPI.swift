//
//  API.swift
//  btccurrency
//
//  Created by fd on 2023/1/7.
//

import Foundation
import SocketTask

enum PriceService {
    case quotation(GetQuotationRequest)

    case quotationStop(GetQuotationStopRequest)

    case symbolsRate(GetSymbolsRateRequest)
}

enum CurrencyService {
    case currencyTokens(GetCurrencyTokensRequest)
    
    case currencyTokensList(GetCurrencyTokensListRequest)
    
    case currentCurrency(GetCurrentCurrencyTokensRequest)
}

extension PriceService: TargetType {
    var baseURL: URL {
        .socketBaseURL
    }

    var responseCmd: SocketTask.ResponseCmd {
        switch self {
        case .quotation:
            return .quotationResponse

        case .quotationStop:
            return .quotationStopResponse

        case .symbolsRate:
            return .symbolRateResponse
        }
    }

    var requestContent: [String: AnyHashable] {
        switch self {
        case let .quotation(request):
            return request.mapToJSONObject()

        case let .quotationStop(request):
            return request.mapToJSONObject()

        case let .symbolsRate(request):
            return request.mapToJSONObject()
        }
    }

    var request: SocketTask.Message {
        switch self {
        case let .quotation(request):
            return request
        case let .quotationStop(request):
            return request
        case let .symbolsRate(request):
            return request
        }
    }
}

extension CurrencyService: TargetType {
    var baseURL: URL {
        .socketBaseURL
    }

    var responseCmd: SocketTask.ResponseCmd {
        switch self {
        case .currencyTokens:
            return .currencyTokensRespone
            
        case .currencyTokensList:
            return .currencyTokensListResponse
            
        case .currentCurrency:
            return .currentCurrencyTokensResponse
        }
    }

    var requestContent: [String: AnyHashable] {
        switch self {
        case let .currentCurrency(request):
            return request.mapToJSONObject()
            
        case let .currencyTokensList(request):
            return request.mapToJSONObject()

        case let .currencyTokens(request):
            return request.mapToJSONObject()
        }
    }

    var request: SocketTask.Message {
        switch self {
        case let .currentCurrency(request):
            return request
        
        case let .currencyTokensList(reqeust):
            return reqeust
            
        case let .currencyTokens(request):
            return request
        }
    }
}
