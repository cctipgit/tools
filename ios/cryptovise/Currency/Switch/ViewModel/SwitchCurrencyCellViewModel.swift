//
//  SwitchCurrencyCellViewModel.swift
//  cryptovise
//
//  Created by fd on 2022/10/29.
//

import Foundation
import SocketTask


class SwitchCurrencyCellViewModel {
    var title: String
    var isShowLocation: Bool = false
    var currency: String
    var isSelect: Bool = false
    var flag: String
    var response: GetCurrencyTokensResponse
    
    init(with data: GetCurrencyTokensResponse) {
        title = data.name
        flag = data.icon
        currency = data.token
        response = data
    }
}
