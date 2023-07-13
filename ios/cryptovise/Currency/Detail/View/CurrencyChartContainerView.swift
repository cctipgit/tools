//
//  CurrencyChartContainerView.swift
//  cryptovise
//
//  Created by fd on 2022/10/21.
//

import BetterSegmentedControl
import Charts
import FluentDarkModeKit
import RxSwift
import SwifterSwift
import UIKit

class CurrencyChartContainerView: UIView {
    var scrollView = UIScrollView().then { scrollView in
        scrollView.backgroundColor = .backgroundColor
    }

    var switchContainerView = UIView().then {
        $0.backgroundColor = .backgroundColor
    }

    var switchView = CurrencySpecsSwitchView().then { _ in
    }

    var priceLabel = UILabel().then { label in
        label.textColor = .primaryTextColor
        label.font = .boldPoppin(with: 38)

        label.adjustsFontSizeToFitWidth = true
        label.minimumScaleFactor = 0.5
        label.numberOfLines = 1
        label.textAlignment = .center
    }

    var loadIndicatorView = ChartLoadIndicatorView(frame: .zero)
    var floatPointView = FloatPointView()
    var marker = BalloonMarker(frame: .zero)

    var pricePromptView = ChartPricePromptView(frame: .zero).then { v in
        v.isHidden = true
    }

    lazy var transformer = Transformer(viewPortHandler: chart.viewPortHandler).then { transformer in
        transformer.prepareMatrixOffset(inverted: false)
    }

    var lineDataSet: LineChartDataSet = LineChartDataSet(entries: []).then { dataSet in
        dataSet.drawCirclesEnabled = false
        dataSet.drawCircleHoleEnabled = false
        dataSet.lineWidth = 2.28
        dataSet.drawValuesEnabled = false
        dataSet.mode = .linear
        dataSet.highlightEnabled = true
        dataSet.lineCapType = .square
        dataSet.highlightLineWidth = 1
        dataSet.highlightColor = .primaryBranding
        dataSet.axisDependency = .left
        dataSet.drawHorizontalHighlightIndicatorEnabled = false
    }

    var chart: LineChartView = LineChartView().then { chartView in
        chartView.dragEnabled = true
        chartView.pinchZoomEnabled = false
        chartView.setScaleEnabled(false)
        chartView.drawGridBackgroundEnabled = false
        chartView.legend.enabled = false
        chartView.highlightPerTapEnabled = true
        chartView.highlightPerDragEnabled = true

        chartView.xAxis.enabled = false
        chartView.leftAxis.enabled = false
        chartView.rightAxis.enabled = false

        chartView.drawBordersEnabled = false
        chartView.highlightPerTapEnabled = false

        chartView.maxVisibleCount = 200
        chartView.minOffset = 0
    }

    var segmentView: CurrencyTimeSegementView = CurrencyTimeSegementView()
    var segmentControl: BetterSegmentedControl {
        segmentView.segement
    }

    var infoView = CurrencyChartInfoView(frame: .zero)
    var isReverse = false
    var tapSwitchHandler: ((Int) -> Void)?

    var isFeedbacked = false
    var viewModel: CurrencyDetailViewModel!

    var dateFormatter: DateFormatter = DateFormatter().then {
        $0.locale = Locale.current
        $0.timeZone = .autoupdatingCurrent
        $0.dateFormat = "MMM d HH:mm:ss"
    }

    // MARK: methods

    init() {
        super.init(frame: .zero)
        makeUI()
        makeEvent()
    }

    required init?(coder: NSCoder) {
        super.init(coder: coder)
        makeUI()
        makeEvent()
    }

    func makeUI() {
        addSubview(scrollView)
        scrollView.addSubviews([switchContainerView,
                                priceLabel,
                                chart,
                                segmentView,
                                infoView])
        switchContainerView.addSubview(switchView)

        chart.addSubviews([loadIndicatorView,
                           marker,
                           floatPointView])

        chart.delegate = self
    }

