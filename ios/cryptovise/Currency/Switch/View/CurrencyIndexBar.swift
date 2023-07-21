//
//  CurrencyIndexBar.swift
//  cryptovise
//
//  Created by fd on 2022/11/17.
//

import RxCocoa
import RxSwift
import UIKit

class CurrencyIndexBar: UIView {
    @objc weak var tableView: UITableView? {
        didSet {
            addObserverContentOffset()
        }
    }

    var disposable: Disposable?
    var titlesArray: [String] = [] {
        didSet {
            if titlesArray.isEmpty {
                return
            }
            reloadData()
        }
    }

    var titleLabelsArray: [UILabel] = []
    var currentIndex: Int = 0
    var isTouchMoved = false
    let itemHeight: CGFloat = 15.0

    var currentLabel: UILabel = UILabel().then {
        $0.textColor = .primaryTextColor
        $0.textAlignment = .center
        $0.numberOfLines = 1
        $0.font = .boldSystemFont(ofSize: 20)
    }

    var currentBackgroundView: UIView = UIView()

    deinit {
        disposable?.dispose()
    }

    override init(frame: CGRect) {
        super.init(frame: frame)
        makeUI()
    }

    required init?(coder: NSCoder) {
        super.init(coder: coder)
    }

    func makeUI() {
        currentBackgroundView.addSubview(currentLabel)
        currentBackgroundView.backgroundColor = .selectedBackgroundColor

        backgroundColor = .backgroundColor
    }

    func reloadData() {
        for (index, text) in titlesArray.enumerated() {
            var label: UILabel?
            if titleLabelsArray.count < index {
                label = titleLabelsArray[index]
            } else {
                label = UILabel().then {
                    $0.textColor = .secondaryTextColor
                    $0.textAlignment = .center
                    $0.numberOfLines = 1
                    $0.font = .mediumPoppin(with: 10)
                }
                titleLabelsArray.append(label!)
                addSubview(label!)
            }
            
            label?.text = text
            if index == 0 {
                label?.textColor = .darkBlueColor
                label?.font = .mediumPoppin(with: 12)
            }
        }
        setNeedsLayout()
    }

    func addObserverContentOffset() {
        guard let tableView
        else { return }

        disposable?.dispose()

        disposable = tableView.rx.contentOffset
            .distinctUntilChanged()
            .subscribe(onNext: { [weak self] _ in
                guard let self
                else { return }

                if tableView.isDragging && !tableView.isDecelerating {
                    return
                }

                if self.isTouchMoved {
                    return
                }
                self.tableViewDidScroll()
            })
    }

    func tableViewDidScroll() {
        if let rows = tableView?.indexPathsForVisibleRows,
           !rows.isEmpty {
            let index = rows[0].section
            didSelectRow(index: index, isTouch: false)
        }
    }

    // MARK: - touch
    
    override func gestureRecognizerShouldBegin(_ gestureRecognizer: UIGestureRecognizer) -> Bool {
        return false
    }

    override func touchesBegan(_ touches: Set<UITouch>, with event: UIEvent?) {
        super.touchesBegan(touches, with: event)
        isTouchMoved = true
        handle(touches: touches, event: event)
    }

    override func touchesMoved(_ touches: Set<UITouch>, with event: UIEvent?) {
        super.touchesMoved(touches, with: event)
        handle(touches: touches, event: event)
    }

    override func touchesEnded(_ touches: Set<UITouch>, with event: UIEvent?) {
        super.touchesEnded(touches, with: event)
        handleCancel(touches: touches, event: event)
        isTouchMoved = false
    }

    override func touchesCancelled(_ touches: Set<UITouch>, with event: UIEvent?) {
        super.touchesCancelled(touches, with: event)
        handleCancel(touches: touches, event: event)
        isTouchMoved = false
    }

    func handle(touches: Set<UITouch>, event: UIEvent?) {
        guard let touch = touches.randomElement()
        else { return }
        let point = touch.location(in: self)
        if point.x < -20 {
            return
        }

        var index = Int(point.y / itemHeight)
        index = index < 0 ? 0 : index
        index = (index >= titlesArray.count) ? titlesArray.count - 1 : index
        didSelectRow(index: index, isTouch: true)
    }

    func didSelectRow(index: Int, isTouch: Bool) {
        if index == currentIndex {
            return
        }

        currentIndex = index
        for (index, label) in titleLabelsArray.enumerated() {
            if index == currentIndex {
                label.textColor = .darkBlueColor
                label.font = .mediumPoppin(with: 12)
            } else {
                label.textColor = .secondaryTextColor
                label.font = .mediumPoppin(with: 10)
            }
            label.sizeToFit()
        }

        if isTouch {
            showIndicator(with: index)
            let indexpath = IndexPath(row: 0, section: index)
            tableView?.scrollToRow(at: indexpath, at: .top, animated: false)
            UIImpactFeedbackGenerator(style: .light).impactOccurred()
        }
    }

    func handleCancel(touches: Set<UITouch>, event: UIEvent?) {
        self.dismissIndicator()
    }

    func dismissIndicator() {
        UIView.animate(withDuration: 0.25, delay: 0) {
            self.currentBackgroundView.alpha = 0
        }
    }

    func showIndicator(with index: Int) {
        let text = titlesArray[index]
        if text.isEmpty {
            return
        }
        
        currentLabel.text = text
        currentLabel.pin
            .sizeToFit()
            .center()

        currentBackgroundView.pin
            .top(to: edge.top)
            .marginTop(index.cgFloat * itemHeight)
            .right(to: edge.left)
            .marginRight(25)

        if currentBackgroundView.superview == nil {
            superview?.addSubview(currentBackgroundView)
        }

        superview?.bringSubviewToFront(currentBackgroundView)
        UIView.animate(withDuration: 0.25, delay: 0) {
            self.currentBackgroundView.alpha = 1
        }
    }

    override func layoutSubviews() {
        super.layoutSubviews()
        pin.height(titlesArray.count.cgFloat * itemHeight)

        for (index, label) in titleLabelsArray.enumerated() {
            label.pin
                .top(index.cgFloat * itemHeight)
                .hCenter()
                .sizeToFit()
        }

        currentLabel.pin
            .center()
            .sizeToFit()

        currentBackgroundView.pin
            .size(45)
        currentBackgroundView.cornerRadius = 8
    }
}
