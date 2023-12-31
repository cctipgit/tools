//
//  ProfileIndexViewController.swift
//  cryptovise
//
//  Created by admin on 2023/6/28.
//

import UIKit
import SnapKit
import Then
import RxSwift
import RxCocoa

class ProfileIndexViewController: YBaseViewController {
    private let topBgView = UIView().then { v in
        v.layer.cornerRadius = 24
        v.layer.maskedCorners = [.layerMinXMaxYCorner, .layerMaxXMaxYCorner]
        v.backgroundColor = .primaryBranding
    }
    
    private let avatarImgView = UIImageView().then { v in
        v.layer.cornerRadius = 32
        v.layer.borderColor = UIColor(red: 0.98, green: 0.63, blue: 0.12, alpha: 1).cgColor
        v.layer.borderWidth = 1.5
        v.clipsToBounds = true
    }
    
    private let nameLabel = UILabel().then { v in
        v.textAlignment = .center
        v.font = .robotoBold(with: 18)
        v.textColor = .white
    }
    
    private let joinLabel = UILabel().then {v in
        v.textAlignment = .center
        v.font = .robotoRegular(with: 16)
        v.textColor = .white
    }
    
    private let pointLabel = UILabel().then { v in
        v.textAlignment = .center
        v.layer.cornerRadius = 8
        v.textColor = .white
        v.backgroundColor = .basicPurpleLight
        v.clipsToBounds = true
    }
    
    private let titles: [String] = ["Settings".localized(),
                                    "Privacy Policy".localized(),
                                    "About".localized()]
    private let tableView = UITableView(frame: .zero, style: .plain).then { tb in
        tb.rowHeight = 64
        tb.separatorStyle = .none
    }
    
    // MARK: Super Method
    override func viewDidLoad() {
        super.viewDidLoad()
        navigationView.backMode = .normal
        navigationView.pinMode = .none
        navigationView.titleMode = .left
        navigationView.titleLabel.text = "Profile".localized()
        navigationView.titleLabel.textColor = .white
        navigationView.backButton.setImage(UIImage(named: "n_back_white"), for: .normal)
        navigationView.backButton.setImage(UIImage(named: "n_back_white"), for: .highlighted)
        
        self.view.sendSubviewToBack(topBgView)
        p_setElements()
        updateViewConstraints()
    }
    
    override func updateViewConstraints() {
        super.updateViewConstraints()
        avatarImgView.snp.makeConstraints { make in
            make.size.equalTo(64)
            make.centerX.equalToSuperview()
            make.bottom.equalTo(nameLabel.snp.top).offset(-8)
        }
        nameLabel.snp.makeConstraints { make in
            make.left.right.equalToSuperview()
            make.height.equalTo(24)
            make.bottom.equalTo(joinLabel.snp.top)
        }
        joinLabel.snp.makeConstraints { make in
            make.left.right.equalToSuperview()
            make.bottom.equalTo(pointLabel.snp.top).offset(-24)
            make.height.equalTo(20)
        }
        pointLabel.snp.makeConstraints { make in
            make.height.equalTo(28)
            make.centerX.equalToSuperview()
            make.bottom.equalToSuperview().offset(-24)
            make.width.greaterThanOrEqualTo(120)
        }
        topBgView.snp.makeConstraints { make in
            make.left.right.top.equalToSuperview()
            make.height.equalTo(272 + UIDevice.kStatusBarHeight())
        }
        tableView.snp.makeConstraints { make in
            make.left.right.equalToSuperview()
            make.top.equalTo(topBgView.snp.bottom).offset(24)
            make.bottom.equalTo(self.view.safeAreaLayoutGuide.snp.bottom)
        }
    }
    
    // MARK: Public Method
    public func setLocalProfile(image: UIImage?, name: String?) {
        avatarImgView.image = image
        nameLabel.text = name
    }
    
    // MARK: Private Method
    private func p_setElements() {
        topBgView.addSubviews([avatarImgView, nameLabel, joinLabel, pointLabel])
        view.addSubviews([topBgView, tableView])
        navigationView.backgroundColor = .primaryBranding
        
        view.bringSubviewToFront(navigationView)
        tableView.delegate = self
        tableView.dataSource = self
        let info = self.getUserInfo()
        setLocalProfile(image: UIImage(named: info.avatarImgName), name: info.userName)
        p_refresh()
        _ = tableView.configMJHeader { [weak self] in
            guard let self = self else { return }
            self.p_refresh()
        }
        NotificationCenter.default
                .rx
                .notification(Notification.Name(drawPrizeChanceChangedNofification))
                .subscribe(onNext: { [weak self] _ in
                    guard let self else { return }
                    self.p_refresh()
                })
                .disposed(by: rx.disposeBag)
    }
    
    private func p_refresh() {
        SmartService().userProfile().subscribe(onNext: { [weak self] result in
            guard let res = result, res.isSuccess, let self = self else { return }
            self.joinLabel.text = TimeInterval(res.data.created / 1000).customJoinTime()
            self.pointLabel.text = "\(res.data.point)".decimalFormat() + " points".localized()
            self.tableView.mj_header?.endRefreshing()
        })
        .disposed(by: rx.disposeBag)
    }
}

extension ProfileIndexViewController: UITableViewDataSource, UITableViewDelegate {
    func numberOfSections(in tableView: UITableView) -> Int {
        return 1
    }
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return titles.count
    }
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cellIdentifier = "cellIdentifier"
        var cell = tableView.dequeueReusableCell(withIdentifier: cellIdentifier)
        if cell == nil {
            cell = ProfileIndexCell.init(style: .default, reuseIdentifier: cellIdentifier)
            cell?.selectionStyle = .none
        }
        let mCell = cell as! ProfileIndexCell
        mCell.setData(title: titles[indexPath.row])
        return mCell
    }
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        var vc: UIViewController!
        if indexPath.row == 0 {
            vc = SettingViewController()
        } else if indexPath.row == 1 {
            vc = ProfilePrivacyViewController()
        } else if indexPath.row == 2 {
            vc = ProfileAboutViewController()
        }
        self.navigationController?.pushViewController(vc)
    }
}
