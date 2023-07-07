//
//  TaskGameViewController.swift
//  btccurrency
//
//  Created by admin on 2023/6/29.
//

import UIKit
import SnapKit
import Then
import RxCocoa
import RxSwift
import Toast_Swift

class TaskGameViewController: YBaseViewController {
    private let fullBGView = UIImageView().then { v in
        v.image = UIImage(named: "n_task_bg")
        v.contentMode = .redraw
    }
    
    private let vcBackBtn = UIButton().then { v in
        v.setImage(UIImage(named: "n_back_white"), for: .normal)
        v.setImage(UIImage(named: "n_back_white"), for: .highlighted)
    }
    
    private let pointIconView = UIImageView().then { v in
        v.image = UIImage(named: "n_point_icon")
    }
    
    private let pointLabel = UILabel().then { v in
        v.font = .robotoRegular(with: 14)
        v.textColor = .white
        v.textAlignment = .right
    }
    
    private let pointContainerView = UIView().then { v in
        v.layer.borderColor = UIColor.primaryYellow.cgColor
        v.layer.borderWidth = 2
        v.layer.cornerRadius = 8
    }
    
    private let drawPrizeView = TCDrawPrizeView(.zero, width: UIDevice.kScreenWidth())
    private var gridCount = 8
    private var drawEnd = true
    
    private let changeLeftLabel = UILabel().then { v in
        v.textAlignment = .center
        v.font = .monomaniacOneRegular(with: 32)
        v.textColor = .white
    }
    
    private let spinBtn = UIButton().then { v in
        v.setImage(UIImage(named: "n_spin_normal"), for: .normal)
        v.setImage(UIImage(named: "n_spin_highlighted"), for: .disabled)
        v.isHidden = true
    }
    
    private var pinList: [PinListItem] = [PinListItem]()
    private var pinResult: Int?
    
    // MARK: Super Method
    override func viewDidLoad() {
        super.viewDidLoad()
        navigationView.backMode = .none
        view.backgroundColor = .white
       
        p_setElements()
        updateViewConstraints()
    }
    
    override func updateViewConstraints() {
        super.updateViewConstraints()
        
        fullBGView.snp.makeConstraints { make in
            make.edges.equalToSuperview()
        }
        
        vcBackBtn.snp.makeConstraints { make in
            make.size.equalTo(24)
            make.centerY.equalTo(navigationView)
            make.left.equalToSuperview().offset(20)
        }
        
        pointIconView.snp.makeConstraints { make in
            make.size.equalTo(20)
            make.centerY.equalTo(navigationView)
            make.right.equalTo(pointLabel.snp.left).offset(-8)
        }
        
        pointLabel.snp.makeConstraints { make in
            make.width.greaterThanOrEqualTo(20)
            make.top.bottom.equalToSuperview()
            make.right.equalToSuperview().offset(-8)
        }
        
        pointContainerView.snp.makeConstraints { make in
            make.height.equalTo(32)
            make.right.equalToSuperview().offset(-20)
            make.left.equalTo(pointIconView).offset(-8)
            make.centerY.equalTo(navigationView)
        }
        
        drawPrizeView.snp.makeConstraints { make in
            make.centerY.equalToSuperview().offset(-80)
            make.width.height.equalTo(view.snp.width)
            make.centerX.equalToSuperview()
        }
        changeLeftLabel.snp.makeConstraints { make in
            make.left.right.equalToSuperview()
            make.height.equalTo(26)
            make.top.equalTo(drawPrizeView.snp.bottom).offset(20)
        }
        
        spinBtn.snp.makeConstraints { make in
            make.width.equalTo(120)
            make.height.equalTo(125)
            make.centerX.equalToSuperview()
            make.top.equalTo(changeLeftLabel.snp.bottom).offset(56)
        }
    }
    
    // MARK: Private Method
    private func p_setElements() {
        drawPrizeView.delegate = self
        drawPrizeView.dataSource = self
        
        pointContainerView.addSubviews([pointIconView, pointLabel])
        view.addSubviews([fullBGView,
                          vcBackBtn,
                          pointContainerView,
                          drawPrizeView,
                          changeLeftLabel,
                          spinBtn])
        vcBackBtn.rx.tap.subscribe(onNext: { _ in
            self.navigationController?.popViewController(animated: true)
        })
        .disposed(by: rx.disposeBag)
        
        spinBtn.rx.tap
            .throttle(RxTimeInterval.seconds(1), scheduler: MainScheduler.instance)
            .subscribe(onNext: { [weak self] _ in
                guard let self = self else { return }
                self.p_startDraw()
            })
            .disposed(by: rx.disposeBag)
        p_refresh()
    }
    
