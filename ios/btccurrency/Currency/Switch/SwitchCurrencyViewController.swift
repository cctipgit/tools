//
//  SwitchCurrencyViewController.swift
//  xCurrency
//
//  Created by fd on 2022/10/18.
//

import NSObject_Rx
import RxDataSources
import RxSwift
import UIKit
import SocketTask
import FittedSheets

enum SwitchVCSelectType: String, CaseIterable {
    case selectToken = "Select tokens"
    case selectCurrency = "Select currency"
    func attString() -> NSMutableAttributedString {
        let imageAttachment = NSTextAttachment()
        imageAttachment.image = UIImage(named: "n_arrow_down")
        imageAttachment.bounds = CGRect(x: 0, y: -6, width: 24, height: 24)
        let imageString = NSAttributedString(attachment: imageAttachment)
        let textString = NSMutableAttributedString(string: self.rawValue, attributes: [NSAttributedString.Key.foregroundColor: UIColor.basicBlk, NSAttributedString.Key.font: UIFont.robotoBold(with: 20) ?? .systemFont(ofSize: 20)])
        let combination = NSMutableAttributedString()
        combination.append(textString)
        combination.append(imageString)
        return combination
    }
}

class SwitchCurrencyViewController: YBaseViewController {
    var interactiveAnimator: SwitchCurrencyInteractiveAnimation?
    var viewModel: SwitchCurrencyViewModel = SwitchCurrencyViewModel()

    var didSelectItemHandler: PublishSubject<GetCurrencyTokensResponse> = PublishSubject()
    var selectItems: Set<String> = Set(minimumCapacity: 7)
    var sheet: SheetViewController?
    
    lazy var tableView: UITableView = UITableView(frame: .zero, style: .plain).then { tv in
        tv.showsVerticalScrollIndicator = false
        tv.register(cellWithClass: SwitchCurrencyTableViewCell.self)
        tv.register(headerFooterViewClassWith: SwitchCurrencySectionHeaderView.self)
        tv.backgroundColor = .backgroundColor
        tv.separatorStyle = .none
    }

    lazy var searchTableView: UITableView = UITableView(frame: .zero, style: .plain).then { tv in
        tv.showsVerticalScrollIndicator = false
        tv.register(cellWithClass: SwitchCurrencyTableViewCell.self)
        tv.backgroundColor = .backgroundColor
        tv.separatorStyle = .none
        tv.isHidden = true
    }

    let indexView = CurrencyIndexBar(frame: .zero)

    let searchbar = CurrencySearchView(frame: .zero)
    var emptyView: CurrencySearchEmptyView = CurrencySearchEmptyView(frame: .zero).then { empty in
        empty.isHidden = true
    }
    
    var isViewDidAppear = false
    var currentSelect: SwitchVCSelectType = .selectToken
    
    let switchBtn = UIButton(type: .custom).then { btn in
        btn.setAttributedTitle( SwitchVCSelectType.selectToken.attString(), for: .normal)
        btn.titleLabel?.attributedText = SwitchVCSelectType.selectToken.attString()
    }
 
