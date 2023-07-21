//
//  TokenSpecModel.swift
//  cryptovise
//
//  Created by fd on 2022/11/7.
//

import Foundation
import SocketTask

struct TokenSpecModel {
    var token: String
    var amount: String
    var flag: String
    
    init(from: String, amont: String, flag: String) {
        self.token = from
        self.amount = amont
        self.flag = flag
    }
    
    init(model: GetCurrencyTokensResponse) {
        self.token = model.token
        self.flag = model.icon
        switch model.currencyType {
        case "currency":
            amount = "100"
        case "digit":
            amount = "1"
        default:
            amount = "1"
        }
    }
}
