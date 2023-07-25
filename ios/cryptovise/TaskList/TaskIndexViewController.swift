//
//  GameIndexViewController.swift
//  cryptovise
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
import RAMAnimatedTabBarController
import SafariServices
import AppsFlyerLib

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
    
    let taskLabel = UILabel().then { v in
        v.font = .robotoRegular(with: 16)
        v.textColor = .primaryYellow
    }
    
    let timeLabel = UILabel().then { v in
        v.font = .robotoRegular(with: 14)
        v.frame = CGRect(x: UIDevice.kScreenWidth() - 94, y: 20, width: 74, height: 16)
        v.text = "0h:0m:0s"
    }
    
    // data
    private var data = [TaskListItem]()
    private var leftTime: TimeInterval = 0
    private var isFirstLoad: Bool = true
    
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
        tableView.emptyDataSetDelegate = self
        tableView.emptyDataSetDataSource = self
        _ = tableView.configMJHeader { [unowned self] in
            self.p_refresh()
        }
        view.addSubviews([vcTitle, tableView])
        navigationView.addSubviews([gameIcon, gameCorner, gameBtn])
        gameCorner.addSubview(gameLabel)
        gameBtn.rx.tap.subscribe(onNext: { _ in
            self.navigationController?.pushViewController(TaskGameViewController(), animated: true)
        })
        .disposed(by: rx.disposeBag)
        Observable<Int>.interval(RxTimeInterval.seconds(1), scheduler: MainScheduler.instance)
            .subscribe(onNext: { [weak self] _ in
                guard let self = self else { return }
                if leftTime > 0 {
                    self.leftTime = self.leftTime - 1
                    self.timeLabel.text = self.leftTime.customTaskLeftTime()
                } else {
                    self.timeLabel.text = "0h:0m:0s"
                }
            })
            .disposed(by: rx.disposeBag)
        NotificationCenter.default
                .rx
                .notification(Notification.Name(drawPrizeChanceChangedNofification))
                .subscribe(onNext: { [weak self] _ in
                    guard let self else { return }
                    self.p_refresh()
                })
                .disposed(by: rx.disposeBag)
        p_setTaskCount(done: 0, total: 0)
        p_refresh()
    }
    
    private func p_setTaskCount(done: Int, total: Int) {
        taskLabel.text = "Daily tasks".localized() + " \(done)/\(total)"
    }
    
    private func p_refresh() {
        SmartService().taskList()
            .subscribe(onNext: { [weak self] result in
                guard let self = self else { return }
                guard let res = result else {
                    self.tableView.mj_header?.endRefreshing()
                    return
                }
                guard let listArray = res.list as? [TaskListItem] else {
                    self.tableView.mj_header?.endRefreshing()
                    return
                }
                var spinNumStr: String = "\(res.pinNum)"
                if res.pinNum > 99 {
                    spinNumStr = "99+"
                }
                self.leftTime = TimeInterval(res.expireTime / 1000) - Date().timeIntervalSince1970
                self.gameLabel.text = spinNumStr
                self.p_setTaskCount(done: listArray.filter({ $0.done == true }).count, total: res.list.count)
                self.data = (res.list as? [TaskListItem]) ?? [TaskListItem]()
                self.tableView.reloadData()
                self.tableView.mj_header?.endRefreshing()
                self.isFirstLoad = false
            })
            .disposed(by: rx.disposeBag)
    }
    
    @objc
    private func p_finishTask(sender: UIButton) {
        sender.isEnabled = false
        func submitAndRefresh(data: TaskListItem) {
            SmartService().taskCheck(param: data.params, taskId: data.taskId)
                .subscribe(onNext: { [weak self ] result in
                    guard let self = self else { return }
                    self.view.makeToast(result?.msg ?? "", duration: 0.3, point: self.view.center, title: nil, image: nil) { didTap in
                    self.view.hideAllToasts()
                    if let res = result, res.isSuccess {
                        self.p_refresh()
                    }
                }
            }).disposed(by: self.rx.disposeBag)
        }
        guard data.count > sender.tag else {
            sender.isEnabled = true
            return
        }
        let data = data[sender.tag]
        switch data.taskTypeEnum {
        case .TaskType_ShareToFacebook,
                .TaskType_ShareToTwitter,
                .TaskType_ShareToTelegram,
                .TaskType_ShareToDiscord,
                .TaskType_SignIn:
            self.share(text: data.taskName, image: nil, url: URL(string: data.params)) {
                submitAndRefresh(data: data)
            }
            break
        case .TaskType_AskFriendToRegister_1,
                .TaskType_AskFriendToRegister_3,
                .TaskType_AskFriendToRegister_5:
            let task = self.getTaskInfo()
            task.inviteFriendCount += 1
            self.setLocalTaskModel(model: task)
            var needCount: Int = 0
            if data.taskTypeEnum == .TaskType_AskFriendToRegister_1 {
                needCount = 1
            } else if data.taskTypeEnum == .TaskType_AskFriendToRegister_3 {
                needCount = 3
            } else if data.taskTypeEnum == .TaskType_AskFriendToRegister_5 {
                needCount = 5
            }
            guard task.inviteFriendCount >= needCount else {
                sender.isEnabled = true
                let infoDictionary = Bundle.main.infoDictionary!
                let appDisplayName: String = (infoDictionary["CFBundleDisplayName"] as? String) ?? ""
                return self.share(text: appDisplayName, image: nil, url: URL(string: data.params)) { }
            }
            submitAndRefresh(data: data)
            break
        case .TaskType_Add_Token_1,
                .TaskType_Add_Token_3:
            let task = self.getTaskInfo()
            var needCount: Int = 1
            if data.taskTypeEnum == .TaskType_Add_Token_3 {
                needCount = 3
            }
            guard task.addTokenCount >= needCount else {
                sender.isEnabled = true
                if let vc = self.navigationController?.viewControllers.first as? RAMAnimatedTabBarController {
                    vc.setSelectIndex(from: vc.selectedIndex, to: 1)
                }
                return
            }
            submitAndRefresh(data: data)
            break
        case .TaskType_View_Token_1,
                .TaskType_View_Token_3:
            let task = self.getTaskInfo()
            var needCount: Int = 1
            if data.taskTypeEnum == .TaskType_View_Token_3 {
                needCount = 3
            }
            guard task.viewDetailCount >= needCount else {
                sender.isEnabled = true
                if let vc = self.navigationController?.viewControllers.first as? RAMAnimatedTabBarController {
                    vc.setSelectIndex(from: vc.selectedIndex, to: 1)
                }
                return
            }
            submitAndRefresh(data: data)
            break
        case .TaskType_Quiz_Done:
            let task = self.getTaskInfo()
            guard Date().timeIntervalSince1970.customJoinTime() == task.quizDoneTime else {
                sender.isEnabled = true
                if let vc = self.navigationController?.viewControllers.first as? RAMAnimatedTabBarController {
                    vc.setSelectIndex(from: vc.selectedIndex, to: 2)
                }
                return
            }
            submitAndRefresh(data: data)
            break
        case .TaskType_Visit_Website:
            guard let url = URL(string: data.params) else {
                sender.isEnabled = true
                return
            }
            let safari = SFSafariViewController(url: url)
            self.present(safari, animated: true) {
                submitAndRefresh(data: data)
            }
            break
           
        case .TaskType_Product_Exchange:
            let task = self.getTaskInfo()
            guard task.redeemCount > 0  else {
                sender.isEnabled = true
                if let vc = self.navigationController?.viewControllers.first as? RAMAnimatedTabBarController {
                    vc.setSelectIndex(from: vc.selectedIndex, to: 3)
                }
                return
            }
            submitAndRefresh(data: data)
            break
        case .TaskType_Product_Get_During_10_12,
                .TaskType_Product_Get_During_14_16:
            let task = self.getTaskInfo()
            var count: Int = 0
            if data.taskTypeEnum == .TaskType_Product_Get_During_10_12 {
                count = task.pin1012Count
            } else {
                count = task.pin1416Count
            }
            guard count > 0 else {
                sender.isEnabled = true
                let vc = TaskGameViewController()
                self.navigationController?.pushViewController(vc, animated: true)
                return
            }
            submitAndRefresh(data: data)
            break
        case .TaskType_Product_Get_App_Star:
            let url = URL(string: "itms-apps://itunes.apple.com/app/id\(AppConfig.appstoreAppId)")
            if !UIApplication.shared.canOpenURL(url!) {
                sender.isEnabled = true
                return
            }
            UIApplication.shared.open(url!, options: [:])
            submitAndRefresh(data: data)
            break
        case .unknown:
            sender.isEnabled = true
            break
        }
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
        mCell.setData(item: data[indexPath.row])
        mCell.statusBtn.tag = indexPath.row
        mCell.statusBtn.addTarget(self, action: #selector(p_finishTask(sender:)), for: .touchUpInside)
        return mCell
    }
    
    func tableView(_ tableView: UITableView, viewForHeaderInSection section: Int) -> UIView? {
        let header = UIView(frame: CGRect(x: 0, y: 0, width: UIDevice.kScreenWidth(), height: 52)).then { v in
            v.backgroundColor = .white
        }
        let icon = UIImageView().then { v in
            v.image = UIImage(named: "n_clock_icon")
            v.frame = CGRect(x: UIDevice.kScreenWidth() - 126, y: 16, width: 24, height: 24)
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
            make.width.greaterThanOrEqualTo(74)
        }
        return header
    }
    
    func tableView(_ tableView: UITableView, heightForHeaderInSection section: Int) -> CGFloat {
        return 52
    }
    
    func tableView(_ tableView: UITableView, willDisplay cell: UITableViewCell, forRowAt indexPath: IndexPath) {
        if isFirstLoad {
            let count = data.count
            cell.alpha = 0
            DispatchQueue.main.asyncAfter(deadline: .now() + Double(count) * 0.01) {
                cell.layer.transform = CATransform3DMakeTranslation(-cell.width, 0, 0)
                UIView.animate(withDuration: 0.25, delay: 0.2, usingSpringWithDamping: 0.75, initialSpringVelocity: 0) {
                    cell.layer.transform = CATransform3DIdentity
                    cell.alpha = 1
                }
            }
        }
    }
}

extension TaskIndexViewController: TBEmptyDataSetDelegate, TBEmptyDataSetDataSource {
    func titleForEmptyDataSet(in scrollView: UIScrollView) -> NSAttributedString? {
        return NSAttributedString(string: "No record yet".localized(),
                                  attributes: [NSAttributedString.Key.font: UIFont.robotoRegular(with: 16) ?? .systemFont(ofSize: 16),
                                               NSAttributedString.Key.foregroundColor: UIColor.contentSecondary])
    }
    
    func verticalOffsetForEmptyDataSet(in scrollView: UIScrollView) -> CGFloat {
        return -UIDevice.kScreenHeight() * 0.35
    }
    
    func emptyDataSetScrollEnabled(in scrollView: UIScrollView) -> Bool {
        return true
    }
}
