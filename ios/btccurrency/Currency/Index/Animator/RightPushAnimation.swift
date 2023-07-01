//
//  File.swift
//  btccurrency
//
//  Created by fd on 2022/10/20.
//

import Foundation

class RightPushAnimation: NSObject, UIViewControllerAnimatedTransitioning {
    func transitionDuration(using transitionContext: UIViewControllerContextTransitioning?) -> TimeInterval {
        return 0.3
    }

    func animateTransition(using transitionContext: UIViewControllerContextTransitioning) {
        guard let toVC = transitionContext.viewController(forKey: .to),
              let fromVC = transitionContext.viewController(forKey: .from)
        else {
            transitionContext.completeTransition(true)
            return
        }

        let containerView = transitionContext.containerView
        containerView.addSubview(toVC.view)

        toVC.view.pin.vertically().before(of: fromVC.view)

        let duration = transitionDuration(using: transitionContext)

        UIView.animate(withDuration: duration, delay: 0, options: [.curveEaseIn]) {
            toVC.view.pin.center()
            fromVC.view.pin.vertically().after(of: toVC.view)
        } completion: { _ in
            transitionContext.completeTransition(true)
        }
    }
}
