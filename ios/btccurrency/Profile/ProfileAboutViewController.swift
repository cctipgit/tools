//
//  ProfileAboutViewController.swift
//  btccurrency
//
//  Created by admin on 2023/6/28.
//

import UIKit

class ProfileAboutViewController: BaseWebViewController {

    override func viewDidLoad() {
        super.viewDidLoad()
        
        navigationView.backMode = .normal
        navigationView.pinMode = .none
        navigationView.titleMode = .left
        navigationView.titleLabel.text = "Abount".localized()
    }
    
    override var webUrl: URL {
        return URL(string: "http://www.baidu.com")!
    }
}
