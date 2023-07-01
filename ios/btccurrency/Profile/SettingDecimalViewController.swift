//
//  SettingDecimalViewController.swift
//  btccurrency
//
//  Created by fd on 2022/11/3.
//

import UIKit

class SettingDecimalViewController: YBaseViewController {
    var tableView = UITableView(frame: .zero).then { tv in
        tv.backgroundColor = .backgroundColor
        tv.separatorStyle = .none
        tv.register(cellWithClass: SettingTableViewCell.self)
        tv.tableFooterView = UIView()
    }

    override func makeUI() {
        view.addSubview(tableView)

        tableView.dataSource = self
        tableView.delegate = self
        
        navigationView.pinMode = .none
        navigationView.titleLabel.text = "Setting point".localized()
    }

    override func makeLayout() {
        tableView.pin.horizontally()
            .below(of: navigationView)
            .bottom()
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        self.tableView.reloadData()
    }
}

extension SettingDecimalViewController: UITableViewDataSource, UITableViewDelegate {
    func numberOfSections(in tableView: UITableView) -> Int {
        return 1
    }

    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return 2
    }

    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: String(describing: SettingTableViewCell.self)) as! SettingTableViewCell

        if indexPath.row == 0 {
            cell.titleLabel.text = "Legal Tender".localized()
            cell.valueLabel.text = String(AppSetting.shared.legalDecimalDigit.value)
        } else {
            cell.titleLabel.text = "Cryptocurrency".localized()
            cell.valueLabel.text = String(AppSetting.shared.btcDecimalDigit.value)
        }
        return cell
    }

    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        tableView.deselectRow(at: indexPath, animated: true)

        let vc = DigitSettingViewController()
        vc.isBtcCurrency = indexPath.row == 1
        navigationController?.pushViewController(vc, animated: true)
    }
}
