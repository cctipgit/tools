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
    }
    
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
        
        spinBtn.rx.tap.subscribe(onNext: { [weak self] _ in
            guard let self = self else { return }
            self.drawPrizeView.startDrawPrizeAction()
        })
        .disposed(by: rx.disposeBag)
        
        setData()
    }
    
    private func setData() {
        changeLeftLabel.text = "Chance left 10"
        pointLabel.text = "1,000"
    }
}

extension TaskGameViewController: TCDrawPrizeDataSource {
    func tcDrawPrize(prizeView: TCDrawPrizeView, descAt index: NSInteger) -> String? {
        return "25 PTS"
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
        //这里是本地测试的 随机 奖品 index
        //具体可根据业务数据，定位到index (顺时针顺序)
        let prizeIndex = Int(arc4random() % (UInt32(gridCount)))
        print("random index:\(prizeIndex)")
        //执行动画
        self.drawPrizeView.drawPrize(at: NSInteger(prizeIndex), reject: {
            [unowned self] reject in
            if !reject {
                self.drawEnd = false
            }
        })
        //不关注是否正在执行动画，直接调用这个
        //self.drawPrizeView.drawPrize(at: NSInteger(prizeIndex))
    }
    ///动画执行结束
    func tcDrawPrizeEndAction(prizeView: TCDrawPrizeView, prize index: NSInteger) {
        //本地测试
        self.drawEnd = true
        var value = ""
        if index == 3 {
            value = "已抽完"
        } else if index == (self.gridCount - 1) {
            value = "谢谢参与"
        } else {
            value = "\((index + 1) % 7)"
        }
        print("Index:\(index), 奖品:\(value)")
    }
}
