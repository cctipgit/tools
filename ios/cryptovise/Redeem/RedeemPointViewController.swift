//
//  RedeemPointViewController.swift
//  cryptovise
//
//  Created by admin on 2023/6/28.
//

import UIKit
import SnapKit
import Then
import TBEmptyDataSet
import RxSwift
import RxCocoa
import MJRefresh

class RedeemPointViewController: YBaseViewController {
    private let totalPointLabel = UILabel().then { v in
        v.font = .robotoRegular(with: 18)
        v.textColor = .contentSecondary
    }
    
    private let tableView = UITableView(frame: .zero, style: .plain).then { v in
        v.rowHeight = 74
        v.backgroundColor = .white
        v.separatorStyle = .none
        v.tableHeaderView = UIView(frame: CGRect(x: 0, y: 0, width: UIDevice.kScreenWidth(), height: 24))
        v.showsVerticalScrollIndicator = false
    }
    
    // data
    private var data = [PointListItem]()
    
    // MARK: Super Method
    override func viewDidLoad() {
        super.viewDidLoad()
        navigationView.backMode = .normal
        navigationView.titleLabel.text = "Points details".localized()
        view.backgroundColor = .white
        p_setElements()
        updateViewConstraints()
    }
    
    override func updateViewConstraints() {
        super.updateViewConstraints()
        totalPointLabel.snp.makeConstraints { make in
            make.left.equalToSuperview().offset(20)
            make.height.equalTo(24)
            make.top.equalTo(navigationView.snp.bottom).offset(16)
            make.width.greaterThanOrEqualTo(150)
        }
        tableView.snp.makeConstraints { make in
            make.top.equalTo(totalPointLabel.snp.bottom)
            make.left.right.equalToSuperview()
            make.bottom.equalTo(self.view.safeAreaLayoutGuide.snp.bottom)
        }
    }
    
    // MARK: Private Method
    private func p_setElements() {
        tableView.delegate = self
        tableView.dataSource = self
        tableView.emptyDataSetDelegate = self
        tableView.emptyDataSetDataSource = self
        view.addSubviews([totalPointLabel, tableView])
        _ = tableView.configMJHeader { [weak self] in
            guard let self = self else { return }
            self.p_refresh()
        }
        p_refresh()
    }
    
    private func p_refresh() {
        SmartService().redeemPointList(page: 1)
            .subscribe(onNext: { [weak self] result in
                guard let self = self else { return }
                guard let res = result else {
                    self.tableView.mj_header?.endRefreshing()
                    return
                }
                self.totalPointLabel.text = "Total points".localized() + ": " + "\(res.totalPoints)".decimalFormat()
                self.data = (res.list as? [PointListItem]) ?? [PointListItem]()
                self.tableView.reloadData()
                self.tableView.mj_header?.endRefreshing()
            })
            .disposed(by: rx.disposeBag)
    }
}

extension RedeemPointViewController: UITableViewDelegate, UITableViewDataSource {
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
            cell = RedeemPointCell.init(style: .default, reuseIdentifier: cellIdentifer)
        }
        let mCell = cell as! RedeemPointCell
        mCell.selectionStyle = .none
        mCell.setData(item: data[indexPath.row])
        return mCell
    }
    
    func tableView(_ tableView: UITableView, willDisplay cell: UITableViewCell, forRowAt indexPath: IndexPath) {
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

extension RedeemPointViewController: TBEmptyDataSetDelegate, TBEmptyDataSetDataSource {
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
