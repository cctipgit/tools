//
//  YNavigationView.swift
//  xCurrency
//
//  Created by fd on 2022/10/18.
//

@_exported import PinLayout
@_exported import SwifterSwift
@_exported import Then

import UIKit

enum NavigationViewTitleMode {
    case left
    case center
}

enum NavigationViewBackMode {
    case none
    case normal
}

enum NavigationViewPinMode {
    case none
    case pan
}

class YNavigationView: UIView {
    var titleMode: NavigationViewTitleMode = .left {
        didSet {
            reLayout()
        }
    }
    
    var backMode: NavigationViewBackMode = .normal {
        didSet {
            reLayout()
        }
    }

    var pinMode: NavigationViewPinMode = .none {
        didSet {
            panView.isHidden = pinMode == .none
            setNeedsLayout()
        }
    }

    var titleLabel = {
        UILabel().then { label in
            label.textColor = .primaryTextColor
            label.font = .robotoBold(with: 20)
        }
    }()

    var backButton = {
        UIButton().then { button in
            button.setImage(UIImage(named: "n_back"), for: .normal)
        }
    }()

    lazy var refreshView = RefreshTipView(frame: .zero)

    lazy var panView = UIView().then {
        $0.backgroundColor = .panIndicatorColor
        $0.cornerRadius = 2
        $0.isHidden = true
    }

    var rightButton = {
        UIButton().then { _ in
        }
    }()

    init() {
        super.init(frame: .zero)
        addSubviews([panView, backButton, titleLabel, rightButton])
        backgroundColor = .backgroundColor
    }

    required init?(coder: NSCoder) {
        super.init(coder: coder)
    }

    func reLayout() {
        switch titleMode {
        case .left:
            titleLabel.textAlignment = .left
        case .center:
            titleLabel.textAlignment = .center
        }
        switch backMode {
        case .none:
            backButton.isHidden = true
        case .normal:
            backButton.isHidden = false
        }
        setNeedsLayout()
    }

    override func layoutSubviews() {
        super.layoutSubviews()
        
        let bottomValue = 16.0
        if pinMode == .pan {
            panView.pin.size(CGSize(width: 48, height: 4)).top(8).hCenter()
        }
        backButton.pin.size(24).left(20).bottom(bottomValue)
        if titleMode == .left {
            titleLabel.pin.bottom(bottomValue).left(60).sizeToFit()
        } else {
            titleLabel.pin.bottom(bottomValue).hCenter().marginLeft(20).sizeToFit()
        }
        rightButton.pin.bottom(bottomValue).right(20).size(24)
    }

    func refresh() {
        if refreshView.superview != nil {
            return
        }
        addSubview(refreshView)
        refreshView.tipLabel.text = "Last Update: \(Date().string(withFormat: "HH:mm")), Today"
        refreshView.alpha = 0
        refreshView.pin.hCenter().bottom().sizeToFit()

        UIView.animate(withDuration: 0.25) {
            self.refreshView.alpha = 1
        } completion: { _ in
            DispatchQueue.main.asyncAfter(deadline: .now() + 2) {
                UIView.animate(withDuration: 0.25) {
                    self.refreshView.alpha = 0
                } completion: { _ in
                    self.refreshView.removeFromSuperview()
                }
            }
        }
    }
}
