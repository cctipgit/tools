//
//  ProfilePrivacyViewController.swift
//  cryptovise
//
//  Created by admin on 2023/6/28.
//

import UIKit
import SnapKit

class ProfilePrivacyViewController: BaseWebViewController {
    override func viewDidLoad() {
        super.viewDidLoad()
        
        navigationView.backMode = .normal
        navigationView.pinMode = .none
        navigationView.titleMode = .left
        navigationView.titleLabel.text = "Privacy policy".localized()
    }
    override var webUrl: URL {
        let urlStr = Bundle.main.path(forResource: "privacy", ofType: "html")
        let mUrl = URL.init(fileURLWithPath: urlStr!)
        return mUrl
    }
}
