//
//  SettingSelectViewController.swift
//  cryptovise
//
//  Created by fd on 2022/11/3.
//

import UIKit

enum SettingSelectType: String {
    case defaultCurrency = "Default Value"
    case troyWeight = "Troy weight"
}

class SettingSelectViewController: YBaseViewController {
    var datasource: [SettingSelectViewModel] = []

    var tableView = UITableView(frame: .zero).then { tv in

        tv.register(nib: UINib(nibName: "SettingSelectTableViewCell", bundle: nil),
                    withCellClass: SettingSelectTableViewCell.self)
        tv.backgroundColor = .backgroundColor
        tv.separatorStyle = .none
        tv.tableFooterView = UIView()
    }

    var settingType: SettingSelectType = .defaultCurrency {
        didSet {
            switch settingType {
            case .defaultCurrency:
                datasource = [
                    SettingSelectViewModel(name: "1", isSelect: false),
                    SettingSelectViewModel(name: "10", isSelect: false),
                    SettingSelectViewModel(name: "100", isSelect: false),
                    SettingSelectViewModel(name: "1000", isSelect: false),
                    SettingSelectViewModel(name: "10000", isSelect: false),
                ]

                let string = AppSetting.shared.defaultCurrencyValue.value
                datasource.filter { $0.name == string }.first?.isSelect = true

            case .troyWeight:
                datasource = [
                    SettingSelectViewModel(name: "g", isSelect: false),
                    SettingSelectViewModel(name: "Once troy", isSelect: false),
                    SettingSelectViewModel(name: "Taels troy", isSelect: false),
                    SettingSelectViewModel(name: "Tael(H.K)", isSelect: false),
                    SettingSelectViewModel(name: "Tael(J.P)", isSelect: false),
                    SettingSelectViewModel(name: "Tael(T.W)", isSelect: false),
                    SettingSelectViewModel(name: "Tola", isSelect: false),
                ]
                let string = AppSetting.shared.troyWeight.value
                datasource.filter { $0.name == string }.first?.isSelect = true
            }
        }
    }

    override func makeUI() {
        view.addSubview(tableView)

        tableView.dataSource = self
        tableView.delegate = self

        navigationView.titleLabel.text = settingType.rawValue
        navigationView.pinMode = .none
    }

    override func makeLayout() {
        tableView.pin.horizontally()
            .below(of: navigationView)
            .bottom()
    }
}

extension SettingSelectViewController: UITableViewDataSource, UITableViewDelegate {
    func numberOfSections(in tableView: UITableView) -> Int {
        return 1
    }

    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return datasource.count
    }

    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: String(describing: SettingSelectTableViewCell.self)) as! SettingSelectTableViewCell

        let item = datasource[indexPath.row]

        cell.nameLabel.text = item.name
        cell.selectedImageView.isHidden = !item.isSelect
        return cell
    }

    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        tableView.deselectRow(at: indexPath, animated: true)

        switch settingType {
        case .troyWeight:
            AppSetting.shared.troyWeight
                .accept(datasource[indexPath.row].name)

        case .defaultCurrency:
            AppSetting.shared.defaultCurrencyValue
                .accept(datasource[indexPath.row].name)
        }

        navigationController?.popViewController(animated: true)
    }
}
