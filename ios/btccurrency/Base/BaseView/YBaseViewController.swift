//
//  YBaseViewController.swift
//  xCurrency
//
//  Created by fd on 2022/10/18.
//

import FDFullscreenPopGesture
import UIKit

class YBaseViewController: UIViewController {
    var navigationView = YNavigationView()

    var isFirstAppear = true

    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
        view.addSubview(navigationView)
        fd_prefersNavigationBarHidden = true
        view.backgroundColor = .backgroundColor

        navigationView.backButton.addTarget(self, action: #selector(didTapped(backButto:)), for: .touchUpInside)

        makeUI()
        makeConstraint()
        makeEvent()
    }

    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(_: animated)
        if isFirstAppear {
            firstViewWillAppear(animated)
            isFirstAppear = false
        }
    }

    func firstViewWillAppear(_ animated: Bool) {
    }

    override func viewDidLayoutSubviews() {
        super.viewDidLayoutSubviews()
        navigationView.pin.horizontally().top(view.pin.safeArea.top).height(60.0)
        makeLayout()
    }

    @objc func didTapped(backButto: UIButton) {
        self.navigationController?.popViewController(animated: true)
    }

    func makeUI() {
    }

    func makeConstraint() {
    }

    func makeEvent() {
    }

    func makeLayout() {
    }
    
    func share(text: String?, image: UIImage?, url: URL?, completion: @escaping (() -> Void)) {
        var itemsToShare = [Any]()
        
        if let text = text {
            itemsToShare.append(text)
        }
        
        if let image = image {
            itemsToShare.append(image)
        }
        
        if let url = url {
            itemsToShare.append(url)
        }
        
        let activityViewController = UIActivityViewController(activityItems: itemsToShare, applicationActivities: nil)
        UIApplication.shared.windows.first?.rootViewController?.present(activityViewController, animated: true, completion: completion)
    }
    
    func showAlert(message: String) {
        self.view.makeToast(message, duration: 0.3, point: self.view.center, title: nil, image: nil) { didTap in
            self.view.hideAllToasts(includeActivity: true, clearQueue: true)
        }
    }
}
