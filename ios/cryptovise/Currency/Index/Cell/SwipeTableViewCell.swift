//
//  SwipeTableViewCell.swift
//  xCurrency
//
//  Created by fd on 2022/10/19.
//

import UIKit

class SwipeTableViewCell: UITableViewCell {
    enum SwipeDirection {
        case left
        case right
        case center
    }

    var containerView: UIView = {
        UIView().then { view in
            view.backgroundColor = .backgroundColor
        }
    }()

    var leftContainerView: SwitchCurrencyItemView = {
        SwitchCurrencyItemView(frame: .zero).then { _ in
        }
    }()

    var rightContainerView: SwitchDetailItemView = {
        SwitchDetailItemView().then { _ in
        }
    }()

    var mainContainer: UIView = {
        UIView().then { view in
            view.backgroundColor = .backgroundColor
        }
    }()

    lazy var panGestureRecognizer: UIPanGestureRecognizer = {
        UIPanGestureRecognizer(target: self, action: #selector(handlePan(gestureRecognizer:))).then { pan in
            pan.delegate = self
            pan.cancelsTouchesInView = true
            pan.maximumNumberOfTouches = 1
        }
    }()

    lazy var tapGestureRecognizer: UITapGestureRecognizer = {
        UITapGestureRecognizer(target: self, action: #selector(handleTap(gestureRecognizer:))).then { tap in
            tap.delegate = self
        }
    }()

    var leadingAction: (() -> Void)?
    var trailingAction: (() -> Void)?

    var isDrag = false

    var direction: SwipeDirection = .center
    let durationHighLimit = 0.1
    let durationLowLimit = 0.35
    let defaultVelocity: CGFloat = 40
    var startVelocity: CGFloat = 0

    weak var tableView: UITableView?