    func makeEvent() {
        let tap = UITapGestureRecognizer(target: self, action: #selector(didTap(gestureRecognizer:)))
        switchView.addGestureRecognizer(tap)

        chart.data = LineChartData(dataSet: lineDataSet)
    }

    @objc func didTap(gestureRecognizer: UITapGestureRecognizer) {
        let point = gestureRecognizer.location(in: switchView)
        if isReverse {
            if point.x > switchView.center.x {
                return
            }
            let rect = switchView.currencyItem.selectIconView.frame.inset(by: UIEdgeInsets(inset: -20))
            let convertRect = switchView.currencyItem.convert(rect, to: switchView)
            if CGRectContainsPoint(convertRect, point) {
                tapSwitchHandler?(0)
            }
        } else {
            if point.x < switchView.center.x {
                return
            }
            let rect = switchView.switchItem.selectIconView.frame.inset(by: UIEdgeInsets(inset: -20))
            let convertRect = switchView.switchItem.convert(rect, to: switchView)
            if CGRectContainsPoint(convertRect, point) {
                tapSwitchHandler?(1)
            }
        }
    }

    func bind(viewModel: CurrencyDetailViewModel) {
        self.viewModel = viewModel
    }

    func bind(output: CurrencyDetailViewModel.Output) {
        output.max
            .distinctUntilChanged()
            .throttle(.milliseconds(1000), scheduler: MainScheduler.asyncInstance)
            .bind(to: infoView.maxItemView.valueLabel.rx.text)
            .disposed(by: rx.disposeBag)
        output.min
            .distinctUntilChanged()
            .throttle(.milliseconds(1000), scheduler: MainScheduler.asyncInstance)
            .bind(to: infoView.minItemView.valueLabel.rx.text)
            .disposed(by: rx.disposeBag)
        output.avg
            .distinctUntilChanged()
            .throttle(.milliseconds(1000), scheduler: MainScheduler.asyncInstance)
            .bind(to: infoView.avgItemView.valueLabel.rx.text)
            .disposed(by: rx.disposeBag)
        output.price
            .distinctUntilChanged()
            .throttle(.milliseconds(1000), scheduler: MainScheduler.asyncInstance)
            .bind(to: priceLabel.rx.text)
            .disposed(by: rx.disposeBag)

        output.quote
            .distinctUntilChanged()
            .throttle(.milliseconds(1000), scheduler: MainScheduler.asyncInstance)
            .subscribe(onNext: { [weak self] text in
                self?.infoView.quoteChangeItemView.valueLabel.text = text
                self?.changeChartColor(isRaise: !text.hasPrefix("-"))
            })
            .disposed(by: rx.disposeBag)

        let pair: (Int, [ChartDataEntry]) = (0, [])
        output.totolChartPoints
            .throttle(.milliseconds(1000), scheduler: MainScheduler.asyncInstance)
            .scan(pair, accumulator: { pair, entrys in
                (pair.0 + 1, entrys)
            })
            .subscribe(onNext: { [weak self] pair in
                guard let self
                else { return }
                
                let (count, total) = pair
                if total.isEmpty {
                    return
                }
                
                let lineSet = self.lineDataSet

                if !total.isEmpty {
                    let lastIsEmpty = lineSet.isEmpty
                    lineSet.replaceEntries(total)

                    self.chart.data?.notifyDataChanged()
                    self.chart.notifyDataSetChanged()

                    let isDayUnit = self.viewModel?.response.value?.dateUnit == SegementTapedKind.day.toString()
                    let isNotAnimate = count == 2
                        && !lastIsEmpty
                        && isDayUnit
                    if !isNotAnimate {
                        self.chart.animate(xAxisDuration: 1.0)
                        DispatchQueue.main.asyncAfter(deadline: .now() + 1) {
                            let change = self.viewModel.quote.value
                            self.showPricePromptView(isRaise: !change.hasPrefix("-"), change: change)
                        }
                    } else {
                        let change = self.viewModel.quote.value
                        self.showPricePromptView(isRaise: !change.hasPrefix("-"), change: change)
                    }
                }
            })
            .disposed(by: rx.disposeBag)

        output.partialChartPoints.skip(1)
            .throttle(.milliseconds(1000), scheduler: MainScheduler.asyncInstance)
            .subscribe(onNext: { [weak self] partial in
                guard let self
                else { return }

                if !partial.isEmpty {
                    var removeCount = 0
                    for entry in partial {
                        if let index = self.lineDataSet.lastIndex(where: { $0.x == entry.x }) {
                            if self.lineDataSet.entries[index].y == entry.y {
                                continue
                            }
                            self.lineDataSet.replaceSubrange(index ... index, with: [entry])
                        } else {
                            _ = self.lineDataSet.addEntryOrdered(entry)
                            removeCount += 1
                        }
                    }
                    if removeCount > 0 {
                        self.lineDataSet.removeFirst(removeCount)
                    }

                    self.chart.data?.notifyDataChanged()
                    self.chart.notifyDataSetChanged()
                    
                    let change = self.viewModel.quote.value
                    self.showPricePromptView(isRaise: !change.hasPrefix("-"), change: change)
                }
            })
            .disposed(by: rx.disposeBag)
       
        Observable.zip(output.tokenFrom,
                       output.tokenTo)
            .subscribe(onNext: { [weak self] from, to in
                guard let self else { return }

                if from.token.isEmpty {
                    return
                }
                self.switchView.currencyItem.amountLabel.text = from.amount
                self.switchView.currencyItem.currencyImageView.loadImage(with: from.flag)
                self.switchView.currencyItem.tokenLabel.text = from.token

                self.switchView.switchItem.currencyImageView.loadImage(with: to.flag)
                self.switchView.switchItem.tokenLabel.text = to.token

                self.layoutSwitchView()
            })
            .disposed(by: rx.disposeBag)
    }

    func removePricePromptView() {
        pricePromptView.removeFromSuperview()
    }

    private func showPricePromptView(isRaise: Bool, change: String) {
        if pricePromptView.isHidden
            || chart.width == 0
            || !isCurrentDay() {
            return
        }

        if pricePromptView.superview == nil {
            chart.addSubview(pricePromptView)
        }

        pricePromptView.textLabel.text = change

        let color = isRaise ? UIColor.upColor : .downColor
        pricePromptView.textLabel.textColor = color
        pricePromptView.setNeedsLayout()
        if let entry = lineDataSet.last {
            updateTransformer()

            var point = CGPoint(x: entry.x, y: entry.y)
            transformer.pointValueToPixel(&point)
            pricePromptView.pin
                .width(100%)
                .height(26)

            UIView.animate(withDuration: 0.2) {
                self.pricePromptView.center = CGPointMake(self.chart.width / 2, point.y)
            }
        }
    }

    func updateTransformer() {
        transformer.prepareMatrixValuePx(chartXMin: chart.chartXMin,
                                         deltaX: CGFloat(chart.chartXMax - chart.chartXMin),
                                         deltaY: CGFloat(chart.chartYMax - chart.chartYMin),
                                         chartYMin: chart.chartYMin)
    }

    func changeChartColor(isRaise: Bool) {
        let lineSet = lineDataSet
        let color = isRaise ? UIColor.primarySuccess : .downColor
        let gradientColors = [color.cgColor,
                              color.alpha(0).cgColor]

        let colorLocations: [CGFloat] = [1.0, 0.0]
        let gradient = CGGradient(colorsSpace: nil,
                                  colors: gradientColors as CFArray,
                                  locations: colorLocations)!

        lineSet.fill = LinearGradientFill(gradient: gradient, angle: 90)
        lineSet.drawFilledEnabled = true

        lineSet.setColor(color)
    }

    func changeSwitchSpec() {
        isReverse = !isReverse
        switchView.changeCurrentSpecOrder(isReverse)

        layoutSwitchView()
    }

    func layoutSwitchView() {
        switchView.performLayout()

        switchContainerView.setNeedsLayout()
        switchContainerView.layoutIfNeeded()

        let size = switchView.systemLayoutSizeFitting(UIView.layoutFittingCompressedSize)
        switchView.pin
            .top()
            .hCenter()
            .size(size)

        switchContainerView.pin
            .left()
            .right()
            .top()
            .height(size.height)
    }

    override func layoutSubviews() {
        super.layoutSubviews()

        scrollView.pin.all()

        layoutSwitchView()

        priceLabel.pin.below(of: switchView)
            .marginTop(22)
            .hCenter()
            .width(width - 32)
            .height(priceLabel.font.lineHeight)

        chart.pin.below(of: priceLabel)
            .marginTop(8)
            .horizontally(16)
            .height(220)

        loadIndicatorView.pin
            .all()

        segmentView.pin.horizontally().below(of: chart).height(108)

        infoView.pin.horizontally().below(of: segmentView)
            .width(100%)
            .sizeToFit(.width)
        scrollView.contentSize = CGSizeMake(width, infoView.frame.maxY)
    }
}

extension CurrencyChartContainerView: ChartViewDelegate {
    func chartValueSelected(_ chartView: ChartViewBase, entry: ChartDataEntry, highlight: Highlight) {
        if !isFeedbacked {
            isFeedbacked = true
            UIImpactFeedbackGenerator(style: .light).impactOccurred()
            updateTransformer()
        }

        var point = CGPoint(x: highlight.x, y: highlight.y)
        transformer.pointValueToPixel(&point)

        showMarker(entry: entry, point: point)

        floatPointView.isHidden = false
        floatPointView.center = point
        floatPointView.bounds = CGRectMake(0, 0, 8, 8)
        floatPointView.layer.masksToBounds = false

        floatPointView.startAnimate()
    }