    override func makeUI() {
        view.backgroundColor = .backgroundColor
        navigationView.backgroundColor = .backgroundColor
        
//        if presentingViewController != nil {
//            navigationView.backMode = .normal
//            navigationView.backButton.rx
//                .tap
//                .subscribe(onNext: { [weak self] in
//                    self?.dismiss(animated: true)
//                })
//                .disposed(by: rx.disposeBag)
//        } else {
//            navigationView.backMode = .none
//            fd_interactivePopDisabled = true
//            interactiveAnimator = SwitchCurrencyInteractiveAnimation(currentViewController: self)
//            navigationView.rightButton.setImage(UIImage(named: "forward"), for: .normal)
//        }
        navigationView.titleMode = .center
        navigationView.backMode = .normal
        navigationView.pinMode = .none
        navigationView.backButton.rx
            .tap
            .subscribe(onNext: { [weak self] in
                self?.dismiss(animated: true)
            })
            .disposed(by: rx.disposeBag)
        
        switchBtn.rx.tap.subscribe(onNext: { [weak self] _ in
            guard let self  = self else { return }
            let options = SheetOptions(
                pullBarHeight: 0,
                presentingViewCornerRadius: 16,
                shrinkPresentingViewController: true,
                useInlineMode: true
            )
            let sheetVC = CurrencySheetViewController(selectType: self.currentSelect)
            sheetVC.delegate = self
            sheetVC.backBtn.rx.tap
                .subscribe(onNext: { [weak self] in
                    guard let self = self else { return }
                    self.sheet?.animateOut()
                })
                .disposed(by: rx.disposeBag)
            self.sheet = SheetViewController(controller: sheetVC, sizes: [.fixed(233)], options: options)
            self.sheet?.animateIn(to: self.view, in: self)
            self.sheet?.didDismiss =  { (void) in
                // reload data
                print("reload data")
            }
        }).disposed(by: rx.disposeBag)
        
        navigationView.addSubview(switchBtn)
        view.addSubviews([searchbar, tableView, searchTableView, indexView])
        searchTableView.addSubview(emptyView)
        searchbar.delegate = self
    }

    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
        isViewDidAppear = true
    }

    override func makeLayout() {
        if presentingViewController != nil {
            navigationView.pinMode = .pan
        }

        searchbar.pin
            .below(of: navigationView)
            .marginTop(16)
            .horizontally(20)
            .height(36)

        tableView.pin
            .left(20)
            .bottom()
            .right(66)
            .below(of: searchbar)
            .marginTop(16)

        searchTableView.pin
            .left(20)
            .bottom()
            .right(20)
            .below(of: searchbar)
            .marginTop(16)

        indexView.pin
            .after(of: tableView)
            .marginLeft(16)
            .vCenter()
            .width(30)

        switchBtn.snp.makeConstraints { make in
            make.width.equalToSuperview().multipliedBy(0.5)
            make.top.bottom.centerX.equalToSuperview()
        }
        emptyView.pin.top(80).size(CGSizeMake(137, 133)).hCenter()
    }

    override func makeEvent() {
        navigationView.rightButton.addTarget(self, action: #selector(didTouched(back:)), for: .touchUpInside)
        CurrencyTokensList.shared.insertLocalItem()
        bindViewModel()
        indexView.tableView = tableView
    }

    func bindViewModel() {
        let datasource = RxTableViewSectionedReloadDataSource<SwitchCurrencyGroup> { [weak self] _, tableView, indexPath, item in
            let cell = tableView.dequeueReusableCell(withIdentifier: String(describing: SwitchCurrencyTableViewCell.self), for: indexPath) as! SwitchCurrencyTableViewCell
            if let self {
                item.isSelect = self.selectItems.contains(item.currency)
            }
            cell.config(with: item, isSearch: false)
            return cell
        }

        let searchDatasource = RxTableViewSectionedReloadDataSource<SwitchCurrencyGroup> { [weak self] _, tableView, indexPath, item in
            let cell = tableView.dequeueReusableCell(withIdentifier: String(describing: SwitchCurrencyTableViewCell.self), for: indexPath) as! SwitchCurrencyTableViewCell
            if let self {
                item.isSelect = self.selectItems.contains(item.currency)
            }

            cell.config(with: item, isSearch: true)
            return cell
        }

        let searchTrigger = searchbar.searchField.rx.text
            .distinctUntilChanged()
            .compactMap({ $0 })
            .asObservable()

        let input = SwitchCurrencyViewModel.Input(searchTrigger: searchTrigger,
                                                  refreshTrigger: rx.viewWillAppear.mapToVoid())
        let output = viewModel.transform(input: input)

        tableView.rx
            .setDelegate(self)
            .disposed(by: rx.disposeBag)

        output.datasource
            .bind(to: tableView.rx.items(dataSource: datasource))
            .disposed(by: rx.disposeBag)
        output.datasource
            .map {
                $0.map {
                    if $0.sectionType == .frequent {
                        return "#"
                    } else if $0.sectionType == .fixed {
                        return $0.header
                    } else {
                        return ""
                    }
                }
            }
            .subscribe(onNext: { [weak self] titles in
                self?.indexView.titlesArray = titles
            })
            .disposed(by: rx.disposeBag)

        searchTableView.rx
            .setDelegate(self)
            .disposed(by: rx.disposeBag)

        output.searchDatasource
            .bind(to: searchTableView.rx.items(dataSource: searchDatasource))
            .disposed(by: rx.disposeBag)

        output.searchDatasource
            .skip(1)
            .compactMap { $0.first?.items.isEmpty }
            .subscribe(onNext: { [weak self] hideEmpty in
                guard let self
                else { return }
                if (self.searchbar.searchField.text?.isEmpty).or(true) {
                    self.emptyView.isHidden = true
                    return
                }

                self.emptyView.isHidden = !hideEmpty

            })
            .disposed(by: rx.disposeBag)
        
        Observable.zip(tableView.rx.itemSelected,
                       tableView.rx.modelSelected(SwitchCurrencyCellViewModel.self))
            .subscribe(onNext: { [weak self] indexpath,item in
                guard let self
                else { return }
                
                if (item.isSelect) {
                    let cell = self.tableView.cellForRow(at: indexpath)
                    cell?.shakeHorizontal()
                    UIImpactFeedbackGenerator(style: .light).impactOccurred()
                    return
                }
                self.didSelectItemHandler.onNext(item.response)
                CurrencyTokensList.shared.insertFrequent(viewModel: item)
                // task
                let taskInfo = self.getTaskInfo()
                taskInfo.addTokenCount += 1
                self.setLocalTaskModel(model: taskInfo)
                
                if self.presentingViewController != nil {
                    self.dismiss(animated: true)
                } else {
                    self.navigationController?.popViewController(animated: true)
                }
            })
            .disposed(by: rx.disposeBag)

        Observable.zip(searchTableView.rx.itemSelected,
                       searchTableView.rx.modelSelected(SwitchCurrencyCellViewModel.self))
            .subscribe(onNext: { [weak self] indexpath, item in
                guard let self
                else { return }
                if (item.isSelect) {
                    let cell = self.searchTableView.cellForRow(at: indexpath)
                    cell?.shakeHorizontal()
                    UIImpactFeedbackGenerator(style: .light).impactOccurred()
                    return
                }
                self.didSelectItemHandler.onNext(item.response)
                CurrencyTokensList.shared.insertFrequent(viewModel: item)
                // task
                let taskInfo = self.getTaskInfo()
                taskInfo.addTokenCount += 1
                self.setLocalTaskModel(model: taskInfo)
                
                if self.presentingViewController != nil {
                    self.dismiss(animated: true)
                } else {
                    self.navigationController?.popViewController(animated: true)
                }
            })
            .disposed(by: rx.disposeBag)

        searchTableView.rx
            .willBeginDragging
            .subscribe(onNext: { [weak self] in
                guard let self else {
                    return
                }
                self.searchbar.resignFirstResponder()

            })
            .disposed(by: rx.disposeBag)
    }

    @objc func didTouched(back button: UIButton) {
        navigationController?.popViewController(animated: true)
    }
}

