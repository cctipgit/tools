//
//  SettingSwitchTableViewCell.swift
//  cryptovise
//
//  Created by fd on 2022/11/2.
//

import RxSwift
import UIKit

class SettingSwitchTableViewCell: UITableViewCell {
    var titleLabel = UILabel().then { label in
        label.font = .regularPoppin(with: 16)
        label.textColor = .primaryTextColor
    }

    var disposeBag = DisposeBag()
    var switchView = CustomSwitch(frame: CGRectMake(0, 0, 44, 26))

    override init(style: UITableViewCell.CellStyle, reuseIdentifier: String?) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)
        contentView.addSubviews([titleLabel,
                                 switchView])
        selectionStyle = .none
        backgroundColor = .backgroundColor
    }

    required init?(coder: NSCoder) {
        super.init(coder: coder)
    }

    override func prepareForReuse() {
        super.prepareForReuse()
        disposeBag = DisposeBag()
    }

    func bind(viewModel: SettingCellViewModel, localMark: Bool = false) {
        guard let item = viewModel as? SettingSwitchViewModel
        else { return }

        titleLabel.text = item.title
        item.isOn
            .subscribe(onNext: { [weak self] isOn in
                self?.switchView.isOn = isOn
            })
            .disposed(by: disposeBag)

        if localMark {
            switchView.isLocked = !LocationManager.shared.locationEnabled
            switchView.lockedHandler = { [weak self] in
                if let superView = self?.parentViewController?.view {
                    let alert = CustomAlertConfirmView()
                    alert.show(in: superView)
                }
            }

            switchView.valueChangedHandler = { value in
                item.isOn.accept(value)
            }

        } else {
            switchView.valueChangedHandler = { value in
                item.isOn.accept(value)
            }
        }
    }

    override func layoutSubviews() {
        super.layoutSubviews()
        titleLabel.pin.left(23)
            .vCenter()
            .sizeToFit()

        switchView.pin.right(23)
            .vCenter()
            .size(CGSizeMake(44, 26))
            .sizeToFit()

        switchView.cornerRadius = switchView.height / 2
    }

    override func sizeThatFits(_ size: CGSize) -> CGSize {
        return CGSizeMake(width, 72)
    }
}
