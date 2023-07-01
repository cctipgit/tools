//
//  DigitSettingViewController.swift
//  btccurrency
//
//  Created by fd on 2022/11/3.
//

import UIKit

class DigitSettingViewController: YBaseViewController {
    var isBtcCurrency = false
    var tableView = UITableView(frame: .zero).then { tv in
        tv.backgroundColor = .backgroundColor
        tv.separatorStyle = .none
        let nib = UINib(nibName: "SelectDecimalDigitTableViewCell", bundle: nil)
        tv.register(nib: nib, withCellClass: SelectDecimalDigitTableViewCell.self)
        tv.tableFooterView = UIView()
    }

    var datasource = [
        ("none", "1,234"),
        ("2", "1,234.00"),
        ("4", "1,234.0000"),
        ("6", "1,234.000000"),
        ("8", "1,234.00000000"),
        ("10", "1,234.0000000000"),
        ("12", "1,234.000000000000"),
    ]

    override func makeUI() {
        view.addSubview(tableView)
        navigationView.titleLabel.text = "Legal Tender".localized()

        tableView.delegate = self
        tableView.dataSource = self
        navigationView.pinMode = .none
    }

    override func makeLayout() {
        tableView.pin.horizontally()
            .below(of: navigationView)
            .bottom()
    }
}

extension DigitSettingViewController: UITableViewDataSource, UITableViewDelegate {
    func numberOfSections(in tableView: UITableView) -> Int {
        return !isBtcCurrency ? 5 : 7
    }

    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return 1
    }

    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "SelectDecimalDigitTableViewCell", for: indexPath) as! SelectDecimalDigitTableViewCell
        var value = 0
        if isBtcCurrency {
            value = AppSetting.shared.btcDecimalDigit.value
        } else {
            value = AppSetting.shared.legalDecimalDigit.value
        }

        var isMatch = false

        isMatch = value == indexPath.section * 2

        cell.makeSelect(isMatch: isMatch)

        cell.titleLabel.text = datasource[indexPath.section].0
        cell.countLabel.text = datasource[indexPath.section].1
        return cell
    }

    func tableView(_ tableView: UITableView, heightForFooterInSection section: Int) -> CGFloat {
        return 8
    }

    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        var value = 0

        value = indexPath.section * 2

        if isBtcCurrency {
            AppSetting.shared.btcDecimalDigit.accept(value)
        } else {
            AppSetting.shared.legalDecimalDigit.accept(value)
        }

        tableView.reloadData()

        navigationController?.popViewController(animated: true)
    }
}
