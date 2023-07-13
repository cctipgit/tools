//
//  LanguageViewController.swift
//  btccurrency
//
//  Created by fd on 2023/1/29.
//

import UIKit

class LanguageViewController: YBaseViewController {
    var tableView = UITableView(frame: .zero).then { tv in
        tv.backgroundColor = .backgroundColor
        tv.separatorStyle = .none
        tv.register(nib: UINib(nibName: "SettingSelectTableViewCell", bundle: nil),
                    withCellClass: SettingSelectTableViewCell.self)
        tv.backgroundColor = .backgroundColor
        tv.separatorStyle = .none
        tv.tableFooterView = UIView()
    }

    var datasource = LanuageCode.allCases.sorted(by: \.rawValue)

    override func makeUI() {
        view.addSubview(tableView)

        tableView.dataSource = self
        tableView.delegate = self

        navigationView.pinMode = .pan
        navigationView.titleLabel.text = "Language".localized()
    }

    override func makeLayout() {
        tableView.pin.horizontally()
            .below(of: navigationView)
            .bottom()
    }

    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        tableView.reloadData()
    }
}

extension LanguageViewController: UITableViewDataSource, UITableViewDelegate {
    func numberOfSections(in tableView: UITableView) -> Int {
        return 1
    }

    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return datasource.count
    }

    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: String(describing: SettingSelectTableViewCell.self)) as! SettingSelectTableViewCell
        
        cell.nameLabel.text = datasource[indexPath.row].displayName().localized()
        cell.selectedImageView.isHidden = datasource[indexPath.row].displayName() != LocalizeManager.shared.currentDispalyName()
        return cell
    }

    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        tableView.deselectRow(at: indexPath, animated: true)

        LocalizeManager.shared.changeCurrent(lanuage: datasource[indexPath.row])
        dismiss(animated: true)
    }
}
