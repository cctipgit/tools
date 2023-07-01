//
//  ProfilePrivacyViewController.swift
//  btccurrency
//
//  Created by admin on 2023/6/28.
//

import UIKit

class ProfilePrivacyViewController: BaseWebViewController {
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        navigationView.backMode = .normal
        navigationView.pinMode = .none
        navigationView.titleMode = .left
        navigationView.titleLabel.text = "Privacy policy".localized()
    }
    
    override var webUrl: URL {
        return URL(string: "http://www.baidu.com")!
    }
}
