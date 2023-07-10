//
//  HomeCurrencyViewController.swift
//  xCurrency
//
//  Created by fd on 2022/10/18.
//

import RxCocoa
import RxDataSources
import RxSwift
import UIKit
import WidgetKit
import SocketTask

class HomeCurrencyViewController: YBaseViewController {
    var viewModel: HomeViewModel!
    var valueChangedSubscription: Disposable?
    var currentValueChanged: BehaviorRelay<String?> = BehaviorRelay(value: nil)
    var tableViewHeightSubject: BehaviorRelay<CGFloat> = BehaviorRelay(value: 0)

    var calculatorView = CalculatorView().then { item in
        item.isHidden = true // hide custom keyboard
    }
    
    var keyboardController = KeyboardController()
    var lastInputSelectCurrencyToken = ""
    var selectCell: HomeTableViewCell? {
        didSet {
            if let selectCell {
                keyboardController.bind(cell: selectCell)
            }
        }
    }

    @objc func appWillEnterForeground() {
        if viewModel != nil {
            viewModel.getSymbolsRateRequest()
        }
    }

    lazy var tableView: UITableView = UITableView(frame: .zero).then { tv in
        tv.register(cellWithClass: HomeTableViewCell.self)
        tv.showsVerticalScrollIndicator = false
        tv.tableFooterView = UIView()
        tv.separatorStyle = .none
        tv.backgroundColor = .backgroundColor
        tv.rowHeight = 74
    }

