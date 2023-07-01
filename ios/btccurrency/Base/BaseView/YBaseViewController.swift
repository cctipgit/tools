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
}