extension SwitchCurrencyViewController: UITableViewDelegate {
    func scrollViewWillBeginDragging(_ scrollView: UIScrollView) {
        view.endEditing(true)
        if self.searchbar.searchField.isEmpty {
            self.searchTableView.isHidden = true
            self.tableView.isHidden = false
            self.indexView.isHidden = self.tableView.isHidden
        }
    }

    func tableView(_ tableView: UITableView, willDisplay cell: UITableViewCell, forRowAt indexPath: IndexPath) {
        if isViewDidAppear
            || tableView == searchTableView
            || presentingViewController != nil {
            return
        }

        var count = 0
        for section in 0 ..< indexPath.section {
            count += viewModel.datasource.value[section].items.count
        }
        count += viewModel.datasource.value[indexPath.section].items[0 ..< indexPath.row].count

        cell.alpha = 0
        DispatchQueue.main.asyncAfter(deadline: .now() + Double(count) * 0.01) {
            cell.layer.transform = CATransform3DMakeTranslation(-cell.width, 0, 0)

            UIView.animate(withDuration: 0.25, delay: 0.2, usingSpringWithDamping: 0.75, initialSpringVelocity: 0) {
                cell.layer.transform = CATransform3DIdentity
                cell.alpha = 1
            }
        }
    }

    func tableView(_ tableView: UITableView, willDisplayHeaderView view: UIView, forSection section: Int) {
        if isViewDidAppear
            || tableView == searchTableView
            || presentingViewController != nil {
            return
        }

        var count = 0
        for section in 0 ..< section {
            count += viewModel.datasource.value[section].items.count
        }
        count += 1
        view.alpha = 0
        DispatchQueue.main.asyncAfter(deadline: .now() + Double(count) * 0.01) {
            view.layer.transform = CATransform3DMakeTranslation(-view.width, 0, 0)

            UIView.animate(withDuration: 0.25, delay: 0.2, usingSpringWithDamping: 0.75, initialSpringVelocity: 0) {
                view.layer.transform = CATransform3DIdentity
                view.alpha = 1
            }
        }
    }

    func tableView(_ tableView: UITableView, viewForHeaderInSection section: Int) -> UIView? {
        if searchTableView == tableView {
            return nil
        }
        let headerView = tableView.dequeueReusableHeaderFooterView(withIdentifier: String(describing: SwitchCurrencySectionHeaderView.self)) as! SwitchCurrencySectionHeaderView
        let section = viewModel.datasource.value[section]
        headerView.titleLabel.text = section.header
        headerView.backgroundView = nil
        return headerView
    }

    func tableView(_ tableView: UITableView, viewForFooterInSection section: Int) -> UIView? {
        let footer = UIView().then {
            $0.backgroundColor = .clear
            $0.isHidden = true
        }
        return footer
    }

    func tableView(_ tableView: UITableView, heightForFooterInSection section: Int) -> CGFloat {
        return section == viewModel.datasource.value.count - 1 ? 0 : 16
    }

    func tableView(_ tableView: UITableView, heightForHeaderInSection section: Int) -> CGFloat {
        return tableView == searchTableView ? 0 : 28
    }

    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return 60
    }
}

extension SwitchCurrencyViewController: CurrencySearchViewDelegate {
    func searchBarShouldBeginEditing(_ searchBar: CurrencySearchView) -> Bool {
        searchTableView.isHidden = false
        indexView.isHidden = true
        return true
    }

    func searchBarCancelButtonClicked(_ searchBar: CurrencySearchView) {
        searchTableView.isHidden = true
        indexView.isHidden = false
        view.endEditing(true)
    }
}

extension SwitchCurrencyViewController: CurrencySheetDelegate {
    func didCurrencySheetSelected(type: SwitchVCSelectType) {
        self.currentSelect = type
        switchBtn.setAttributedTitle(type.attString(), for: .normal)
        switchBtn.setAttributedTitle(type.attString(), for: .highlighted)
        self.sheet?.animateOut()
        self.viewModel.datasource.accept(CurrencyTokensList.shared.filterCustomData(type: type))
    }
}
