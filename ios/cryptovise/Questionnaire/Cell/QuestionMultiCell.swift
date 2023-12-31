//
//  QuestionMultiCell.swift
//  cryptovise
//
//  Created by admin on 2023/7/6.
//

import UIKit
import Then
import SnapKit

protocol QuestionCellDelegate: AnyObject {
    func didSelectIndexChange(qID: String, selected: Set<Int>)
}

class QuestionMultiCell: UITableViewCell, UICollectionViewDelegate {
    private let titleLabel = UILabel().then { v in
        v.font = .robotoBold(with: 16)
        v.textColor = .basicBlk
        v.numberOfLines = 2
        v.adjustsFontSizeToFitWidth = true
    }
    
    private var collectView: UICollectionView!
    private var data: QuizSonQuestionItem = QuizSonQuestionItem()
    private var selectedIndex: Set<Int> = [0]
    private var allCells: Set<QuizQuestionItemCell> = Set<QuizQuestionItemCell>()
    weak var delegate: QuestionCellDelegate?
    
    // MARK: Super Method
    override init(style: UITableViewCell.CellStyle, reuseIdentifier: String?) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)
        p_setElements()
        updateConstraints()
    }

    required init?(coder: NSCoder) {
        super.init(coder: coder)
    }
    
    override func updateConstraints() {
        super.updateConstraints()
        titleLabel.snp.makeConstraints { make in
            make.height.greaterThanOrEqualTo(20)
            make.top.equalToSuperview()
            make.left.equalToSuperview().offset(16)
            make.right.equalToSuperview().offset(-16)
        }
        collectView.snp.makeConstraints { make in
            make.left.equalToSuperview().inset(16)
            make.right.equalToSuperview().inset(16)
            make.top.equalTo(titleLabel.snp.bottom).offset(16)
            make.bottom.equalToSuperview().inset(24)
        }
    }
    override func systemLayoutSizeFitting(_ targetSize: CGSize, withHorizontalFittingPriority horizontalFittingPriority: UILayoutPriority, verticalFittingPriority: UILayoutPriority) -> CGSize {
        let size = super.systemLayoutSizeFitting(targetSize, withHorizontalFittingPriority: horizontalFittingPriority, verticalFittingPriority: verticalFittingPriority)
        self.collectView.layoutIfNeeded()
        let height = self.collectView.collectionViewLayout.collectionViewContentSize.height
        return CGSizeMake(size.width, size.height + height)
    }
    
    // MARK: Private Method
    private func p_setElements() {
        let flowLayout = UICollectionViewLeftAlignedLayout()
        flowLayout.estimatedItemSize = CGSize(width: 114, height: 40)
        collectView = UICollectionView(frame: .zero, collectionViewLayout: flowLayout)
        collectView.dataSource = self
        collectView.delegate = self
        collectView.isScrollEnabled = false
        collectView.register(QuizQuestionItemCell.self, forCellWithReuseIdentifier: "QuizQuestionItemCell")
        contentView.addSubviews([titleLabel, collectView])
        self.backgroundColor = .white
        contentView.backgroundColor = .white
    }
    
    // MARK: Public Method
    public func setData(item: QuizSonQuestionItem) {
        titleLabel.text = item.title
        self.data = item
        collectView.reloadData()
    }
    
    static func calculateItemSize(str: String) -> CGSize {
        let strSize = NSString(string: str).boundingRect(with: CGSize(width: Double.greatestFiniteMagnitude, height: 40), options: .usesLineFragmentOrigin, attributes: [NSAttributedString.Key.font: UIFont.robotoRegular(with: 14) as Any], context: nil)
        let maxWidth: CGFloat = UIDevice.kScreenWidth() - 72
        var needWidth: CGFloat = strSize.width + 40
        var needHeight: CGFloat = 40
        if needWidth > maxWidth {
            needHeight = 60
        }
        if needWidth > maxWidth {
            needWidth = maxWidth
        }
        return CGSize(width: needWidth, height: needHeight)
    }
    
    static func calculateHeaderHeight(str: String) -> CGFloat {
        let boundHeight: CGFloat = NSString(string: str).boundingRect(with: CGSize(width: UIDevice.kScreenWidth() - 72,
                                                                                   height: Double.greatestFiniteMagnitude),
                                                                      options: .usesLineFragmentOrigin,
                                                                      attributes: [NSAttributedString.Key.font: UIFont.robotoBold(with: 32) as Any],
                                                                      context: nil).height + 40
        let height: CGFloat = (boundHeight < 80) ? 80.0 : boundHeight
        return height
    }
}

