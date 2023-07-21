//
//  HomeCellViewModel.swift
//  xCurrency
//
//  Created by fd on 2022/10/18.
//

import RxCocoa
import RxDataSources
import RxSwift
import UIKit
import SocketTask

class HomeCellSection: SectionModelType {
    typealias Item = HomeCellViewModel

    var items: [HomeCellViewModel]
    var header: String
    init(items: [HomeCellViewModel], header: String) {
        self.items = items
        self.header = header
    }

    required init(original: HomeCellSection, items: [HomeCellViewModel]) {
        self.items = items
        header = original.header
    }
}

enum HomeCellRaiseOrDownMode {
    case none
    case first
    case show
}

class HomeCellViewModel {
    var title: BehaviorRelay<String>

    var currencyName: BehaviorRelay<String>
    var placeHolderValue: BehaviorRelay<String?>

    var currencyFlag: BehaviorRelay<String>
    var location: BehaviorRelay<Bool>

    var isSwitchSelectSubject:BehaviorRelay<Bool>
    var selectedSubject: BehaviorRelay<String?>
    var expressionDriver: BehaviorRelay<String>
    var placeholderRaiseOrDown: BehaviorRelay<HomeCellRaiseOrDownMode>
    var calculatorRaiseOrDown: BehaviorRelay<HomeCellRaiseOrDownMode>

    var selectedInputValue: BehaviorRelay<String>
    var currencyRatePrice: BehaviorRelay<Rate?>
    var calculateResultValue: BehaviorRelay<String?>

    var cellHeight: CGFloat
    var selectCellHeight: CGFloat

    var model: GetCurrencyTokensResponse
    var token: String
    var currencyType: String

    var disposeBag = DisposeBag()
    deinit {
        debugPrint("cellViewMode deinit \(self)")
    }

