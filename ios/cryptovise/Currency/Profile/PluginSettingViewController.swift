//
//  PulginSettingViewController.swift
//  cryptovise
//
//  Created by fd on 2022/11/3.
//

import UIKit

class PluginSettingViewController: YBaseViewController {
    

    var imageView = UIImageView().then{
        $0.image = UIImage(named: "plugin")
    }
    
    var descLabel = UILabel().then{
        $0.font = .regularPoppin(with: 16)
        $0.textColor = .primaryTextColor
        $0.text = "This widget will display the currency list of converter.".localized()
        $0.numberOfLines = 0
    }
    
    override func makeUI() {
        navigationView.pinMode = .pan
        navigationView.titleLabel.text = "Plugin".localized()
        view.addSubviews([imageView,
                         descLabel])
    }
    
    override func makeLayout() {
        imageView.pin.below(of: navigationView)
            .marginTop(20)
            .hCenter()
            .sizeToFit()
        
        descLabel.pin.below(of: imageView)
            .left(to: imageView.edge.left)
            .right(to:imageView.edge.right)
            .marginTop(24)
            .sizeToFit(.width)
    }

}
