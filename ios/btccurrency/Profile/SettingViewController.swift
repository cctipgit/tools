//
//  SettingViewController.swift
//  btccurrency
//
//  Created by fd on 2022/11/2.
//

import RxAppState
import RxDataSources
import UIKit

let resetHomeListNotification = Notification.Name("ResertHomeListNotification")

class SettingViewController: YBaseViewController, UIScrollViewDelegate {
    var viewModel = SettingViewModel()

    var tableView = UITableView(frame: .zero).then { tv in
        tv.backgroundColor = .backgroundColor
        tv.separatorStyle = .none
        tv.register(cellWithClass: SettingLineTableViewCell.self)
        tv.register(cellWithClass: SettingTableViewCell.self)
        tv.register(cellWithClass: SettingSwitchTableViewCell.self)
    }

    override func makeUI() {
        view.addSubview(tableView)

        let container = UIView(frame: CGRectMake(0, 0, view.width, 50 + 40))
        let button = UIButton().then { button in
            button.titleLabel?.font = .robotoRegular(with: 16)
            button.setTitleColor(.contentSecondary, for: .normal)
            button.cornerRadius = 8
            button.setTitle("Reset to default".localized(), for: .normal)
            button.backgroundColor = .bgDisabled
        }
        container.addSubview(button)
        button.sizeToFit()
        button.pin.height(50)
            .hCenter()
            .width(view.width - 40)
            .top(40)
        tableView.tableFooterView = container
        button.rx
            .tap
            .subscribe(onNext: { [weak self] in
                guard let self else { return }
                let alert = CustomAlertView(frame: .zero)
                let window = getWindow()!
                alert.show(in: window, handler: {
                    NotificationCenter.default.post(Notification(name: resetHomeListNotification))
                    self.dismiss(animated: true)
                })
            })
            .disposed(by: rx.disposeBag)
        
        navigationView.backMode = .normal
        navigationView.pinMode = .none
        navigationView.titleMode = .left
        navigationView.titleLabel.text = "Setting".localized()
    }

    override func makeLayout() {
        tableView.pin.below(of: navigationView).marginTop(16).bottom().horizontally()
    }

    override func makeEvent() {
        bindViewModel()
    }

    func bindViewModel() {
        /*
        let datasource = RxTableViewSectionedReloadDataSource<SettingSection> { _, tv, indexpath, item in
            switch item {
            case let .localCurrency(viewModel), let .currencySymbol(viewModel):
                let cell = tv.dequeueReusableCell(withIdentifier: String(describing: SettingSwitchTableViewCell.self)) as! SettingSwitchTableViewCell

                cell.bind(viewModel: viewModel,localMark: indexpath.row == 0)
                return cell
            case .placeholder:
                let cell = tv.dequeueReusableCell(withIdentifier: String(describing: SettingLineTableViewCell.self)) as! SettingLineTableViewCell

                return cell

            case let .defaultCurrencyValue(viewModel),
                 let .decimalDigit(viewModel),
                 let .more(viewModel):
                let cell = tv.dequeueReusableCell(withIdentifier: String(describing: SettingTableViewCell.self)) as! SettingTableViewCell
                cell.bind(viewModel: viewModel)
                return cell
            }
        }
        */
        
        let datasource = RxTableViewSectionedReloadDataSource<SettingSection> { _, tv, indexpath, item in
            switch item {
            case .placeholder:
                let cell = tv.dequeueReusableCell(withIdentifier: String(describing: SettingLineTableViewCell.self)) as! SettingLineTableViewCell
                return cell
            case let .defaultCurrencyValue(viewModel),
                 let .decimalDigit(viewModel),
                 let .quoteColor(viewModel):
                let cell = tv.dequeueReusableCell(withIdentifier: String(describing: SettingTableViewCell.self)) as! SettingTableViewCell
                cell.bind(viewModel: viewModel)
                return cell
            }
        }
        
        let trigger = rx.viewWillAppear

        let output = viewModel.transform(input: SettingViewModel.Input(trigger: trigger.mapToVoid(),
                                                                       selection: tableView.rx.modelSelected(SettingSectionItem.self).asDriver()))

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
        /*
        tableView.rx.modelSelected(SettingSectionItem.self)
            .subscribe(onNext: { [weak self] item in
                guard let self
                else { return }
                switch item {
                case .defaultCurrencyValue:
                    let vc = SettingSelectViewController()
                    vc.settingType = .defaultCurrency
                    self.navigationController?.pushViewController(vc, animated: true)
                    break
                case .decimalDigit:
                    let vc = SettingDecimalViewController()
                    self.navigationController?.pushViewController(vc, animated: true)
                    break
                case .more:
                    let more = MoreSettingViewController()
                    self.navigationController?.pushViewController(more, animated: true)
                    break
                default:
                    break
                }
            })
            .disposed(by: rx.disposeBag)
         */
        tableView.rx.modelSelected(SettingSectionItem.self)
            .subscribe(onNext: { [weak self] item in
                guard let self
                else { return }
                switch item {
                case .defaultCurrencyValue:
                    let vc = SettingSelectViewController()
                    vc.settingType = .defaultCurrency
                    self.navigationController?.pushViewController(vc, animated: true)
                    break
                case .decimalDigit:
                    let vc = SettingDecimalViewController()
                    self.navigationController?.pushViewController(vc, animated: true)
                    break
                case .quoteColor:
                    let vc = QuoteColorSettingViewController()
                    self.navigationController?.pushViewController(vc, animated: true)
                    break
                default:
                    break
                }
            })
            .disposed(by: rx.disposeBag)
        
        output.selectedEvent.drive(onNext: { item in
            debugPrint(item)
        })
        .disposed(by: rx.disposeBag)
    }
}