    func chartValueNothingSelected(_ chartView: ChartViewBase) {
        endSelect()
    }

    func chartViewDidEndPanning(_ chartView: ChartViewBase) {
        endSelect()
    }

    func endSelect() {
        isFeedbacked = false
        UIImpactFeedbackGenerator(style: .light).impactOccurred()

        chart.highlightValue(nil)
        marker.isHidden = true
        priceLabel.isHidden = false
        floatPointView.isHidden = true

        if isCurrentDay() {
            pricePromptView.isHidden = false
        }
    }

    func showMarker(entry: ChartDataEntry, point: CGPoint) {
        let text = CurrencyFormatter.format(with: entry.y.string, type: viewModel.fromItem.currencyType)
        let timeInterval = entry.x
        if segmentControl.index < 2 {
            dateFormatter.dateFormat = "MMMd HH:mm"
        } else {
            dateFormatter.dateStyle = .medium
        }

        let dateString = dateFormatter.string(from: Date(timeIntervalSince1970: timeInterval / 1000))

        marker.textLabel.text = text
        marker.dateLabel.text = dateString

        marker.sizeToFit()
        marker.setNeedsLayout()
        marker.isHidden = false
        var x = point.x
        if x - marker.width / 2 < 0 {
            x = marker.width / 2
        } else if x + marker.width / 2 > chart.width {
            x = chart.width - marker.width / 2
        }
        marker.center = CGPoint(x: x, y: -10)
        priceLabel.isHidden = true

        if isCurrentDay() {
            pricePromptView.isHidden = true
        }
    }
}

extension CurrencyChartContainerView {
    func isCurrentDay() -> Bool {
        viewModel.currentDateRange == SegementTapedKind.day.toString()
    }
}
