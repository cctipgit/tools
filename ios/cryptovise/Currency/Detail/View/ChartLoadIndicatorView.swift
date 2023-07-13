//
//  ChartFloatView.swift
//  cryptovise
//
//  Created by fd on 2022/11/22.
//

import UIKit

class ChartLoadIndicatorView: UIView {
    var indicatorView = UIActivityIndicatorView(frame: .zero).then { indicator in
        indicator.color = .primaryTextColor
        indicator.style = .large
    }

    override init(frame: CGRect) {
        super.init(frame: frame)
        addSubview(indicatorView)

        backgroundColor = .backgroundColor.alpha(0.6)
    }

    required init?(coder: NSCoder) {
        super.init(coder: coder)
    }

    override var isHidden: Bool {
        didSet {
            if isHidden {
                indicatorView.stopAnimating()
            } else {
                indicatorView.startAnimating()
            }
        }
    }
    
    func isAnimating() -> Bool {
        return self.indicatorView.isAnimating
    }

    override func layoutSubviews() {
        super.layoutSubviews()
        indicatorView.pin
            .center()
    }
}
