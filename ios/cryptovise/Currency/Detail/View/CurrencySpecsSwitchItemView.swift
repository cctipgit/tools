//
//  CurrencySpecsSwitchItemView.swift
//  cryptovise
//
//  Created by fd on 2022/11/1.
//

import SnapKit
import UIKit

class CurrencySpecsSwitchItemView: UIView {
    var currencyImageView = UIImageView().then {
        $0.layer.cornerRadius = 10
        $0.clipsToBounds = true
        $0.contentMode = .scaleAspectFill
    }

    var amountLabel = UILabel().then { label in
        label.font = .robotoBold(with: 16)
        label.textColor = .basicBlk
    }

    var tokenLabel = {
        UILabel().then {
            $0.font = .robotoBold(with: 16)
            $0.textColor = .basicBlk
        }
    }()

    var selectIconView = {
        UIImageView().then {
            $0.image = UIImage(named: "n_arrow_down")
        }
    }()

    var isPureCurrencySpecItem: Bool = false {
        didSet {
            self.makeConstraint()
        }
    }

    override init(frame: CGRect) {
        super.init(frame: frame)
        makeUI()
    }

    required init?(coder: NSCoder) {
        super.init(coder: coder)
        makeUI()
    }

    func makeUI() {
        addSubviews([currencyImageView,
                     amountLabel,
                     tokenLabel,
                     selectIconView])
    }

    func makeConstraint() {
        if isPureCurrencySpecItem {
            amountLabel.isHidden = false
            if selectIconView.isHidden {
                selectIconView.snp.removeConstraints()
                
                currencyImageView.snp.remakeConstraints({
                    $0.size.equalTo(20)
                    $0.left.equalToSuperview()
                    $0.centerY.equalToSuperview()
                    $0.top.bottom.equalToSuperview().inset(4)
                })
                
            } else {
                selectIconView.snp.remakeConstraints {
                    $0.size.equalTo(16)
                    $0.left.equalToSuperview()
                    $0.centerY.equalToSuperview()
                }

                currencyImageView.snp.remakeConstraints({
                    $0.size.equalTo(20)
                    $0.left.equalTo(selectIconView.snp.right).offset(5)
                    $0.centerY.equalToSuperview()
                    $0.top.bottom.equalToSuperview().inset(4)
                })
            }
            amountLabel.snp.remakeConstraints {
                $0.centerY.equalToSuperview()
                $0.left.equalTo(currencyImageView.snp.right).offset(4)
            }

            tokenLabel.snp.remakeConstraints {
                $0.centerY.equalToSuperview()
                $0.left.equalTo(amountLabel.snp.right).offset(2)
                $0.right.equalToSuperview()
            }
        } else {
            amountLabel.isHidden = true
            currencyImageView.snp.remakeConstraints({
                $0.size.equalTo(20)
                $0.left.equalToSuperview()
                $0.centerY.equalToSuperview()
                $0.top.bottom.equalToSuperview().inset(4)
            })

            amountLabel.snp.removeConstraints()

            if selectIconView.isHidden {
                selectIconView.snp.removeConstraints()
                
                tokenLabel.snp.remakeConstraints {
                    $0.centerY.equalToSuperview()
                    $0.right.equalToSuperview()
                    $0.left.equalTo(currencyImageView.snp.right).offset(5)
                }
                
            } else {
                tokenLabel.snp.remakeConstraints {
                    $0.centerY.equalToSuperview()
                    $0.left.equalTo(currencyImageView.snp.right).offset(5)
                }

                selectIconView.snp.remakeConstraints {
                    $0.size.equalTo(16)
                    $0.left.equalTo(tokenLabel.snp.right).offset(5)
                    $0.right.equalToSuperview()
                    $0.centerY.equalToSuperview()
                }
            }
        }
    }
}
