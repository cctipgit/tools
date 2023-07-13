//
//  MoreSettingViewController.swift
//  btccurrency
//
//  Created by fd on 2022/11/3.
//

import RxDataSources
import UIKit
class MoreSettingViewController: YBaseViewController, UIScrollViewDelegate {
    var viewModel = MoreSettingViewModel()

    var tableView = UITableView(frame: .zero).then { tv in
        tv.backgroundColor = .backgroundColor
        tv.separatorStyle = .none
        tv.register(cellWithClass: SettingLineTableViewCell.self)
        tv.register(cellWithClass: SettingTableViewCell.self)
        tv.register(cellWithClass: SettingSwitchTableViewCell.self)
    }

    override func makeUI() {
        view.addSubview(tableView)
        navigationView.pinMode = .pan
    }

    override func makeLayout() {
        tableView.pin.horizontally()
            .below(of: navigationView)
            .bottom()
    }

    override func makeEvent() {
        bindViewModel()
        navigationView.titleLabel.text = "More Setting".localized()
    }

    func bindViewModel() {
        let datasource = RxTableViewSectionedReloadDataSource<MoreSecttingSection> { _, tv, _, item in
            switch item {
            case .placeholder:
                let cell = tv.dequeueReusableCell(withIdentifier: String(describing: SettingLineTableViewCell.self)) as! SettingLineTableViewCell

                return cell

            case let .plugin(viewModel),
                 let .quoteColor(viewModel),
                let .language(viewModel),
                 let .troyWeight(viewModel):
                let cell = tv.dequeueReusableCell(withIdentifier: String(describing: SettingTableViewCell.self)) as! SettingTableViewCell
                cell.bind(viewModel: viewModel)
                return cell
                
            case let .keyboardSound(viewModel):
                let cell = tv.dequeueReusableCell(withIdentifier: String(describing: SettingSwitchTableViewCell.self)) as! SettingSwitchTableViewCell

                cell.bind(viewModel: viewModel)
                return cell
            }
        }

        let trigger = rx.viewWillAppear

        let output = viewModel.transform(input: MoreSettingViewModel.Input(trigger: trigger.mapToVoid(),
                                                                           selection: tableView.rx.modelSelected(MoreSettingSectionItem.self).asDriver()))

        tableView.rx
            .setDelegate(self)
            .disposed(by: rx.disposeBag)
        output.items
            .asObservable()
            .bind(to: tableView.rx.items(dataSource: datasource))
            .disposed(by: rx.disposeBag)

        tableView.rx.itemSelected
            .subscribe(onNext: { [weak self] indexpath in
                self?.tableView.deselectRow(at: indexpath, animated: true)
            })
            .disposed(by: rx.disposeBag)
        tableView.rx.modelSelected(MoreSettingSectionItem.self)
            .subscribe(onNext: { [weak self] item in
                guard let self
                else {
                    return
                }
                switch item {
                case .plugin:
                    let vc = PluginSettingViewController()
                    self.navigationController?.pushViewController(vc, animated: true)
                case .quoteColor:
                    let vc = QuoteColorSettingViewController()
                    self.navigationController?.pushViewController(vc, animated: true)
                case .language:
                    let vc = LanguageViewController()
                    self.navigationController?.pushViewController(vc, animated: true)
                case .troyWeight:
                    let vc = SettingSelectViewController()
                    vc.settingType = .troyWeight
                    self.navigationController?.pushViewController(vc, animated: true)
                default:
                    break
                }

            })
            .disposed(by: rx.disposeBag)

        output.selectedEvent.drive(onNext: { item in
            print(item)
        })
        .disposed(by: rx.disposeBag)
    }
}
