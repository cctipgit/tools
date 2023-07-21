//
//  LeftPopViewAnimation.swift
//  cryptovise
//
//  Created by fd on 2022/10/20.
//

import UIKit

class LeftPopViewAnimation: NSObject, UIViewControllerAnimatedTransitioning {
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

        let finalFrame = transitionContext.finalFrame(for: toVC)

        toVC.view.pin.vertically().after(of: fromVC.view).margin(0)
        
        let duration = transitionDuration(using: transitionContext)

        UIView.animate(withDuration: duration, delay: 0, options: [.curveEaseIn, .layoutSubviews]) {
            toVC.view.frame = finalFrame
            fromVC.view.pin.vertically().before(of: toVC.view)

        } completion: { _ in

            transitionContext.completeTransition(!transitionContext.transitionWasCancelled)
        }
    }
}