    override init(style: UITableViewCell.CellStyle, reuseIdentifier: String?) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)
        mainContainer.addSubviews([leftContainerView,
                                   containerView,
                                   rightContainerView])
        contentView.addSubview(mainContainer)

        addGestureRecognizer(panGestureRecognizer)
        addGestureRecognizer(tapGestureRecognizer)

        containerView.clipsToBounds = true
        makeUI()
        makeEvent()
    }

    required init?(coder: NSCoder) {
        super.init(coder: coder)
    }

    func makeUI() {
    }

    func makeEvent() {
    }

    func makeLayout() {
    }

    override func prepareForReuse() {
        super.prepareForReuse()
        direction = .center
        isDrag = false
    }

    override func layoutSubviews() {
        super.layoutSubviews()

        if isDrag {
            return
        }
        mainContainer.pin
            .vertically()
            .hCenter()
            .width(300%)

        leftContainerView.pin.left()
            .vertically()
            .width(of: contentView)

        containerView.pin
            .vertically()
            .after(of: leftContainerView)
            .width(of: contentView)

        rightContainerView.pin
            .vertically()
            .after(of: containerView)
            .right()
        makeLayout()
    }

    override func willMove(toSuperview newSuperview: UIView?) {
        super.willMove(toSuperview: newSuperview)
        var parent = newSuperview
        while !(parent?.isKind(of: UITableView.self) ?? false) {
            parent = parent?.superview
        }

        if parent != nil {
            tableView = parent as? UITableView
            tableView?.panGestureRecognizer.removeTarget(self, action: #selector(handleTableViewPan(gestureRecognizer:)))
            tableView?.panGestureRecognizer.addTarget(self, action: #selector(handleTableViewPan(gestureRecognizer:)))
        }
    }

    @objc func handleTap(gestureRecognizer: UITapGestureRecognizer) {
        for cell in tableView?.visibleCells.compactMap({ $0 as? SwipeTableViewCell }) ?? [] {
            cell.animateBackToOrigin(duration: durationLowLimit, velocity: defaultVelocity)
        }
    }

    @objc func handleTableViewPan(gestureRecognizer: UIPanGestureRecognizer) {
        if gestureRecognizer.state == .began {
            animateBackToOrigin(duration: durationLowLimit, velocity: defaultVelocity)
        }
    }

    // do nothing
    @objc func handlePan(gestureRecognizer: UIPanGestureRecognizer) {
        func panAction() {
            let translation = gestureRecognizer.translation(in: mainContainer)
            let velocity = gestureRecognizer.velocity(in: mainContainer)

            let duration = animationDuration(with: velocity)

            let relativeOffset = mainContainer.convert(containerView.frame.origin, to: mainContainer.superview)
            let percentage = percentage(with: relativeOffset.x, relativeToWidth: width)

            direction = direction(with: percentage)

            switch gestureRecognizer.state {
            case .began:
                startVelocity = velocity.x
                let visibleCells = tableView?.visibleCells.compactMap({ $0 as? SwipeTableViewCell })
                    .filter({ $0.isDrag && $0 != self }) ?? []

                if !visibleCells.isEmpty {
                    isDrag = false
                    return
                }

                isDrag = true

                for cell in visibleCells {
                    cell.animateBackToOrigin(duration: duration, velocity: defaultVelocity)
                }
            case .changed:
                if !isDrag {
                    return
                }
                let x = mainContainer.x + translation.x
                mainContainer.pin.left(x)
                gestureRecognizer.setTranslation(.zero, in: self)

            case .cancelled, .ended, .failed:
                if !isDrag {
                    return
                }

                let isReverse = isReverseVelocity(with: velocity.x)
                if isReverse {
                    animateBackToOrigin(duration: 0.5, velocity: 0, damping: 0.55)
                    return
                }

                if abs(percentage) >= 0.2 {
                    animateLeftOrRight(duration: 0.12, velocity: defaultVelocity)
                    isDrag = true
                } else {
                    animateBackToOrigin(duration: 0.5, velocity: 0,damping: 0.55)
                }

            default:
                break
            }
        }
    }

    func isReverseVelocity(with velocity: CGFloat) -> Bool {
        if abs(startVelocity + velocity) < abs(startVelocity) + abs(velocity) {
            return true
        } else {
            return false
        }
    }

    override func gestureRecognizerShouldBegin(_ gestureRecognizer: UIGestureRecognizer) -> Bool {
        if gestureRecognizer == panGestureRecognizer {
            let point = panGestureRecognizer.velocity(in: mainContainer)

            return abs(point.x) >= abs(point.y)

        } else if gestureRecognizer == tapGestureRecognizer {
            let cell = tableView?.visibleCells.compactMap { $0 as? SwipeTableViewCell }.first(where: {
                $0.isDrag
            })
            return cell != nil
        }
        return true
    }

    func percentage(with offset: CGFloat, relativeToWidth: CGFloat) -> CGFloat {
        var percentage = offset / width

        if percentage < -1.0 {
            percentage = -1.0
        } else if percentage > 1.0 {
            percentage = 1.0
        }

        return percentage
    }

    func animationDuration(with velocity: CGPoint) -> CGFloat {
        var horizontalVelocity = velocity.x

        let durationDiff = durationHighLimit - durationLowLimit

        if horizontalVelocity < -width {
            horizontalVelocity = -width
        } else if horizontalVelocity > width {
            horizontalVelocity = width
        }

        return durationHighLimit + durationLowLimit - abs(horizontalVelocity / width * durationDiff)
    }

    func direction(with percentage: CGFloat) -> SwipeDirection {
        if percentage < 0 {
            return .left
        } else if percentage > 0 {
            return .right
        } else {
            return .center
        }
    }

    func animateBackToOrigin(duration: CGFloat, velocity: CGFloat, damping: Double = 0.7) {
        let animator = UIViewSpringAnimator(duration: duration,
                                            damping: damping,
                                            initialVelocity: velocity)
        animator.animations = {
            self.mainContainer.pin.center()
        }
        animator.completion = { _ in
            self.isDrag = false
        }
        animator.startAnimation()
    }

    func animateLeftOrRight(duration: CGFloat, velocity: CGFloat) {
        
        UIView.animate(withDuration: duration, delay: 0, options: [.curveLinear]) {
            if self.direction == .left {
                self.mainContainer.pin.right()
            } else if self.direction == .right {
                self.mainContainer.pin.left()
            } else {
                self.mainContainer.pin.center()
            }
        } completion: { _ in
            if self.direction == .left {
                self.trailingAction?()

            } else if self.direction == .right {
                self.leadingAction?()
            }
        }
    }
}
