//
//  Rate.swift
//  cryptovise
//
//  Created by fd on 2022/11/12.
//

import Foundation
import GRDB

struct Rate: FetchableRecord, TableRecord, MutablePersistableRecord {
    var price: String
    var token: String
    var currentSelectPrice: String?

    init(price: String, token: String) {
        self.price = NSDecimalNumber(string: price).stringValue
        self.token = token
    }

    init(row: Row) throws {
        price = NSDecimalNumber(string: row["price"]).stringValue
        token = row["token"]
    }

    static var databaseTableName: String {
        return "currency"
    }

    func encode(to container: inout GRDB.PersistenceContainer) throws {
        container["token"] = token
        container["price"] = price
    }
}
