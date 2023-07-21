//
//  SettingTableViewCell.swift
//  cryptovise
//
//  Created by fd on 2022/11/2.
//

import RxSwift
import UIKit
class SettingTableViewCell: UITableViewCell {
    var disposeBag = DisposeBag()
    var titleLabel = UILabel().then { label in
        label.font = .robotoRegular(with: 16)
        label.textColor = .basicBlk
    }

    var valueLabel = UILabel().then { label in
        label.font = .robotoRegular(with: 16)
        label.textColor = .contentSecondary
        label.textAlignment = .right
    }

    var arrowImageView = UIImageView().then { imv in
        imv.image = UIImage(named: "n_arrow_right")
    }

    override init(style: UITableViewCell.CellStyle, reuseIdentifier: String?) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)
        contentView.addSubviews([titleLabel,
                                 valueLabel,
                                 arrowImageView])
        selectedBackgroundView = UIView().then({ view in
            view.backgroundColor = .grayColor
        })
        self.backgroundColor = .backgroundColor
    }

    required init?(coder: NSCoder) {
        super.init(coder: coder)
    }

    override func prepareForReuse() {
        super.prepareForReuse()
        disposeBag = DisposeBag()
    }

    func bind(viewModel: SettingCellViewModel) {
        guard let item = viewModel as? SettingArrowViewModel
        else {
            return
        }

        titleLabel.text = item.title
        item.value.bind(to: valueLabel.rx.text)
            .disposed(by: disposeBag)
    }

    override func layoutSubviews() {
        super.layoutSubviews()
        titleLabel.pin.left(23)
            .vCenter()
            .sizeToFit()

        arrowImageView.pin.right(20)
            .vCenter()
            .size(16)

        valueLabel.pin.before(of: arrowImageView)
            .marginRight(8)
            .vCenter()
            .sizeToFit()

        selectedBackgroundView?.pin
            .vertically()
            .horizontally(8)
        selectedBackgroundView?.cornerRadius = 16
    }

    override func sizeThatFits(_ size: CGSize) -> CGSize {
        return CGSizeMake(width, 56)
    }
}
