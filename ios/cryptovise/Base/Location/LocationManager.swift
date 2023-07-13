//
//  LocationManager.swift
//  cryptovise
//
//  Created by fd on 2022/10/31.
//

import CoreLocation
import Foundation
import RxRelay
import SocketTask

class LocationManager: NSObject {
    static var shared = LocationManager()
    var clManager = CLLocationManager()
    var currentZoneCode: BehaviorRelay<String?>
    var countryCurrency: [String: String] = [:]
    var locationEnabled = false

    override private init() {
        currentZoneCode = BehaviorRelay(value: nil)
        super.init()
        clManager.delegate = self
        if #available(iOS 14.0, *) {
            clManager.desiredAccuracy = kCLLocationAccuracyReduced
        } else {
            clManager.desiredAccuracy = kCLLocationAccuracyHundredMeters
        }

        makeLocationCurrency()
    }

    func startLocation() {
        locationEnabled = isHasLocationAuthority()

        if !locationEnabled {
            clManager.requestWhenInUseAuthorization()
        }
    }

    func requestLocation() {
        if locationEnabled {
            clManager.startUpdatingLocation()

            DispatchQueue.main.asyncAfter(deadline: .now() + 30 * 60) {
                self.requestLocation()
            }
        }
    }

    func isHasLocationAuthority() -> Bool {
        var status: CLAuthorizationStatus = .notDetermined
        if #available(iOS 14.0, *) {
            status = clManager.authorizationStatus
        } else {
            status = CLLocationManager.authorizationStatus()
        }

        let successSet: Set<CLAuthorizationStatus> = Set(arrayLiteral: .authorizedAlways, .authorizedWhenInUse)
        return successSet.contains(status)
    }

    func makeLocationCurrency() {
        if let path = Bundle.main.path(forResource: "LocationCurrency", ofType: "json"),
           let json = try? FileManager.default.jsonFromFile(atPath: path, readingOptions: .fragmentsAllowed) {
            countryCurrency = json as? [String: String] ?? [:]
        }
    }

    func getCurrentLocationCurrencyToken() -> String? {
        if !locationEnabled {
            return nil
        }

        guard let zoneCode = currentZoneCode.value
        else {
            return nil
        }

        return countryCurrency[zoneCode]
    }

    func getCurrentLocationResponse() -> GetCurrencyTokensResponse? {
        if let token = getCurrentLocationCurrencyToken() {
            return GetCurrencyTokensResponse.fetchOneCurrency(with: token)
        }
        return nil
    }
}

extension LocationManager: CLLocationManagerDelegate {
    func locationManager(_ manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {

        if locations.isEmpty {
            manager.stopUpdatingLocation()
            return
        }

        let location = locations.first!
        let geocoder = CLGeocoder()

        geocoder.reverseGeocodeLocation(location) { mark, _ in
            if mark.or([]).isEmpty {
                manager.stopUpdatingLocation()
                return
            }
            let item = mark!.first!
            if let countrycode = item.isoCountryCode {
                self.currentZoneCode.accept(countrycode)
                manager.stopUpdatingLocation()
            }
        }
    }

    func locationManager(_ manager: CLLocationManager, didFailWithError error: Error) {
        debugPrint("error: \(error.localizedDescription)")
        manager.stopUpdatingLocation()
    }

    func locationManagerDidChangeAuthorization(_ manager: CLLocationManager) {
        DispatchQueue.global().async {
            self.locationEnabled = self.isHasLocationAuthority() && CLLocationManager.locationServicesEnabled()
            if self.locationEnabled {
                DispatchQueue.main.async {
                    self.requestLocation()
                }
            }
        }
    }
}
