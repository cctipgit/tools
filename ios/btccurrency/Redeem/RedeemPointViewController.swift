//
//  RedeemPointViewController.swift
//  btccurrency
//
//  Created by admin on 2023/6/28.
//

import UIKit
import SnapKit
import Then
import TBEmptyDataSet

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
    private var data = ["String", "String", "String", "String", "String", "String"]
    
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
        p_refresh()
    }
    
    private func p_refresh() {
        totalPointLabel.text = "Total points".localized() + ":" + "1,000"
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
        mCell.setData()
        return mCell
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
}
