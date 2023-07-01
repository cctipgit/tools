//
//  CurrencySearchView.swift
//  btccurrency
//
//  Created by fd on 2022/11/16.
//

import RxCocoa
import RxSwift
import UIKit
import Then

@objc protocol CurrencySearchViewDelegate: AnyObject {
    func searchBarShouldBeginEditing(_ searchBar: CurrencySearchView) -> Bool
    func searchBarCancelButtonClicked(_ searchBar: CurrencySearchView)
}

class CurrencySearchView: UIView {
    var leftView = UIView()
    var rightView = UIView()
    let leftImageView = UIImageView().then { v in
        v.image = UIImage(named: "n_search")
    }
    let rightImageView = UIImageView().then { v in
        v.image = UIImage(named: "n_cancel")
    }
    var delegate: CurrencySearchViewDelegate?

    var searchField = UITextField().then {
        $0.font = .robotoRegular(with: 14)
        $0.placeholder = "Search".localized()
        $0.leftViewMode = .always
        $0.rightViewMode = .whileEditing
    }

    override init(frame: CGRect) {
        super.init(frame: frame)
        makeUI()
        makeEvent()
    }

    required init?(coder: NSCoder) {
        super.init(coder: coder)
    }

    func makeUI() {
        addSubview(searchField)
        leftView.addSubview(leftImageView)
        rightView.addSubview(rightImageView)
        searchField.leftView = leftView
        searchField.rightView = rightView
        backgroundColor = UIColor(red: 0.98, green: 0.98, blue: 0.98, alpha: 1)
        cornerRadius = 16
        searchField.delegate = self
        rightImageView.isUserInteractionEnabled = true
    }

    func makeEvent() {
        searchField.rx.controlEvent(.editingDidEnd)
            .subscribe(onNext: { [weak self] in
                self?.changeIconSearch()
            })
            .disposed(by: rx.disposeBag)
        rightView.addGestureRecognizer(UITapGestureRecognizer(target: self,action: #selector(clearSearchField(tap:))))
    }

    @objc func clearSearchField(tap: UITapGestureRecognizer) {
        if !searchField.isEditing {
            return
        }
        searchField.text = nil
        delegate?.searchBarCancelButtonClicked(self)
    }

    override func layoutSubviews() {
        super.layoutSubviews()
        searchField.pin.horizontally(10).vertically()
        
        leftView.pin.height(searchField.height)
        leftImageView.pin.size(16).centerLeft()
        leftView.frame = CGRectMake(0, 0, 32, searchField.height)
        
        rightView.pin.height(searchField.height)
        rightImageView.pin.size(16).centerRight()
        rightView.frame = CGRectMake(0, 0, 32, searchField.height)
    }

    func changeIconClear() {
        rightImageView.isHidden = false
    }

    func changeIconSearch() {
        rightImageView.isHidden = true
    }
}

extension CurrencySearchView: UITextFieldDelegate {
    func textFieldShouldBeginEditing(_ textField: UITextField) -> Bool {
        changeIconClear()
        return delegate?.searchBarShouldBeginEditing(self) ?? true
    }

    func textFieldDidEndEditing(_ textField: UITextField) {
        changeIconSearch()
    }
}
