//
//  AppDatabase.swift
//  cryptovise
//
//  Created by fd on 2022/11/11.
//

import Foundation
import GRDB
import SocketTask

final class AppDatabase {
    init(_ dbWriter: any DatabaseWriter) throws {
        self.dbWriter = dbWriter
        try migrator.migrate(dbWriter)
    }

    let dbWriter: any DatabaseWriter

    private var migrator: DatabaseMigrator {
        var migrator = DatabaseMigrator()

        migrator.registerMigration("v1") {[weak self] db in
            self?.firstMigration(db: db)
        }

        return migrator
    }

    func firstMigration(db: Database) {
        try? db.create(table: "currency", options: .ifNotExists) { table in
            table.primaryKey(["token"])
            table.column("token", .text).notNull()
            table.column("icon", .text)
            table.column("name", .text)
            table.column("currencyType", .text)
            table.column("unitName", .text)
            table.column("countryCode", .text)
            table.column("fchat", .text)
            table.column("price", .text)
        }

        if let path = Bundle.main.path(forResource: "db", ofType: "sqlite") {
            try? db.execute(sql: """
                                        ATTACH DATABASE ? AS BUNDLEDB;
                                        INSERT OR REPLACE INTO  currency(token,icon,name,currencyType,unitName,countryCode,fchat,price) SELECT token,icon,name,currencyType,unitName,countryCode,fchat,price FROM BUNDLEDB.currency;
                            """,
                            arguments: [path])
        }
    }
}

// MARK: - Database Access: Writes

extension AppDatabase {
    func saveTokensList(with list: [GetCurrencyTokensListMap]) {
        dbWriter.asyncWrite { db in
            for tokens in list {
                let fchat = tokens.fchat
                for item in tokens.data {
                    try? db.execute(sql: """
                                    insert or replace into currency (token,icon,name,currencyType,unitName,fchat,price,countryCode) values(?,?,?,?,?,?,?,?)
                                    """,
                                    arguments: [item.token, item.icon, item.name, item.currencyType, item.unitName, fchat, item.price, item.countryCode])
                }
            }

        } completion: { _, _ in
        }
    }

    func saveRates(list: [Rate]) {
        dbWriter.asyncWrite { db in
            for rate in list {
                _ = try rate.saved(db)
            }
        } completion: { _, _ in
        }
    }
    
    func removeInvalidCurrency() {
//        dbWriter.write { db in
////            GetCurrencyTokensResponse.dele
//        }
    }
}

// MARK: - Database Access: Reads

extension AppDatabase {
    /// Provides a read-only access to the database
    var databaseReader: DatabaseReader {
        dbWriter
    }

    func readRates() -> Dictionary<String, Rate> {
        var dictionary = Dictionary<String, Rate>()

        if let rates = try? databaseReader.read({ db in
            try? Rate.fetchAll(db)
        }) {
            for rate in rates {
                dictionary[rate.token] = rate
            }
        }
        return dictionary
    }

    func readFrequent(with tokens: [String]) -> [GetCurrencyTokensResponse] {
        if let list = try? databaseReader.read({ db in
            var items: [GetCurrencyTokensResponse] = []
            
            if var currencies = try? GetCurrencyTokensResponse.fetchAll(db, keys: tokens) {
                currencies.removeAll(where: {$0.price.isEmpty || $0.price == "0" })
                items.append(contentsOf: currencies)
            }
            return items
        }) {
            return list
        }
        return []
    }
}

// MARK: -

extension AppDatabase {
    static let shared = makeShared()

    private static func makeShared() -> AppDatabase {
        do {
            let dbURL = try getMainDBPath()
            let dbPool = try DatabasePool(path: dbURL.path)

            let appDatabase = try AppDatabase(dbPool)

            return appDatabase
        } catch {
            fatalError("Unresolved error \(error)")
        }
    }

    private static func getMainDBPath() throws -> URL {
        let fileManager = FileManager()
        let url = URL(fileURLWithPath: NSSearchPathForDirectoriesInDomains(.documentDirectory, .userDomainMask, true).first!)
        let folderURL = url.appendingPathComponent("database", isDirectory: true)
        try fileManager.createDirectory(at: folderURL, withIntermediateDirectories: true)

        return folderURL.appendingPathComponent("db.sqlite")
    }
}
