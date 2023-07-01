//
//  NetworkMonitor.swift
//  SocketTask
//
//  Created by fd on 2022/12/28.
//

import Foundation
import Reachability

protocol NetworkStateChangeDelegate: AnyObject {
    func networkStateChanged(_ state: Reachability.Connection)
}

class NetworkMonitor {
    let reachability: Reachability!
    weak var delegate: NetworkStateChangeDelegate?
    var isNetworkReachable: Bool {
        return reachability.connection == .unavailable
    }

    init(host: String) {
        do {
            reachability = try? Reachability(hostname: host)
            reachability.whenReachable = { reachability in
                self.delegate?.networkStateChanged(reachability.connection)
            }

            reachability.whenUnreachable = { reachability in
                self.delegate?.networkStateChanged(reachability.connection)
            }

            try reachability.startNotifier()

        } catch {
            debugPrint(error)
        }
    }
}
