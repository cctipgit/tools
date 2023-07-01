//
//  GameIndexViewController.swift
//  btccurrency
//
//  Created by admin on 2023/6/25.
//

import UIKit
import SnapKit
import Then
import TBEmptyDataSet
import RxCocoa
import RxSwift
import MJRefresh

class TaskIndexViewController: YBaseViewController {

    private let vcTitle = UILabel().then { v in
        v.font = .robotoBold(with: 20)
        v.textColor = .basicBlk
        v.text = "Task list".localized()
    }
    
    private let gameIcon = UIImageView().then { v in
        v.image = UIImage(named: "n_game_icon")
        v.isUserInteractionEnabled = true
    }
    
    private let gameCorner = UIImageView().then { v in
        v.image = UIImage(named: "n_game_star")
        v.isUserInteractionEnabled = true
    }
    
    private var gameLabel = UILabel().then { v in
        v.textColor = .white
        v.font = .robotoRegular(with: 12)
        v.isUserInteractionEnabled = true
        v.textAlignment = .center
    }
    
    private let gameBtn = UIButton()
    
    private let tableView = UITableView(frame: .zero, style: .plain).then { v in
        v.rowHeight = 74
        v.backgroundColor = .backgroundColor
        if #available(iOS 15.0, *) {
            v.sectionHeaderTopPadding = 0
        }
        v.separatorStyle = .none
        v.showsVerticalScrollIndicator = false
        v.showsHorizontalScrollIndicator = false
        v.contentInset = .zero
    }
    
    // data
    private var data = ["", "", "", "", "", "", "", "", "", "", "", ""]
    
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
        vcTitle.snp.makeConstraints { make in
            make.left.equalToSuperview().offset(20)
            make.height.equalTo(28)
            make.width.greaterThanOrEqualTo(74)
            make.centerY.equalTo(self.navigationView)
        }
        tableView.snp.makeConstraints { make in
            make.top.equalTo(navigationView.snp.bottom)
            make.left.right.equalToSuperview()
            make.bottom.equalTo(view.safeAreaLayoutGuide.snp.bottom)
        }
        gameIcon.snp.makeConstraints { make in
            make.size.equalTo(40)
            make.centerY.equalToSuperview()
            make.right.equalToSuperview().offset(-30)
        }
        gameCorner.snp.makeConstraints { make in
            make.size.equalTo(24)
            make.right.equalToSuperview().offset(-20)
            make.top.equalTo(gameIcon).offset(-3)
        }
        gameLabel.snp.makeConstraints { make in
            make.edges.equalTo(gameCorner)
        }
        gameBtn.snp.makeConstraints { make in
            make.left.bottom.equalTo(gameIcon)
            make.top.right.equalTo(gameCorner)
        }
    }
    
    // MARK: Private Method
    private func p_setElements() {
        tableView.delegate = self
        tableView.dataSource = self
        _ = tableView.configMJHeader { [unowned self] in
            self.p_refresh()
        }
        view.addSubviews([vcTitle, tableView])
        navigationView.addSubviews([gameIcon, gameCorner, gameBtn])
        gameCorner.addSubview(gameLabel)
        gameBtn.rx.tap.subscribe(onNext: { _ in
            let vc = TaskGameViewController()
            self.navigationController?.pushViewController(vc, animated: true)
        })
        .disposed(by: rx.disposeBag)
        p_refresh()
    }
    
    private func p_refresh() {
        tableView.reloadData()
        gameLabel.text = "11"
        tableView.mj_header?.endRefreshing()
    }
}

extension TaskIndexViewController: UITableViewDelegate, UITableViewDataSource {
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
            cell = TaskIndexCell.init(style: .default, reuseIdentifier: cellIdentifer)
        }
        let mCell = cell as! TaskIndexCell
        mCell.selectionStyle = .none
        mCell.setData()
        return mCell
    }
    
    func tableView(_ tableView: UITableView, viewForHeaderInSection section: Int) -> UIView? {
        let header = UIView(frame: CGRect(x: 0, y: 0, width: UIDevice.kScreenWidth(), height: 52)).then { v in
            v.backgroundColor = .white
        }
        let taskLabel = UILabel().then { v in
            v.font = .robotoRegular(with: 16)
            v.textColor = .primaryYellow
        }
        taskLabel.text = "Daily tasks 0/8"
        let icon = UIImageView().then { v in
            v.image = UIImage(named: "n_clock_icon")
            v.frame = CGRect(x: UIDevice.kScreenWidth() - 126, y: 16, width: 24, height: 24)
        }
        let timeLabel = UILabel().then { v in
            v.font = .robotoRegular(with: 14)
            v.text = "27h:57m:3s"
            v.frame = CGRect(x: UIDevice.kScreenWidth() - 94, y: 20, width: 74, height: 16)
        }
        header.addSubviews([taskLabel, icon, timeLabel])
        taskLabel.snp.makeConstraints { make in
            make.height.equalTo(20)
            make.left.equalToSuperview().offset(20)
            make.centerY.equalToSuperview()
            make.width.greaterThanOrEqualTo(107)
        }
        icon.snp.makeConstraints { make in
            make.size.equalTo(24)
            make.centerY.equalToSuperview()
            make.right.equalTo(timeLabel.snp.left).offset(-8)
        }
        timeLabel.snp.makeConstraints { make in
            make.height.equalTo(16)
            make.centerY.equalToSuperview()
            make.right.equalToSuperview().offset(-20)
            make.right.greaterThanOrEqualTo(74)
        }
        return header
    }
    
    func tableView(_ tableView: UITableView, heightForHeaderInSection section: Int) -> CGFloat {
        return 52
    }
}
