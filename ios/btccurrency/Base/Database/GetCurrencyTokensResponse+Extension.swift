//
//  Database+Extension.swift
//  btccurrency
//
//  Created by fd on 2022/11/11.
//

import Foundation
import GRDB
import SocketTask

extension GetCurrencyTokensResponse: FetchableRecord {
    public init(row: Row) throws {
        self.init()
        token = row["token"]
        icon = row["icon"]
        name = row["name"]
        currencyType = row["currencyType"]
        unitName = row["unitName"]
        price = row["price"]
        if row["countryCode"].isNone {
            countryCode = ""
        } else {
            countryCode = row["countryCode"]
        }
    }
}

extension GetCurrencyTokensResponse: TableRecord {
    public static var databaseTableName: String {
        return "currency"
    }
}

extension GetCurrencyTokensResponse: MutablePersistableRecord {
    public func encode(to container: inout GRDB.PersistenceContainer) throws {
        container["token"] = token
        container["icon"] = icon
        container["name"] = name
        container["currencyType"] = currencyType
        container["unitName"] = unitName
        container["price"] = price
        if countryCode.isEmpty {
            container["countryCode"] = ""
        } else {
            container["countryCode"] = countryCode
        }
    }
}

extension GetCurrencyTokensResponse {
    static let dbReader = AppDatabase.shared.databaseReader

    static func readCurrencyList() -> [SwitchCurrencyGroup] {
        if let list = try? dbReader.read({ db in

            var groups: [SwitchCurrencyGroup] = []

            let fchats = try String.fetchAll(db, sql: "select distinct fchat from currency")

            for fchat in fchats {
                if let items = try? GetCurrencyTokensResponse.fetchAll(db, sql: "select * from currency where fchat = ? and price > 0", arguments: [fchat]) {
                    let group = SwitchCurrencyGroup(header: fchat, items: items.map { SwitchCurrencyCellViewModel(with: $0) }, sectionType: .fixed)
                    groups.append(group)
                }
            }

            return groups
        }) {
            return list
        }
        return []
    }

    static public func readLocalItem(with zoneCode: String?) -> GetCurrencyTokensResponse? {
        guard let zoneCode,
              !zoneCode.isEmpty
        else { return nil }
        return try? dbReader.read({ db in
            try? GetCurrencyTokensResponse
                .fetchOne(db, sql: "select * from currency where countryCode = ?", arguments: [zoneCode])
        })
    }

    static public func readHomeList(tokens: [String]) -> [GetCurrencyTokensResponse]? {
//        try? dbReader.read({ db in
//
//            var data: [GetCurrencyTokensResponse] = []
//            for token in tokens {
//                if let current = try? GetCurrencyTokensResponse.fetchOne(db, key: token) {
//                    data.append(current)
//                }
//            }
//
//            return data
//        })
        
        return try? dbReader.read({ db in
            let data = try? GetCurrencyTokensResponse.fetchAll(db, keys: tokens)
            return data?.sorted(like: tokens, keyPath: \.token)
        })
    }

    static public func fetchOneCurrency(with token: String) -> GetCurrencyTokensResponse? {
        try? dbReader.read({ db in
            try GetCurrencyTokensResponse.fetchOne(db, key: token)
        })
    }
}