extension QuestionMultiCell: UICollectionViewDataSource, UICollectionViewDelegateLeftAlignedLayout {
    func numberOfSections(in collectionView: UICollectionView) -> Int {
        return 1
    }
    
    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        return data.options.count
    }
    
    func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        let cell = collectionView.dequeueReusableCell(withReuseIdentifier: "QuizQuestionItemCell", for: indexPath) as! QuizQuestionItemCell
        if indexPath.row == 0 {
            cell.label.backgroundColor = .primaryBranding
            cell.label.textColor = .white
        } else {
            cell.label.backgroundColor = .bgSecondary
            cell.label.textColor = .contentDisabled
        }
        allCells.insert(cell)
        cell.label.text = "\(data.options[indexPath.row])"
        return cell
    }

    func collectionView(_ collectionView: UICollectionView, didSelectItemAt indexPath: IndexPath) {
        collectionView.deselectItem(at: indexPath, animated: false)
        if data.questionType == .single {
            allCells.forEach({ $0.setSelected(isSelect: false) })
            selectedIndex.removeAll()
            selectedIndex.insert(indexPath.row)
            if let cell = collectionView.cellForItem(at: indexPath) as? QuizQuestionItemCell {
                cell.setSelected(isSelect: true)
            }
        } else {
            if !selectedIndex.contains(indexPath.row) {
                selectedIndex.insert(indexPath.row)
                if let cell = collectionView.cellForItem(at: indexPath) as? QuizQuestionItemCell {
                    cell.setSelected(isSelect: true)
                }
            } else if selectedIndex.contains(indexPath.row) && selectedIndex.count > 1 {
                selectedIndex.remove(indexPath.row)
                if let cell = collectionView.cellForItem(at: indexPath) as? QuizQuestionItemCell {
                    cell.setSelected(isSelect: false)
                }
            }
        }
        delegate?.didSelectIndexChange(qID: data.id, selected: self.selectedIndex)
    }
}

class QuizQuestionItemCell: UICollectionViewCell {
    let label = LabelInset().then { v in
        v.layer.cornerRadius = 8
        v.font = .robotoRegular(with: 14)
        v.textAlignment = .left
        v.clipsToBounds = true
        v.numberOfLines = 2
        v.contentInset = UIEdgeInsets(top: 0, left: 16, bottom: 0, right: 16)
    }
    
    override init(frame: CGRect) {
        super.init(frame: frame)
        self.addSubview(label)
        contentView.layer.cornerRadius = 8
        contentView.clipsToBounds = true
        label.snp.makeConstraints { (make) in
            make.edges.equalToSuperview()
        }
    }
    
    public func setSelected(isSelect: Bool) {
        if isSelect {
            label.backgroundColor = .primaryBranding
            label.textColor = .white
        } else {
            label.backgroundColor = .bgSecondary
            label.textColor = .contentDisabled
        }
    }
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    override func preferredLayoutAttributesFitting(_ layoutAttributes: UICollectionViewLayoutAttributes) -> UICollectionViewLayoutAttributes {
        self.setNeedsLayout()
        self.layoutIfNeeded()
        let attributes = super.preferredLayoutAttributesFitting(layoutAttributes)
        let size = QuestionMultiCell.calculateItemSize(str: label.text ?? "")
        var cellFrame = attributes.frame
        cellFrame.size.width = size.width
        cellFrame.size.height = 40
        attributes.frame = cellFrame
        return attributes
    }
}
