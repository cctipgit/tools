//
//  CustomTabViewController.swift
//  cryptovise
//
//  Created by admin on 2023/6/25.
//

import UIKit
import RAMAnimatedTabBarController

class CustomTabViewController: RAMAnimatedTabBarController {

    private var currencyVC: UIViewController? // Currency
    private var gameVC: UIViewController? // Game
    private var redeemVC: UIViewController? // Redeem
    private var questionVC: UIViewController? // Question
    
    override func viewDidLoad() {
        let currencyItem = CustomTabBarItem(title: "Currency".localized(), image: UIImage(named: "n_tab_currency")!, selectedImage: UIImage(named: "n_tab_currency")!)
        currencyItem.animation = CustomTabItemAnimation()
        currencyVC = HomeCurrencyViewController()
        currencyVC?.tabBarItem = currencyItem
        
        let gameItem = CustomTabBarItem(title: "Task list".localized(), image: UIImage(named: "n_tab_tasklist")!, selectedImage: UIImage(named: "n_tab_tasklist")!)
        gameItem.animation = CustomTabItemAnimation()
        gameVC = TaskIndexViewController()
        gameVC?.tabBarItem = gameItem
        
        let questionItem = CustomTabBarItem(title: "Questionnaire", image: UIImage(named: "n_questionare")!, selectedImage: UIImage(named: "n_questionare")!)
        questionItem.animation = CustomTabItemAnimation()
        questionVC = QuestionsViewController()
        questionVC?.tabBarItem = questionItem
        
        let redeemItem = CustomTabBarItem(title: "Redeem", image: UIImage(named: "n_tab_redeem")!, selectedImage: UIImage(named: "n_tab_redeem")!)
        redeemItem.animation = CustomTabItemAnimation()
        redeemVC = RedeemIndexViewController()
        redeemVC?.tabBarItem = redeemItem
        
        self.viewControllers = [gameVC!, currencyVC!, questionVC!, redeemVC!]
        super.viewDidLoad()
    }
}

class CustomTabBarItem: RAMAnimatedTabBarItem {
    override init () {
        super.init()
        self.iconColor = UIColor.contentSecondary
        self.textColor = UIColor.contentSecondary
    }
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    override func playAnimation() {
        super.playAnimation()
        self.iconColor = UIColor.primaryBranding
        self.textColor = UIColor.primaryBranding
    }
    
    override func deselectAnimation() {
        super.deselectAnimation()
        self.iconColor = UIColor.contentSecondary
        self.textColor = UIColor.contentSecondary
    }
}

class CustomTabItemAnimation: RAMItemAnimation {
    override func playAnimation(_ icon: UIImageView, textLabel: UILabel) {
        playBounceAnimation(icon)
        textLabel.textColor = UIColor.primaryBranding // selected
        icon.tintColor = UIColor.primaryBranding
    }
    
    override func deselectAnimation(_ icon: UIImageView, textLabel: UILabel, defaultTextColor: UIColor, defaultIconColor: UIColor) {
        textLabel.textColor = UIColor.contentSecondary // Default
        icon.tintColor = UIColor.contentSecondary
    }
    
    override func selectedState(_ icon: UIImageView, textLabel: UILabel) {
        textLabel.textColor = UIColor.primaryBranding // selected
        icon.tintColor = UIColor.primaryBranding
    }
    
    func playBounceAnimation(_ icon : UIImageView) {
        let bounceAnimation = CAKeyframeAnimation(keyPath: "transform.scale")
        bounceAnimation.values = [1.0 ,1.4, 0.9, 1.15, 0.95, 1.02, 1.0]
        bounceAnimation.duration = TimeInterval(duration)
        bounceAnimation.calculationMode = CAAnimationCalculationMode.cubic
        icon.layer.add(bounceAnimation, forKey: "bounceAnimation")
    }
}
