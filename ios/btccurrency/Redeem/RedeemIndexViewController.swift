//
//  RedeemIndexViewController.swift
//  btccurrency
//
//  Created by admin on 2023/6/25.
//

import UIKit
import SnapKit
import Then

class RedeemIndexViewController: YBaseViewController {

    private let topBGView = UIView().then { v in
        v.backgroundColor = .primaryBranding
        v.layer.cornerRadius = 24
        v.layer.maskedCorners = [.layerMinXMaxYCorner, .layerMaxXMaxYCorner]
    }
    private let topBgView = UIView().then { v in
        v.backgroundColor = .bgSecondary
    }
    private let topContentBgView = UIView().then { v in
        v.backgroundColor = .primaryBranding
        v.layer.cornerRadius = 24
        v.backgroundColor = .basicPurpleLight
    }
    
    private let vcTitle = UILabel().then { v in
        v.font = .robotoBold(with: 20)
        v.textColor = .white
        v.text = "Redeem".localized()
    }
    
    private let pointTitleLabel = UILabel().then { v in
        v.font = .robotoRegular(with: 14)
        v.textColor = .white
        v.textAlignment = .center
        v.text = "Total points".localized()
    }
    
    private let pointContainer = UIView()
    
    private let pointIconView = UIImageView().then { v in
        v.image = UIImage(named: "n_point_icon")
    }
    
    private let pointLabel = UILabel().then { v in
        v.textColor = .white
        v.font = .robotoBold(with: 40)
    }
    
    private let iconContainer = UIView().then { v in
        v.backgroundColor = .primaryBranding
        v.layer.borderWidth = 1
        v.layer.borderColor = UIColor.white.cgColor
        v.layer.cornerRadius = 16
    }
    private let detailBtn = UIButton(type: .custom)
    private let historyBtn = UIButton(type: .custom)
    
    private let detailIcon = UIImageView().then { v in
        v.image = UIImage(named: "n_record")
        v.isUserInteractionEnabled = true
    }
    
    private let detailLabel = UILabel().then { v in
        v.font = .robotoRegular(with: 14)
        v.textColor = .white
        v.textAlignment = .center
        v.text = "points details".localized()
        v.isUserInteractionEnabled = true
    }

    private let historyIcon = UIImageView().then { v in
        v.image = UIImage(named: "n_gift")
        v.isUserInteractionEnabled = true
    }
    
    private let historyLabel = UILabel().then { v in
        v.font = .robotoRegular(with: 14)
        v.textColor = .white
        v.textAlignment = .center
        v.text = "redeem history".localized()
        v.isUserInteractionEnabled = true
    }
    
    private let sepLine = UIView().then { v in
        v.backgroundColor = .white
    }
    
    private let tableView = UITableView(frame: .zero, style: .plain).then { v in
        v.rowHeight = 198 + 8
        v.backgroundColor = .bgSecondary
        v.separatorStyle = .none
        v.tableHeaderView = UIView(frame: CGRect(x: 0, y: 0, width: UIDevice.kScreenWidth(), height: 16))
        v.showsVerticalScrollIndicator = false
    }
    
    // data
    private var data = ["", "", ""]
    
    // MARK: Super Method
    override func viewDidLoad() {
        super.viewDidLoad()
        navigationView.backMode = .none
        navigationView.titleLabel.text = "Redeem".localized()
        view.backgroundColor = .white
        p_setElements()
        updateViewConstraints()
    }
    
