//
//  QuoteColorSettingViewController.swift
//  cryptovise
//
//  Created by fd on 2022/11/3.
//

import UIKit
import SnapKit

class QuoteColorSettingViewController: YBaseViewController {
    var leftItem = QuoteColorItemView().then {
        $0.label.text = "red up, green fall".localized()
        $0.imageView1.image = UIImage(named: "n_red_up")
        $0.imageView2.image = UIImage(named: "n_green_down")
    }

    var rightItem = QuoteColorItemView().then {
        $0.label.text = "green up, red fall".localized()
        $0.imageView1.image = UIImage(named: "n_green_up")
        $0.imageView2.image = UIImage(named: "n_red_down")
    }
    
    var containerView = UIView()

    override func makeUI() {
        view.addSubviews([leftItem, rightItem])
        navigationView.backMode = .normal
        navigationView.pinMode = .none
        navigationView.titleMode = .left
        navigationView.titleLabel.text = "Price Color".localized()
        updateViewConstraints()
    }

    override func updateViewConstraints() {
        super.updateViewConstraints()
        rightItem.snp.makeConstraints { make in
            make.left.equalToSuperview().offset(20)
            make.width.equalTo(leftItem)
            make.right.equalTo(leftItem.snp.left).offset(-8)
            make.top.equalTo(navigationView.snp.bottom).offset(16)
            make.height.equalTo(leftItem)
        }
        leftItem.snp.makeConstraints { make in
            make.top.equalTo(rightItem)
            make.height.equalTo(160.0 * 190.0 / ((UIDevice.kScreenWidth() - 48) / 2.0))
        }
    }
    
    override func makeLayout() {
//        leftItem.pin.top()
//            .left()
//            .bottom()
//            .sizeToFit()
//
//        rightItem.pin.top()
//            .right()
//            .bottom()
//            .after(of: leftItem)
//            .marginLeft(16)
//            .sizeToFit()
//
//        containerView.pin.below(of: navigationView)
//            .marginTop(22)
//            .height(leftItem.height)
//            .hCenter()
//            .width(2 * leftItem.width + 16)
    }
    
    override func makeEvent() {
        let isRedGrow = AppSetting.shared.quoteColor.value
        
        leftItem.selected = isRedGrow
        rightItem.selected = !leftItem.selected
        
        leftItem.button.rx
            .tap
            .subscribe(onNext: {
                [weak self] in
                self?.leftItem.selected = true
                self?.rightItem.selected = false
                AppSetting.shared.quoteColor.accept(true)
                self?.navigationController?.popViewController(animated: true)
            })
            .disposed(by: rx.disposeBag)
        rightItem.button.rx
            .tap
            .subscribe(onNext: {
                [weak self] in
                self?.leftItem.selected = false
                self?.rightItem.selected = true
                
                AppSetting.shared.quoteColor.accept(false)
                
                self?.navigationController?.popViewController(animated: true)
            })
            .disposed(by: rx.disposeBag)
    }
}
