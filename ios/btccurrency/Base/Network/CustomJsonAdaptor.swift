//
//  CustomJsonAdaptor.swift
//  binary
//
//  Created by fd on 2023/4/26.
//

import CleanJSON
import Foundation

struct CustomJsonAdaptor: JSONAdapter {
    func adapt(_ decoder: CleanDecoder) throws -> Bool {
        if decoder.decodeNil() {
            return false
        }

        if let value = try? decoder.decodeIfPresent(Int.self) {
            return value != 0
        }
        return false
    }
    
    private let dateFormatter = DateFormatter().then { RFC3339DateFormatter in
        RFC3339DateFormatter.locale = Locale(identifier: "en_US_POSIX")
        RFC3339DateFormatter.dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSSSZZZZZ"
        RFC3339DateFormatter.timeZone = TimeZone(secondsFromGMT: 0)
    }
    private let defaultDate = Date(timeIntervalSince1970: 0)
    
    func adapt(_ decoder: CleanDecoder) throws -> Date {
        if decoder.decodeNil() {
            return defaultDate
        }
        if let value = try? decoder.decodeIfPresent(String.self) {
            return dateFormatter.date(from: value) ?? defaultDate
        }
        if let value = try? decoder.decodeIfPresent(Int.self) {
            return Date(timeIntervalSince1970: TimeInterval(value))
        }
        
        if let value = try? decoder.decodeIfPresent(Double.self) {
            return Date(timeIntervalSince1970: TimeInterval(value))
        }
        
        return defaultDate
    }
}