    private func p_startDraw() {
        SmartService().pinCheck()
            .subscribe(onNext: { [weak self] result in
                guard let self = self else { return }
                guard let res = result, res.isSuccess else {
                    self.showAlert(message: result?.msg ?? "")
                    return
                }
                guard let pinItem = pinList.filter({ $0.pinId == res.data.pinId }).first, let pinIndex = pinList.firstIndex(where: { item in
                    return item.pinId == pinItem.pinId
                }) else {
                    self.showAlert(message: "got no point")
                    return
                }
                self.pinResult = pinIndex
                self.drawPrizeView.startDrawPrizeAction()
            })
            .disposed(by: rx.disposeBag)
    }
    
    private func p_refresh() {
        let service = SmartService()
        service.userProfile()
            .retry()
            .subscribe(onNext: { [weak self] result in
                guard let res = result, res.isSuccess, let self = self else { return }
                self.changeLeftLabel.text = "Chance left".localized() + " " + "\(res.data.pinChance)"
                self.pointLabel.text = "\(res.data.point)".decimalFormat()
                self.spinBtn.isEnabled = !(res.data.pinChance == 0)
                self.spinBtn.isHidden = false
                
        })
        .disposed(by: rx.disposeBag)
        
        service.pinList()
            .retry()
            .subscribe(onNext: { [weak self] result in
                guard let res = result, res.isSuccess, let self = self else { return }
                self.pinList = (res.list as? [PinListItem]) ?? [PinListItem]()
                self.drawPrizeView.reloadData()
        })
        .disposed(by: rx.disposeBag)
    }
    
    private func p_share(content: String) {
        self.share(text: content, image: nil, url: nil, completion: {
            self.view.hideAllToasts()
            ToastManager.shared.isTapToDismissEnabled = true
        })
    }
}

extension TaskGameViewController: TCDrawPrizeDataSource {
    func tcDrawPrize(prizeView: TCDrawPrizeView, descAt index: NSInteger) -> String? {
        guard pinList.count > index else { return nil }
        return pinList.reversed()[index].pinRewardDesc
    }
    
    func tcDrawPrize(prizeView: TCDrawPrizeView, imageAt index: NSInteger) -> UIImage? {
        return UIImage.init(named: "n_ring_\(index % 7 + 1)")!
    }
    
    func tcDrawPrize(prizeView: TCDrawPrizeView, imageUrlAt index: NSInteger) -> String? {
        return nil
    }
    
    func numberOfPrize(for drawprizeView: TCDrawPrizeView) -> NSInteger {
        return gridCount
    }

    func tcDrawPrize(prizeView: TCDrawPrizeView, drawOutAt index: NSInteger) -> Bool {
        return false
    }

    func tcDrawPrizeCenterImage(prizeView: TCDrawPrizeView) -> UIImage {
        return UIImage(named: "n_wheel_center_icon")!
    }

    func tcDrawPrizeBackgroundImage(prizeView: TCDrawPrizeView) -> UIImage? {
        return UIImage(named: "n_wheel_bg")
    }

    func tcDrawPrizeScrollBackgroundImage(prizeView: TCDrawPrizeView) -> UIImage? {
        if gridCount == 8 {
            return UIImage(named: "n_wheel")
        }
        return nil
    }
}

extension TaskGameViewController: TCDrawPrizeDelegate {

    func tcDrawPrizeStartAction(prizeView: TCDrawPrizeView) {
        guard let prizeIndex = self.pinResult else { return }
        self.drawPrizeView.drawPrize(at: NSInteger(prizeIndex), reject: {
            [unowned self] reject in
            if !reject {
                self.drawEnd = false
            }
        })
    }

    func tcDrawPrizeEndAction(prizeView: TCDrawPrizeView, prize index: NSInteger) {
        self.drawEnd = true
        self.p_refresh()
        
        guard let index = self.pinResult else { return }
        guard self.pinList.count > index else { return }
        ToastManager.shared.isTapToDismissEnabled = false
        let alertView = TCDrawSuccessView(iconName: "n_ring_" + "\(index + 1)", desc: self.pinList[index].pinRewardDesc)
        alertView.prizeShareBtn.rx.tap
            .subscribe(onNext: { [weak self] _ in
                guard let self = self else { return }
                self.p_share(content: self.pinList[index].pinRewardDesc)
            })
            .disposed(by: rx.disposeBag)
        alertView.closeBtn.rx.tap
            .subscribe(onNext: { [weak self] _ in
                guard let self = self else { return }
                self.view.hideAllToasts()
                ToastManager.shared.isTapToDismissEnabled = true
            })
            .disposed(by: rx.disposeBag)
        
        self.view.showToast(alertView, duration: TimeInterval(Int.max), position: .center)
    }
}
