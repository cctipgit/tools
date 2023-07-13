//
//  CurrencyFormatter.swift
//  cryptovise
//
//  Created by fd on 2022/11/8.
//

import Foundation

enum CurrencyType: String {
    case digital
    case currency
    case futures
}

class CurrencyFormatter {
    static var shared = CurrencyFormatter()

    var coinFormatter: NumberFormatter
    var digitFormatter: NumberFormatter

    var selectCurrencyFormatter: NumberFormatter

    private init() {
        coinFormatter = NumberFormatter()
        coinFormatter.groupingSize = 3
        coinFormatter.groupingSeparator = ","
        coinFormatter.numberStyle = .decimal
        coinFormatter.maximumFractionDigits = AppSetting.shared.legalDecimalDigit.value
        coinFormatter.minimumFractionDigits = AppSetting.shared.legalDecimalDigit.value
        coinFormatter.roundingMode = .halfUp
        
        digitFormatter = NumberFormatter()
        digitFormatter.groupingSize = 3
        digitFormatter.groupingSeparator = ","
        digitFormatter.numberStyle = .decimal
        digitFormatter.maximumFractionDigits = AppSetting.shared.btcDecimalDigit.value
        digitFormatter.minimumFractionDigits = AppSetting.shared.btcDecimalDigit.value
        digitFormatter.roundingMode = .halfUp
        
        selectCurrencyFormatter = NumberFormatter()
        selectCurrencyFormatter.groupingSize = 3
        selectCurrencyFormatter.groupingSeparator = ","
        selectCurrencyFormatter.numberStyle = .decimal
        selectCurrencyFormatter.minimumFractionDigits = 0
        selectCurrencyFormatter.roundingMode = .halfUp
    }

    func format(float text: Double, type: CurrencyType) -> String {
        switch type {
        case .digital:
            return digitFormatter.string(from: text as NSNumber) ?? text.string
        case .currency:
            return coinFormatter.string(from: text as NSNumber) ?? text.string
        default:
            return coinFormatter.string(from: text as NSNumber) ?? text.string
        }
    }
    
    func format(with text: String, type: CurrencyType) -> String {
        switch type {
        case .digital:
            return digitFormatter.string(from: NSDecimalNumber(string: text)) ?? text
        case .currency:
            return coinFormatter.string(from: NSDecimalNumber(string: text)) ?? text
        default:
            return coinFormatter.string(from: NSDecimalNumber(string: text)) ?? text
        }
    }
    
    func format(selectCurrency text: String,type: String) -> String {
        
        if text.contains(CalculatorKey.decimal.toString()) {
            return text
        }
        selectCurrencyFormatter.alwaysShowsDecimalSeparator = text.ends(with: CalculatorKey.decimal.toString())
        switch CurrencyType(rawValue: type) ?? .currency {
        case .digital:
            selectCurrencyFormatter.maximumFractionDigits = AppSetting.shared.btcDecimalDigit.value
            return selectCurrencyFormatter.string(from: NSDecimalNumber(string: text)) ?? text
            
        case .currency:
            selectCurrencyFormatter.maximumFractionDigits = AppSetting.shared.legalDecimalDigit.value
            return selectCurrencyFormatter.string(from: NSDecimalNumber(string: text)) ?? text
        default:
            return text
        }
    }
    
    static func format(with text: String, type: String) -> String {
        return CurrencyFormatter.shared.format(with: text,
                                               type: CurrencyType(rawValue: type) ?? .currency)
    }


    static func formatPercent(with text: String, type: String) -> String {
        let text = CurrencyFormatter.shared.format(with: text,
                                                   type: CurrencyType(rawValue: type) ?? .currency)
        if text.isEmpty {
            return text
        } else {
            return text.appending("%")
        }
    }
    
    static func format(float text: Double, type: String) -> String {
        return CurrencyFormatter.shared.format(float: text,
                                               type: CurrencyType(rawValue: type) ?? .currency)
    }
}
