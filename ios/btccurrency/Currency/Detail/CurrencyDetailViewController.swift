//
//  CurrencyDetailViewController.swift
//  xCurrency
//
//  Created by fd on 2022/10/18.
//

import BetterSegmentedControl
import FDFullscreenPopGesture
import RxRelay
import UIKit
import SocketTask

class CurrencyDetailViewController: YBaseViewController {
    var viewModel: CurrencyDetailViewModel!

    var tokenRange: BehaviorRelay<String> = BehaviorRelay(value: "")

    var tokenFromItem: GetCurrencyTokensResponse!
    var tokenToItem: GetCurrencyTokensResponse!

    var containerView: CurrencyChartContainerView = {
        CurrencyChartContainerView()
    }()

    // MARK: methods

    init(from: GetCurrencyTokensResponse, to: GetCurrencyTokensResponse) {
        tokenFromItem = from
        tokenToItem = to
        tokenRange.accept(SegementTapedKind.day.toString())

        viewModel = CurrencyDetailViewModel(with: from, to: to)

        super.init(nibName: nil, bundle: nil)
    }

    required init?(coder: NSCoder) {
        super.init(coder: coder)
    }

    override func makeUI() {
        view.backgroundColor = .backgroundColor

        navigationView.backgroundColor = .backgroundColor
        navigationView.backMode = .normal
        navigationView.pinMode = .none
        navigationView.titleMode = .left
        navigationView.titleLabel.text = "Token details".localized()

        view.addSubview(containerView)

        containerView.segmentView.segement.addTarget(self, action: #selector(didTapped(segment:)), for: .valueChanged)
        containerView.switchView.forwardButton.addTarget(self, action: #selector(didTouchedForward(button:)), for: .touchUpInside)
    }

    override func makeEvent() {
        fd_interactivePopMaxAllowedInitialDistanceToLeftEdge = 64
        bindViewModel()
        navigationView.backButton.rx
            .tap
            .subscribe(onNext: { [weak self] in
                self?.navigationController?.popViewController(animated: true)
            })
            .disposed(by: rx.disposeBag)

        containerView.tapSwitchHandler = { [weak self] index in
            guard let self
            else {
                return
            }

            let searchVC = SwitchCurrencyViewController()
            searchVC.didSelectItemHandler.subscribe(onNext: { response in
                if index == 0 {
                    self.viewModel.fromItem = response
                } else {
                    self.viewModel.toItem = response
                }

                saveDefaultRateCompare(model: response)

                self.viewModel.reloadFromTo()
                let index = self.tokenRange.value
                self.tokenRange.accept(index)
                self.containerView.layoutSwitchView()
            })
            .disposed(by: self.rx.disposeBag)
            self.present(searchVC, animated: true)
        }
    }

    override func makeLayout() {
        containerView.pin
            .horizontally()
            .bottom()
            .top(to: navigationView.edge.bottom)
    }

    func bindViewModel() {
        let trigger = rx.viewWillAppear.mapToVoid()
        let output = viewModel.transform(input: CurrencyDetailViewModel.Input(inputTrigger: trigger,
                                                                              tokenRange: tokenRange))
        containerView.bind(viewModel: viewModel)
        containerView.bind(output: output)
        
        output.isRequesting
            .distinctUntilChanged()
            .not()
            .asDriver(onErrorJustReturn: true)
            .do(onNext: { flag in
                debugPrint(flag)
            })
            .drive(self.containerView.segmentView.segement.rx.isEnabled,
                    self.containerView.loadIndicatorView.rx.isHidden)
            .disposed(by: rx.disposeBag)
                
                
    
        // 测试代码 LocationManager.shared.requestLocation()
//        CurrencyDetailService().queryDetail(tokenFrom: "", tokenTo: "", dateUnitType: .day)
//        .subscribe(onNext: { result in
//            print("test")
//        })
//        .disposed(by: rx.disposeBag)
    }

    @objc func didTouchedForward(button: UIButton) {
        containerView.changeSwitchSpec()

        viewModel.changeFromTo()
        let range = containerView.segmentControl.index
        tokenRange.accept(SegementTapedKind(rawValue: range)!.toString())
    }

    @objc func didTapped(segment: BetterSegmentedControl) {
        if containerView.loadIndicatorView.isAnimating() {
            return
        }

        let tapped = SegementTapedKind(rawValue: segment.index)
        if tapped.or(.day) != .day {
            containerView.removePricePromptView()
        }
        tokenRange.accept(tapped.or(.day).toString())
    }
}
