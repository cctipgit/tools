//
//  HomeTableViewCell.swift
//  xCurrency
//
//  Created by fd on 2022/10/18.
//

import UIKit

import RxCocoa
import RxSwift

class HomeTableViewCell: SwipeTableViewCell {
    var disposeBag = DisposeBag()

    var currencyLabel = {
        UILabel().then { label in
            label.textColor = .contentSecondary
            label.font = .robotoLight(with: 14)
            label.textAlignment = .left
        }
    }()

    var valueTextField: ScrollableTextField = {
        ScrollableTextField(frame: .zero).then { field in
            let toolbar: UIToolbar = UIToolbar()
            toolbar.items = [
                UIBarButtonItem(barButtonSystemItem: UIBarButtonItem.SystemItem.flexibleSpace, target: field.textField, action: nil),
                UIBarButtonItem(title: "Done".localized(), style: .done, target: field.textField, action: #selector(UITextField.resignFirstResponder))
            ]
            toolbar.sizeToFit()
            field.textField?.font = .robotoBold(with: 20)
            field.textField?.textColor = .basicBlk
            field.textField?.keyboardType = .decimalPad
            field.textField?.inputAccessoryView = toolbar
            field.textField?.textAlignment = .right
            field.textField.contentHorizontalAlignment = .right
            field.textField.attributedPlaceholder = NSAttributedString(string: " ", attributes: [NSAttributedString.Key.foregroundColor: UIColor.basicBlk, NSAttributedString.Key.font: UIFont.robotoBold(with: 20) ?? .systemFont(ofSize: 20)])
        }
    }()

    var expressionTextField: ScrollableTextField = {
        ScrollableTextField(frame: .zero).then { field in
            field.textField?.font = .robotoBold(with: 20)
            field.textField?.textColor = .basicBlk
            field.textField?.inputView = TextFieldInputView(frame: CGRectMake(0, 0, 320, 1))
            field.textField?.inputAccessoryView = UIView()
            field.textField?.textAlignment = .right
            field.textField.contentHorizontalAlignment = .right
            field.backgroundColor = .clear
            field.textField.backgroundColor = .clear
            field.isHidden = true
        }
    }()

    var selectedView = UIView().then { view in
        view.backgroundColor = .selectedBackgroundColor
        view.layer.cornerRadius = 16
    }

    var flagIconView = UIImageView().then { imv in
        imv.layer.cornerRadius = 16
        imv.layer.masksToBounds = true
    }

    var titleLabel = UILabel().then { label in
        label.adjustsFontForContentSizeCategory = true
        label.font = .robotoBold(with: 16)
        label.textColor = .basicBlk
    }

    var locationIconView = UIImageView().then { imageView in
        imageView.image = UIImage(named: "location")
    }

    var textfieldMaskView = UIButton()
    var detailMaskView = UIButton()
    
    var raiseOrDownTipView = GradientView().then {
        $0.horizontalMode = true
        $0.backgroundColor = .clear
        $0.startLocation = 0
        $0.endLocation = 1.0
        $0.layer.cornerRadius = 16
        $0.layer.maskedCorners = [.layerMaxXMinYCorner, .layerMaxXMaxYCorner]
    }

    var flagIconMaskView = GradientView().then {
        $0.startColor = .backgroundColor
        $0.endColor = .backgroundColor.alpha(0)
        $0.horizontalMode = true
        $0.isHidden = true
        $0.backgroundColor = .clear
    }

    var valueInnerTextField: UITextField {
        return valueTextField.textField!
    }

    var expressionInnerTextField: UITextField {
        return expressionTextField.textField!
    }

    var arrowImgView = UIImageView().then { imgView in
        imgView.image = UIImage(named: "n_arrow_right")
    }
    
    var sepLine = UIView().then { v in
        v.backgroundColor = UIColor.init(hex: 0xF4F4F5)
    }
    weak var cellViewModel: HomeCellViewModel?

    // MARK: init UI

    override func makeUI() {
        containerView.addSubviews([
            sepLine,
            selectedView,
            raiseOrDownTipView,
            valueTextField,
            flagIconView,
            titleLabel,
            locationIconView,
            expressionTextField,
            currencyLabel,
            arrowImgView,
            detailMaskView,
            textfieldMaskView])
        containerView.backgroundColor = .backgroundColor
        selectionStyle = .none
        raiseOrDownTipView.alpha = 0
        valueTextField.addSubview(flagIconMaskView)
    }

    override func prepareForReuse() {
        super.prepareForReuse()
        disposeBag = DisposeBag()
    }

    func isMe(cell: HomeTableViewCell!) -> Bool {
        if cell != nil && cell.cellViewModel?.token == cellViewModel?.token {
            return true
        }
        return false
    }

    func bindViewModel(cellViewModel: HomeCellViewModel) {
        disposeBag = DisposeBag()
        self.cellViewModel = cellViewModel

        let valueWidthEvent = Observable.merge(valueInnerTextField.rx.text.distinctUntilChanged().asObservable(),
                                               valueInnerTextField.rx.observe(\.placeholder).distinctUntilChanged().asObservable())
            .compactMap { [weak self] _ in
                self?.valueInnerTextField.intrinsicContentSize.width
            }
            .distinctUntilChanged()

        let expressionWidthEvent = expressionInnerTextField.rx.text
            .compactMap { [weak self] _ in
                self?.expressionInnerTextField.intrinsicContentSize.width
            }
            .distinctUntilChanged()

        Observable.combineLatest(valueWidthEvent, expressionWidthEvent)
            .map { self.mapTitleLabelIsHidden(tuple: $0) }
            .subscribe { [weak self] tuple in
                //self?.titleLabel.isHidden = tuple.0
                self?.locationIconView.isHidden = tuple.1
                self?.flagIconMaskView.isHidden = !tuple.0
            }
            .disposed(by: disposeBag)

        cellViewModel.currencyName
            .do(afterNext: { [weak self] _ in
                self?.currencyLabel.sizeToFit()
                self?.setNeedsLayout()
            })
            .bind(to: currencyLabel.rx.text)
            .disposed(by: disposeBag)

        cellViewModel.title
            .do(afterNext: { [weak self] _ in
                self?.titleLabel.sizeToFit()
            })
            .bind(to: titleLabel.rx.text)
            .disposed(by: disposeBag)

        cellViewModel.currencyFlag
            .distinctUntilChanged()
            .subscribe(onNext: { [weak self] icon in
                self?.flagIconView.loadImage(with: icon)
            })
            .disposed(by: disposeBag)

        cellViewModel.calculateResultValue
            .observe(on: MainScheduler.instance)
            .distinctUntilChanged()
            .subscribe(onNext: { [weak self] text in
                guard let self,
                      let text,
                      let cellViewModel = self.cellViewModel
                else { return }
                debugPrint("cell " + cellViewModel.token + " " + text)

                if self.valueInnerTextField.isFirstResponder
                    || self.expressionTextField.isFirstResponder
                    || !self.expressionInnerTextField.isEmpty {
                    self.valueInnerTextField.text = text
                    return
                }

                if self.valueInnerTextField.text == text {
                    return
                }

                if self.valueInnerTextField.text.or("").isEmpty {
                    self.valueInnerTextField.insert(text: "")
                }

                if let old = self.valueInnerTextField.text,
                   !self.valueInnerTextField.isEmpty,
                   !text.isEmpty,
                   cellViewModel.calculatorRaiseOrDown.value != .none {
                    if text > old {
                        self.showColorTipView(isRaise: true)
                    } else if text < old {
                        self.showColorTipView(isRaise: false)
                    }
                }
                self.valueInnerTextField.text?.removeAll()
                self.valueInnerTextField.insert(text: text)
                self.valueInnerTextField.sendActions(for: .allEvents)
            })
            .disposed(by: disposeBag)

        cellViewModel.selectedSubject
            .subscribe(onNext: { [weak self] selectToken in
                guard let self, let cellViewModel = self.cellViewModel else {
                    return
                }
                let isSelect = selectToken == cellViewModel.token
                if isSelect {
                    self.flagIconMaskView.startColor = .selectedBackgroundColor
                    self.flagIconMaskView.endColor = .selectedBackgroundColor.alpha(0)
                } else {
                    self.flagIconMaskView.startColor = .backgroundColor
                    self.flagIconMaskView.endColor = .backgroundColor.alpha(0)
                }

                self.selectedView.isHidden = !isSelect
                self.raiseOrDownTipView.isHidden = isSelect
            })
            .disposed(by: disposeBag)

        cellViewModel.location
            .not()
            .bind(to: locationIconView.rx.isHidden)
            .disposed(by: disposeBag)

        cellViewModel.placeHolderValue
            .distinctUntilChanged()
            .unwrap()
            .subscribe(onNext: { [weak self] placeholder in
                guard let self, let cellViewModel = self.cellViewModel else {
                    return
                }

                if self.valueInnerTextField.placeholder.or("").isEmpty {
                    self.valueInnerTextField.placeholder = placeholder
                    return
                }

                if cellViewModel.placeholderRaiseOrDown.value == .show && self.valueInnerTextField.isEmpty {
                    if placeholder > self.valueInnerTextField.placeholder.or("") {
                        self.showColorTipView(isRaise: true)
                    } else if placeholder < self.valueInnerTextField.placeholder.or("") {
                        self.showColorTipView(isRaise: false)
                    }
                }
                self.valueInnerTextField.placeholder = placeholder
            })
            .disposed(by: disposeBag)
    }

    func showColorTipView(isRaise: Bool) {
        if isRaise {
            raiseOrDownTipView.startColor = UIColor(red: 0.32, green: 0.95, blue: 0.27, alpha: 0)
            raiseOrDownTipView.endColor = UIColor(red: 0.32, green: 0.95, blue: 0.27, alpha: 0.3)
        } else {
            raiseOrDownTipView.startColor = UIColor(red: 0.95, green: 0.31, blue: 0.27, alpha: 0)
            raiseOrDownTipView.endColor = UIColor(red: 0.95, green: 0.31, blue: 0.27, alpha: 0.3)
        }
        raiseOrDownTipView.alpha = 1
        DispatchQueue.main.asyncAfter(deadline: .now() + 0.3) {
            UIView.animate(withDuration: 0.5) {
                self.raiseOrDownTipView.alpha = 0
            }
        }
    }

    func mapTitleLabelIsHidden(tuple: (CGFloat, CGFloat)) -> (Bool, Bool) {
        guard let cellViewModel = cellViewModel else {
            return (false, false)
        }

        let (valueWidth, expressionWidth) = tuple

        var space = width - flagIconView.frame.maxX - (width - valueTextField.frame.maxX) - 8 - titleLabel.width - 14

        let titleLabelIsHidden = (space <= valueWidth) || (space <= expressionWidth)

        var locationViewIsHidden = true
        if cellViewModel.location.value {
            locationViewIsHidden = false
            space -= (locationIconView.width + 8)
            locationViewIsHidden = (space <= valueWidth) || (space <= expressionWidth)
        }

        return (titleLabelIsHidden, locationViewIsHidden)
    }

    override func sizeThatFits(_ size: CGSize) -> CGSize {
        guard let cellViewModel
        else { return size }
        var height = cellViewModel.cellHeight
        if cellViewModel.selectedSubject.value == cellViewModel.token {
            height = cellViewModel.selectCellHeight
        }
        return CGSizeMake(width, height)
    }

    override func layoutSubviews() {
        super.layoutSubviews()
        sepLine.snp.makeConstraints { make in
            make.height.equalTo(1)
            make.left.right.equalToSuperview()
            make.bottom.equalToSuperview().offset(-4)
        }
        let sepHeight: CGFloat = 8
        raiseOrDownTipView.snp.makeConstraints { make in
            make.left.right.top.equalToSuperview()
            make.bottom.equalToSuperview().offset(-sepHeight)
        }
        flagIconView.snp.makeConstraints { make in
            make.size.equalTo(32)
            make.top.left.equalToSuperview().offset(16)
        }
        titleLabel.snp.makeConstraints { make in
            make.height.equalTo(20)
            make.left.equalTo(flagIconView.snp.right).offset(8)
            make.top.equalTo(flagIconView).offset(-1)
            make.right.equalTo(arrowImgView.snp.left).offset(-4)
        }
        arrowImgView.snp.makeConstraints { make in
            make.size.equalTo(20)
            make.centerY.equalTo(flagIconView)
            make.left.equalToSuperview().offset(115)
        }
        locationIconView.snp.makeConstraints { make in
            make.size.equalTo(18)
            make.left.equalTo(titleLabel.snp.right).offset(8)
            make.centerY.equalTo(titleLabel)
        }
        selectedView.snp.makeConstraints { make in
            make.edges.equalTo(raiseOrDownTipView)
        }
        valueTextField.snp.makeConstraints { make in
            make.height.equalTo(24)
            make.left.equalTo(arrowImgView.snp.right).offset(16)
            make.right.equalToSuperview().offset(-16)
            make.centerY.equalTo(flagIconView)
        }
        flagIconMaskView.snp.makeConstraints { make in
            make.edges.equalTo(flagIconView)
        }
        currencyLabel.snp.makeConstraints { make in
            make.height.equalTo(16)
            make.left.equalTo(titleLabel)
            make.top.equalTo(titleLabel.snp.bottom).offset(1)
            make.width.greaterThanOrEqualTo(40)
        }
        detailMaskView.snp.makeConstraints { make in
            make.left.top.bottom.equalToSuperview()
            make.right.equalTo(valueTextField.snp.left)
        }
        textfieldMaskView.snp.makeConstraints { make in
            make.right.top.bottom.equalToSuperview()
            make.left.equalTo(valueTextField)
        }
    }

    func updateValueTextfield(text: String) {
        valueTextField.textField.bounceText = text
    }
}