    override func updateViewConstraints() {
        super.updateViewConstraints()
        topBgView.snp.makeConstraints { make in
            make.edges.equalTo(topBGView)
        }
        topBGView.snp.makeConstraints { make in
            make.left.right.top.equalToSuperview()
            make.height.equalTo(256 + UIDevice.kStatusBarHeight())
        }
        tableView.snp.makeConstraints { make in
            make.left.right.equalToSuperview()
            make.top.equalTo(topBGView.snp.bottom)
            make.bottom.equalTo(view.safeAreaLayoutGuide.snp.bottom)
        }
        vcTitle.snp.makeConstraints { make in
            make.left.equalTo(topContentBgView)
            make.height.equalTo(28)
            make.width.greaterThanOrEqualTo(74)
            make.bottom.equalTo(topContentBgView.snp.top).offset(-16)
        }
        topContentBgView.snp.makeConstraints { make in
            make.left.equalToSuperview().offset(20)
            make.right.equalToSuperview().offset(-20)
            make.bottom.equalToSuperview().offset(-24)
            make.height.equalTo(180)
        }
        pointTitleLabel.snp.makeConstraints { make in
            make.height.equalTo(16)
            make.top.equalToSuperview().offset(16)
            make.left.right.equalToSuperview()
        }
        pointContainer.snp.makeConstraints { make in
            make.height.equalTo(52)
            make.centerX.equalToSuperview()
            make.top.equalTo(pointTitleLabel.snp.bottom).offset(4)
            make.width.greaterThanOrEqualTo(136)
        }
        pointIconView.snp.makeConstraints { make in
            make.size.equalTo(34)
            make.centerY.equalToSuperview()
            make.left.equalToSuperview()
        }
        pointLabel.snp.makeConstraints { make in
            make.left.equalTo(pointIconView.snp.right).offset(8)
            make.top.bottom.equalToSuperview()
            make.width.greaterThanOrEqualTo(40)
        }
        iconContainer.snp.makeConstraints { make in
            make.height.equalTo(60)
            make.centerX.equalToSuperview()
            make.top.equalTo(pointContainer.snp.bottom).offset(16)
            make.width.equalTo(240)
        }
        detailIcon.snp.makeConstraints { make in
            make.size.equalTo(24)
            make.centerX.equalToSuperview().multipliedBy(0.5)
            make.top.equalToSuperview().offset(8)
        }
        detailLabel.snp.makeConstraints { make in
            make.height.equalTo(16)
            make.centerX.equalTo(detailIcon)
            make.top.equalTo(detailIcon.snp.bottom).offset(4)
            make.width.greaterThanOrEqualTo(84)
        }
        detailBtn.snp.makeConstraints { make in
            make.left.right.centerX.equalTo(detailLabel)
            make.height.equalToSuperview()
        }
        sepLine.snp.makeConstraints { make in
            make.centerX.equalToSuperview()
            make.width.equalTo(1.5)
            make.top.equalToSuperview().offset(8)
            make.bottom.equalToSuperview().offset(-8)
        }
        historyIcon.snp.makeConstraints { make in
            make.size.equalTo(24)
            make.centerX.equalToSuperview().multipliedBy(1.5)
            make.top.equalTo(detailIcon)
        }
        historyLabel.snp.makeConstraints { make in
            make.height.equalTo(16)
            make.top.width.equalTo(detailLabel)
            make.centerX.equalTo(historyIcon)
        }
        historyBtn.snp.makeConstraints { make in
            make.left.right.centerX.equalTo(historyLabel)
            make.height.equalToSuperview()
        }
    }
    
    // MARK: Private Method
    private func p_setElements() {
        tableView.delegate = self
        tableView.dataSource = self
        view.addSubviews([topBgView, topBGView, tableView])
        topBGView.addSubviews([vcTitle, topContentBgView])
        topContentBgView.addSubviews([
            pointTitleLabel,
            pointContainer,
            iconContainer
        ])
        pointContainer.addSubviews([pointIconView, pointLabel])
        iconContainer.addSubviews([detailIcon, detailLabel, sepLine, historyIcon, historyLabel, detailBtn, historyBtn])
        detailBtn.rx.tap.subscribe(onNext: { _ in
            let vc = RedeemPointViewController()
            self.navigationController?.pushViewController(vc, animated: true)
        }).disposed(by: rx.disposeBag)
        historyBtn.rx.tap.subscribe(onNext: { _ in
            let vc = RedeemHistoryViewController()
            self.navigationController?.pushViewController(vc, animated: true)
        }).disposed(by: rx.disposeBag)
        p_refresh()
    }
    
    private func p_refresh() {
        pointLabel.text = "1,000"
    }
}

extension RedeemIndexViewController: UITableViewDelegate, UITableViewDataSource {
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return data.count
    }
    
    func numberOfSections(in tableView: UITableView) -> Int {
        return 1
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cellIdentifer = "cellIdentifer"
        var cell = tableView.dequeueReusableCell(withIdentifier: cellIdentifer)
        if cell == nil {
            cell = RedeemIndexCell.init(style: .default, reuseIdentifier: cellIdentifer)
        }
        let mCell = cell as! RedeemIndexCell
        mCell.selectionStyle = .none
        mCell.setData()
        return mCell
    }
}
