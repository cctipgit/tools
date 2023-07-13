//
//  SwitchCurrencyInteractiveAnimation.swift
//  cryptovise
//
//  Created by fd on 2022/10/20.
//

import UIKit

class SwitchCurrencyInteractiveAnimation: UIPercentDrivenInteractiveTransition {
    weak var currentViewController: UIViewController?

    var panGestureRecognizer: UIPanGestureRecognizer
    var isInteracting = false
    init(currentViewController: UIViewController) {
        self.currentViewController = currentViewController
        panGestureRecognizer = UIPanGestureRecognizer()
        super.init()

        panGestureRecognizer.addTarget(self, action: #selector(handle(panGestureRecognizer:)))
        self.currentViewController?.view.addGestureRecognizer(panGestureRecognizer)
        panGestureRecognizer.delegate = self
    }

    @objc func handle(panGestureRecognizer: UIPanGestureRecognizer) {
        let translate = panGestureRecognizer.translation(in: panGestureRecognizer.view)
        var percent = abs(translate.x / panGestureRecognizer.view!.width)

        switch panGestureRecognizer.state {
        case .began:
            isInteracting = true
            currentViewController?.navigationController?.popViewController(animated: true)
        case .changed:
            percent = fmin(fmax(percent, 0.0), 1.0)
            update(percent)

        case .ended, .cancelled, .failed:

            let velocityX = panGestureRecognizer.velocity(in: panGestureRecognizer.view).x
            if velocityX < -500 {
                completionSpeed = 1 - percentComplete
                finish()
            } else if percent < 0.5 || panGestureRecognizer.state == .cancelled {
                completionSpeed = percentComplete
                cancel()
            } else {
                completionSpeed = 1 - percentComplete
                finish()
            }
            isInteracting = false

        default:
            break
        }
    }
}

extension SwitchCurrencyInteractiveAnimation: UIGestureRecognizerDelegate {
    func gestureRecognizerShouldBegin(_ gestureRecognizer: UIGestureRecognizer) -> Bool {
        if gestureRecognizer == panGestureRecognizer {
            let x = gestureRecognizer.location(in: gestureRecognizer.view).x
            let velocityX = panGestureRecognizer.velocity(in: gestureRecognizer.view).x
            let velocityY = panGestureRecognizer.velocity(in: gestureRecognizer.view).y

            if (gestureRecognizer.view?.width ?? 0) - x >= 64 {
                return false
            }
            if abs(velocityX) < abs(velocityY) {
                return false
            }

            return true
        }

        return true
    }
}