    init(with model: GetCurrencyTokensResponse) {
        self.model = model

        token = model.token
        currencyType = model.currencyType

        title = BehaviorRelay(value: model.token)
        currencyFlag = BehaviorRelay(value: model.icon)
        currencyName = BehaviorRelay(value: "")

        cellHeight = 0
        selectCellHeight = 0

        location = BehaviorRelay(value: false)

        selectedSubject = BehaviorRelay(value: "")
        selectedInputValue = BehaviorRelay(value: "")
        isSwitchSelectSubject = BehaviorRelay(value: false)
        
        calculateResultValue = BehaviorRelay(value: nil)
        expressionDriver = BehaviorRelay(value: "")

        currencyRatePrice = BehaviorRelay(value: nil)

        placeHolderValue = BehaviorRelay(value: nil)
        placeholderRaiseOrDown = BehaviorRelay(value: .none)
        calculatorRaiseOrDown = BehaviorRelay(value: .none)

        let currencyRate = currencyRatePrice.unwrap()
            .distinctUntilChanged({
                $0.price == $1.price
                    && $0.token == $1.token
                    && $0.currentSelectPrice == $1.currentSelectPrice
            })
            .share()

        Observable.merge(selectedInputValue.distinctUntilChanged().map({ _ in 0 }),
            currencyRate.map({ _ in 1 })
            .ignoreWhen({ [weak self] _ in
                self?.selectedInputValue.value.isEmpty ?? true
            }))
        .ignoreWhen({ [weak self] _ in
            return self?.isSwitchSelectSubject.value ?? false
        })
            .subscribe(onNext: { [weak self] res in
                guard let self
                else { return }

                if res == 0 {
                    self.calculatorRaiseOrDown.accept(.none)
                } else {
                    if self.calculatorRaiseOrDown.value == .none {
                        self.calculatorRaiseOrDown.accept(.first)
                    } else if self.calculatorRaiseOrDown.value == .first {
                        self.calculatorRaiseOrDown.accept(.show)
                    }
                }
                

                let inputText = self.selectedInputValue.value

                debugPrint(self.token + " " + inputText)

                if inputText.isEmpty {
                    self.calculateResultValue.accept(inputText)
                    return
                }

                if let rate = self.currencyRatePrice.value {
                    if self.selectedSubject.value == self.token {
                        var calcuText = inputText
                        if inputText.contains(",") {
                            calcuText.removeAll(where: {$0 == ","})
                        }
                        
                        let value = CurrencyFormatter.shared.format(selectCurrency: calcuText,type: self.currencyType)
                        self.calculateResultValue.accept(value)
                        return
                    } else {
                        let transformText = self.combineMultiplyExpression(origin: inputText, rate: rate)
                        let result = calculatorExpression(text: transformText)
                        switch result {
                        case let resultValue as DoubleValue:
                            let string = String(resultValue.value)
                            let calculatorString = CurrencyFormatter.format(with: string, type: self.currencyType)
                            self.calculateResultValue.accept(calculatorString)
                        default:
                            self.calculateResultValue.accept(inputText)
                        }
                    }
                }
            })
            .disposed(by: disposeBag)

        AppSetting.shared.currencySymbol
            .distinctUntilChanged()
            .map { [weak self] showUnit in
                self?.makeCurrencyTitle(isShowUnit: showUnit) ?? ""
            }
            .bind(to: currencyName)
            .disposed(by: disposeBag)

        Observable.combineLatest(
            AppSetting.shared.defaultCurrencyValue
                .distinctUntilChanged()
                .delay(.milliseconds(500), scheduler: MainScheduler.asyncInstance),
            
            AppSetting.shared.btcDecimalDigit
                .distinctUntilChanged()
                .delay(.milliseconds(500), scheduler: MainScheduler.asyncInstance),
            
            AppSetting.shared.legalDecimalDigit
                .distinctUntilChanged()
                .delay(.milliseconds(500), scheduler: MainScheduler.asyncInstance),
            
            selectedSubject,
            currencyRate)
            .map({ [weak self] defaultCurreny, _, _, _, currencyRate in
                guard let self
                else { return defaultCurreny }

                if self.selectedSubject.value == self.token {
                    return CurrencyFormatter.shared.format(selectCurrency: defaultCurreny, type: self.currencyType)
                } else {
                    if self.calculateResultValue.value.or("").isEmpty {
                        if self.placeholderRaiseOrDown.value == .none {
                            self.placeholderRaiseOrDown.accept(.first)
                        } else if self.placeholderRaiseOrDown.value == .first {
                            self.placeholderRaiseOrDown.accept(.show)
                        }
                    }

                    let text = self.combineMultiplyExpression(origin: defaultCurreny, rate: currencyRate)
                    let result = calculatorExpression(text: text)
                    switch result {
                    case let resultValue as DoubleValue:
                        let string = String(resultValue.value)
                        return CurrencyFormatter.format(with: string, type: self.currencyType)
                    default:
                        return defaultCurreny
                    }
                }
            })
            .bind(to: placeHolderValue)
            .disposed(by: disposeBag)
    }

    func combineMultiplyExpression(origin: String, rate: Rate) -> String {
        var transformText = origin
        let multiply = CalculatorKey.multiply.toString()
        transformText.insert("(", at: transformText.startIndex)
        transformText.append(")")

        let divid = CalculatorKey.divide.toString()
        transformText.append(multiply)
        if let main = rate.currentSelectPrice {
            transformText.append(main)
            transformText.append(divid)
            transformText.append(rate.price)
        }

        return transformText
    }

    func replaceItem(model: GetCurrencyTokensResponse) {
        self.model = model

        token = model.token
        currencyType = model.currencyType

        title.accept(model.token)
        currencyFlag.accept(model.icon)

        let value = makeCurrencyTitle(isShowUnit: AppSetting.shared.currencySymbol.value)
        currencyName.accept(value)

        calculatorRaiseOrDown.accept(.none)
        placeholderRaiseOrDown.accept(.none)
    }

    func makeCurrencyTitle(isShowUnit: Bool) -> String {
        if isShowUnit {
            return model.name + " " + model.unitName
        } else {
            return model.name
        }
    }
}