    override func makeUI() {
        view.addSubview(tableView)
        view.addSubview(calculatorView)
        
        NotificationCenter.default.addObserver(self, selector: #selector(appWillEnterForeground), name: Notification.Name(rawValue: "websocktConnect"), object: nil)
        
        _ = navigationView.rightButton.then { button in
            button.setImage(UIImage(named: "n_add"), for: .normal)
            button.setImage(UIImage(named: "n_add"), for: .highlighted)
            button.addTarget(self, action: #selector(didTappedAddButton(button:)), for: .touchUpInside)
        }
        navigationView.backMode = .none
        navigationView.pinMode = .none
        navigationView.titleMode = .center
        navigationView.titleLabel.text = "Cryptovise".localized()
    }

    override func makeEvent() {
        viewModel = HomeViewModel()
        keyboardController.delegate = self

        calculatorView.calculatorButtons.forEach { button in
            button.addTarget(keyboardController,
                    action: #selector(keyboardController.handle(touched:)),
                    for: .touchUpInside)
            switch button.key {
            case .delete:
                let gesture = UILongPressGestureRecognizer(target: keyboardController, action: #selector(keyboardController.handleLongPressDelete(gestureRecognizer:)))
                button.addGestureRecognizer(gesture)
                gesture.minimumPressDuration = 0.3
            case .multiply:
                let gesture = UILongPressGestureRecognizer(target: keyboardController, action: #selector(keyboardController.handleLongPressOperator(gestureRecognizer:)))
                button.addGestureRecognizer(gesture)
                gesture.minimumPressDuration = 0.3
            case .divide:
                let gesture = UILongPressGestureRecognizer(target: keyboardController, action: #selector(keyboardController.handleLongPressOperator(gestureRecognizer:)))
                button.addGestureRecognizer(gesture)
                gesture.minimumPressDuration = 0.3
            default:
                break
            }
        }
        bindViewModel()
    }

    override func makeLayout() {
        tableView.snp.makeConstraints { make in
            make.left.equalToSuperview().offset(20)
            make.right.equalToSuperview().offset(-20)
            make.top.equalTo(navigationView.snp.bottom).offset(8)
            make.bottom.equalTo(view.safeAreaLayoutGuide.snp.bottom)
        }
        calculatorView.pin.left().bottom().right().height(calculatorHeight())
        tableViewHeightSubject.accept(tableView.height)
    }

    func bindViewModel() {
        let output = viewModel.transform(input: HomeViewModel.Input(tableViewHeight: tableViewHeightSubject,
                textFieldValueChanged: currentValueChanged,
                itemSelected: tableView.rx.itemSelected.asObservable(),
                viewWillAppear: rx.viewWillAppear,
                viewDidDisappear: rx.viewDidDisappear))

        let datasource = RxTableViewSectionedReloadDataSource<HomeCellSection> { [weak self] _, tableView, indexPath, cellViewModel in
            guard let self else {
                return UITableViewCell(frame: .zero)
            }

            let cell = tableView.dequeueReusableCell(withIdentifier: String(describing: HomeTableViewCell.self),
                    for: indexPath) as! HomeTableViewCell

            cell.bindViewModel(cellViewModel: cellViewModel)
            cell.textfieldMaskView.rx.tap
                    .subscribe(onNext: {
                        self.tableView.delegate?.tableView?(tableView, didSelectRowAt: indexPath)
                    })
                    .disposed(by: self.rx.disposeBag)
            cell.detailMaskView.rx.tap
                .throttle(.milliseconds(300), scheduler: MainScheduler.asyncInstance)
                .subscribe(onNext: {
                    self.navigationController?.delegate = nil
                    let from = cellViewModel.model
                    let to = self.viewModel.getDefualtCompareModel()
                    if let to {
                        let detailVC = CurrencyDetailViewController(from: from, to: to)
                        self.navigationController?.pushViewController(detailVC, animated: true)
                    }
            }).disposed(by: self.rx.disposeBag)
            
            if cellViewModel.selectedSubject.value == cellViewModel.token {
                DispatchQueue.main.asyncAfter(deadline: .now() + 0.2) {
                    self.tableView.delegate?.tableView?(tableView, didSelectRowAt: indexPath)
                }
            }

            cell.leadingAction = {
                let listVC = SwitchCurrencyViewController()
                self.navigationController?.pushViewController(listVC, animated: true)

                if let items = self.viewModel.datasource.value.first?.items {
                    for item in items {
                        listVC.selectItems.insert(item.token)
                    }
                    CurrencyTokensList.shared.insertHomeList(tokens: items.map {
                        $0.token
                    })
                }

                self.view.endEditing(true)

                listVC.didSelectItemHandler.subscribe(onNext: { item in
                            self.viewModel.changeToken(item: item, index: indexPath.row)

                            if let selectIndexpath = self.viewModel.itemSelected.value,
                               let selectCell = self.selectCell {
                                if selectIndexpath.row == indexPath.row {
                                    selectCell.valueInnerTextField.insert(text: "")
                                    selectCell.valueInnerTextField.text = ""
                                    selectCell.expressionInnerTextField.text = ""
                                    self.currentValueChanged.accept("")
                                    self.keyboardController.clean()
                                }

                                _ = self.rx.viewWillAppear
                                        .take(1)
                                        .delay(.milliseconds(100), scheduler: MainScheduler.asyncInstance)
                                        .subscribe(onNext: { [weak self] _ in
                                            self?.tableView.delegate?.tableView?(tableView, didSelectRowAt: selectIndexpath)
                                        })

                                self.navigationView.refresh()
                            }
                        })
                        .disposed(by: listVC.rx.disposeBag)
            }
            cell.trailingAction = {
                self.navigationController?.delegate = nil

                let from = cellViewModel.model
                let to = self.viewModel.getDefualtCompareModel()
                if let to {
                    let detailVC = CurrencyDetailViewController(from: from, to: to)
                    self.navigationController?.pushViewController(detailVC, animated: true)
                }
            }
            return cell
        }

        tableView.rx.setDelegate(self)
                .disposed(by: rx.disposeBag)

        output.datasource.bind(to: tableView.rx.items(dataSource: datasource))
                .disposed(by: rx.disposeBag)

        tableView.rx.itemSelected
                .subscribe(onNext: { [weak self] indexPath in
                    guard let self,
                          let cellViewModels = self.viewModel.datasource.value.first?.items
                    else {
                        return
                    }

                    if self.tableView.visibleCells.isEmpty {
                        DispatchQueue.main.async {
                            self.popupKeyboard()
                        }
                        return
                    }

                    if let cell = self.tableView.visibleCells[indexPath.row] as? HomeTableViewCell {
                        self.selectCell = cell
                        self.valueChangedSubscription?.dispose()

                        let selectToken = cellViewModels[indexPath.row].token

                        if self.lastInputSelectCurrencyToken == selectToken {
                            self.viewModel.isSwitchSelectCurrency.accept(false)
                        } else {
                            self.viewModel.isSwitchSelectCurrency.accept(true)
                        }

                        self.valueChangedSubscription = cell.valueInnerTextField.rx.controlEvent(.editingChanged)
                                .subscribe(onNext: {
                                    self.detectKeyboardInput(with: selectToken)
                                    self.currentValueChanged.accept(cell.valueInnerTextField.text.or(""))
                                })
                    }

                    for cellViewModel in cellViewModels {
                        cellViewModel.selectedSubject.accept(cellViewModels[indexPath.row].token)
                    }

                    self.tableView.beginUpdates()
                    self.tableView.endUpdates()

                    self.popupKeyboard()

                })
                .disposed(by: rx.disposeBag)
        NotificationCenter.default.rx
                .notification(Notification.Name(widgetDidTappedConvertLineNotification))
                .subscribe(onNext: { [weak self] notification in
                    guard let self
                    else {
                        return
                    }
                    if let token = notification.object as? String,
                       let items = self.viewModel.datasource.value.first?.items,
                       let tappedIndex = items.firstIndex(where: { $0.token == token }) {
                        if tappedIndex == self.viewModel.itemSelected.value?.row ?? 0 {
                            return
                        }
                        self.backToHomeViewController {
                            self.tableView.delegate?.tableView?(self.tableView, didSelectRowAt: IndexPath(row: tappedIndex, section: 0))
                            DispatchQueue.main.asyncAfter(deadline: .now() + 0.3) {
                                if #available(iOS 14.0, *) {
                                    WidgetCenter.shared.reloadAllTimelines()
                                }
                            }
                        }
                    }
                })
                .disposed(by: rx.disposeBag)

        NotificationCenter.default
                .rx
                .notification(Notification.Name(widgetDidTappedQuotationNotification))
                .subscribe(onNext: { [weak self] notification in
                    guard let self
                    else {
                        return
                    }

                    let routerBlock = {
                        self.navigationController?.delegate = nil
                        if let token = notification.object as? String,
                           let from = GetCurrencyTokensResponse.fetchOneCurrency(with: token),
                           let to = self.viewModel.getDefualtCompareModel() {
                            let detailVC = CurrencyDetailViewController(from: from, to: to)
                            self.navigationController?.pushViewController(detailVC, animated: true)
                        }
                    }

                    let rootVC = self.navigationController
                    if rootVC?.visibleViewController?.presentedViewController != nil {
                        rootVC?.visibleViewController?.dismiss(animated: true, completion: {
                            rootVC?.popToRootViewController(animated: true)
                            DispatchQueue.main.asyncAfter(deadline: .now() + 0.3) {
                                routerBlock()
                            }
                        })
                    } else {
                        if rootVC?.visibleViewController != self {
                            rootVC?.popToRootViewController(animated: true)
                            DispatchQueue.main.asyncAfter(deadline: .now() + 0.3) {
                                routerBlock()
                            }
                            return
                        } else {
                            routerBlock()
                        }
                    }
                })
                .disposed(by: rx.disposeBag)
    }

    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
    }

    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)

        DispatchQueue.main.asyncAfter(deadline: .now() + 0.1) {
            self.hideSwipeCells()
        }
    }

    @objc func didTappedAddButton(button: UIButton) {
        let listVC = SwitchCurrencyViewController()
        self.navigationController?.pushViewController(listVC, animated: true)

        if let items = self.viewModel.datasource.value.first?.items {
            for item in items {
                listVC.selectItems.insert(item.token)
            }
            CurrencyTokensList.shared.insertHomeList(tokens: items.map {
                $0.token
            })
        }
        self.view.endEditing(true)
        listVC.didSelectItemHandler.subscribe(onNext: { item in
            self.viewModel.changeToken(item: item, index: 0)
            
            if let selectIndexpath = self.viewModel.itemSelected.value, let selectCell = self.selectCell {
//                if selectIndexpath.row == indexPath.row {
//                    selectCell.valueInnerTextField.insert(text: "")
//                    selectCell.valueInnerTextField.text = ""
//                    selectCell.expressionInnerTextField.text = ""
//                    self.currentValueChanged.accept("")
//                    self.keyboardController.clean()
//                }
                _ = self.rx.viewWillAppear
                        .take(1)
                        .delay(.milliseconds(100), scheduler: MainScheduler.asyncInstance)
                        .subscribe(onNext: { [weak self] _ in
                            self?.tableView.delegate?.tableView?(self!.tableView, didSelectRowAt: selectIndexpath)
                        })
            }
            
        })
        .disposed(by: listVC.rx.disposeBag)
    }

    func backToHomeViewController(completion: @escaping () -> Void) {
        guard let window = getWindow()
        else {
            return
        }
        let rootVC = window.rootViewController as! YNavigationController
        if rootVC.findTopViewController().isKind(of: HomeCurrencyViewController.self) {
            completion()
        } else {
            rootVC.topViewController?.dismiss(animated: true, completion: { [weak rootVC] in
                rootVC?.popToRootViewController(animated: true)
                DispatchQueue.main.asyncAfter(deadline: .now() + 0.3) {
                    completion()
                }
            })
        }
    }

    func hideSwipeCells() {
        for cell in tableView.visibleCells
                .compactMap({ $0 as? HomeTableViewCell })
                .filter({ $0.isDrag }) {
            cell.animateBackToOrigin(duration: 0.6, velocity: 10, damping: 0.75)
        }
    }

    func popupKeyboard() {
        keyboardController.popupKeyboard()
    }
}

extension HomeCurrencyViewController: UITableViewDelegate {
    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        let datasource = viewModel.datasource.value
        if datasource.isEmpty {
            return 0
        }
        let rows = datasource[indexPath.section].items
        let height = rows[indexPath.row].cellHeight
        let selectHeight = rows[indexPath.row].selectCellHeight
        let isSelect = rows[indexPath.row].selectedSubject.value == rows[indexPath.row].token
        return isSelect ? selectHeight : height
    }
}

extension HomeCurrencyViewController: KeyboardControllerDelegate {
    func didTappedKeyboard() {
        guard let cellViewModels = viewModel.datasource.value.first?.items,
              let selectIndex = viewModel.itemSelected.value?.row
        else {
            return
        }
        let selectToken = cellViewModels[selectIndex].token
        detectKeyboardInput(with: selectToken)
    }

    func detectKeyboardInput(with selectToken: String) {
        if lastInputSelectCurrencyToken != selectToken {
            lastInputSelectCurrencyToken = selectToken
        }

        if viewModel.isSwitchSelectCurrency.value != false {
            viewModel.isSwitchSelectCurrency.accept(false)
        }
    }
}
