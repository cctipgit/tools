//
//  CurrencySheetViewController.swift
//  btccurrency
//
//  Created by admin on 2023/6/27.
//

import UIKit
import SnapKit
import Then

protocol CurrencySheetDelegate: AnyObject {
    func didCurrencySheetSelected(type: SwitchVCSelectType)
}

class CurrencySheetViewController: UIViewController {
    weak var delegate: CurrencySheetDelegate?
    
    private let sheetBgView = UIView().then { v in
        v.backgroundColor = .white
        v.layer.cornerRadius = 16
        v.layer.maskedCorners = [.layerMinXMinYCorner, .layerMaxXMinYCorner]
        v.clipsToBounds = true
    }
    private let lineView = UIView().then { v in
        v.backgroundColor = UIColor.black.alpha(0.6)
        v.layer.cornerRadius = 2.5
    }
    public let backBtn = UIButton(type: .custom).then { btn in
        btn.setImage(UIImage(named: "n_back"), for: .normal)
        btn.setImage(UIImage(named: "n_back"), for: .highlighted)
    }
    private let titleLabel = UILabel().then { lb in
        lb.text = "Select".localized()
        lb.font = .robotoBold(with: 20)
        lb.textColor = .black
    }
    private let tableView = UITableView(frame: .zero, style: .plain).then { table in
        table.rowHeight = 56
        table.separatorStyle = .none
    }
    weak var currentSelectCell: CurrencyBottomSheetCell?

    var selectType: SwitchVCSelectType = .selectToken
    
    // MARK: Super method
    convenience init(selectType: SwitchVCSelectType) {
        self.init(nibName: nil, bundle: nil)
        self.selectType = selectType
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        p_setElements()
        updateViewConstraints()
    }
    
    override func updateViewConstraints() {
        super.updateViewConstraints()
        sheetBgView.snp.makeConstraints { make in
            make.edges.equalToSuperview()
        }
        lineView.snp.makeConstraints { make in
            make.centerX.equalToSuperview()
            make.height.equalTo(5)
            make.width.equalToSuperview().multipliedBy(139.0 / 428.0)
            make.top.equalToSuperview().offset(8)
        }
        backBtn.snp.makeConstraints { make in
            make.width.height.equalTo(24)
            make.left.equalToSuperview().offset(16)
            make.top.equalToSuperview().offset(37)
        }
        titleLabel.snp.makeConstraints { make in
            make.centerY.equalTo(backBtn)
            make.left.equalTo(backBtn.snp.right).offset(16)
            make.height.equalTo(28)
            make.width.greaterThanOrEqualTo(57)
        }
        tableView.snp.makeConstraints { make in
            make.left.equalToSuperview().offset(16)
            make.right.equalToSuperview().offset(-16)
            make.bottom.equalToSuperview().offset(-24)
            make.top.equalTo(backBtn.snp.bottom).offset(24)
        }
    }
    
    // MARK: Priavte method
    private func p_setElements() {
        tableView.delegate = self
        tableView.dataSource = self
        sheetBgView.addSubviews([lineView, backBtn, titleLabel, tableView])
        self.view.addSubview(sheetBgView)
    }
}

extension CurrencySheetViewController: UITableViewDataSource, UITableViewDelegate {
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return SwitchVCSelectType.allCases.count
    }
    
    func numberOfSections(in tableView: UITableView) -> Int {
        return 1
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cellIdentifier = "cellIdentifier"
        var cell = tableView.dequeueReusableCell(withIdentifier: cellIdentifier)
        if cell == nil {
            cell = CurrencyBottomSheetCell(style: .default, reuseIdentifier: cellIdentifier)
        }
        let mCell = cell as! CurrencyBottomSheetCell
        mCell.selectionStyle = .none
        let data = SwitchVCSelectType.allCases[indexPath.row]
        mCell.setData(title: data.rawValue)
        if self.selectType == data {
            currentSelectCell = mCell
            mCell.setSelected(isSelected: true)
        }
        return mCell
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        tableView.deselectRow(at: indexPath, animated: false)
        currentSelectCell?.setSelected(isSelected: false)
        let cell = tableView.cellForRow(at: indexPath) as! CurrencyBottomSheetCell
        cell.setSelected(isSelected: true)
        currentSelectCell = cell
        self.delegate?.didCurrencySheetSelected(type: SwitchVCSelectType.allCases[indexPath.row])
    }
}
