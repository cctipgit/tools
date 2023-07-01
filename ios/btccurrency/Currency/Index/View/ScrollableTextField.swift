//
//  ScrollableTextField.swift
//  btccurrency
//
//  Created by fd on 2022/10/26.
//

import UIKit

// ref: https://github.com/sunshuyao/ScrollableTextField
enum TextRangeChangedType: Int {
    case leftAndBack = 0
    case leftAndForward
    case rightAndBack
    case rightAndForward
    case none
}

protocol InnerTextFieldDelegate: AnyObject {
    func textDidSet()
    func placeholderDidSet()
}

public class ScrollableTextField: UIView {
    
    struct Constant {
        static let rangeChange: CGFloat = 15
        static let textFieldHeight: CGFloat = 27
    }
    
    // MARK: - UI
    private(set) var textField: InnerTextField!
    private var scrollView: UIScrollView!
    private var contentView: UIView!
    var tapGesture: UITapGestureRecognizer!
    var panGesture: UIPanGestureRecognizer!
    private weak var textFieldWidthConstraint: NSLayoutConstraint!
    
    // MARK: - Properties
    private let paddingRight: CGFloat = 3
    private var startPoint: CGPoint = .zero
    private var startOffset: CGPoint = .zero
    private var shouldScroll: Bool = false
    
    // MARK: - Life circle
    public override init(frame: CGRect) {
        super.init(frame: frame)
        configureSubviews()
    }
    
    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
        configureSubviews()
    }
    
    // MARK: - Funcs
    private func configureSubviews() {
        // setup scroll view
        scrollView = UIScrollView(frame: .zero)
        scrollView.showsVerticalScrollIndicator = false
        scrollView.showsHorizontalScrollIndicator = false
        scrollView.bounces = false
        scrollView.delegate = self
        scrollView.backgroundColor = .clear
        self.addSubview(scrollView)
            
        scrollView.translatesAutoresizingMaskIntoConstraints = false
        NSLayoutConstraint.activate([
            scrollView.leftAnchor.constraint(equalTo: self.leftAnchor),
            scrollView.rightAnchor.constraint(equalTo: self.rightAnchor),
            scrollView.topAnchor.constraint(equalTo: self.topAnchor),
            scrollView.bottomAnchor.constraint(equalTo: self.bottomAnchor)
        ])
        
        // setup content view
        contentView = UIView(frame: CGRect(origin: .zero, size: scrollView.contentSize))
        scrollView.addSubview(contentView)
        
        contentView.translatesAutoresizingMaskIntoConstraints = false
        let contentViewWidthConstraint = contentView.widthAnchor.constraint(equalTo: self.widthAnchor)
        contentViewWidthConstraint.priority = .defaultHigh
        NSLayoutConstraint.activate([
            contentViewWidthConstraint,
            contentView.topAnchor.constraint(equalTo: self.topAnchor),
            contentView.bottomAnchor.constraint(equalTo: self.bottomAnchor)
        ])
            
        // setup text field
        textField = InnerTextField(frame: .zero)
        textField.innerDelegate = self
        textField.addTarget(self, action: #selector(handleTextField(_:)), for: .editingChanged)
        textField.selectedTextRangeChangedBlock = {[weak self] (type, width) in
            guard let scrollView = self?.scrollView else {
                return
            }

            let div: CGFloat = Constant.rangeChange
            if width < scrollView.contentOffset.x + div {
                UIView.animate(withDuration: 0.1, animations: {
                    scrollView.contentOffset.x = max(width - div, 0)
                })
            } else if width > scrollView.contentOffset.x + scrollView.bounds.width - div {
                UIView.animate(withDuration: 0.1, animations: {
                    scrollView.contentOffset.x = width - scrollView.bounds.width + div
                })
            }
        }
        contentView.addSubview(textField)
        textFieldWidthConstraint = textField.widthAnchor.constraint(equalToConstant: scrollView.contentSize.width)
        textField.translatesAutoresizingMaskIntoConstraints = false
        NSLayoutConstraint.activate([
            textField.leftAnchor.constraint(equalTo: contentView.leftAnchor),
            textField.rightAnchor.constraint(equalTo: contentView.rightAnchor),
            textField.centerYAnchor.constraint(equalTo: contentView.centerYAnchor),
            textField.heightAnchor.constraint(equalToConstant: Constant.textFieldHeight),
            textFieldWidthConstraint
        ])
        
        // to fire an editing changed event.
        textField.text = ""
        
        tapGesture = UITapGestureRecognizer(target: self, action: #selector(handleTap(_:)))
        tapGesture.delegate = self
        textField.addGestureRecognizer(tapGesture)
        
        panGesture = UIPanGestureRecognizer(target: self, action: #selector(handlePan(_:)))
        textField.addGestureRecognizer(panGesture)
    }
    
    // MARK: - Actions
    @objc private func handleTap(_ gesture: UITapGestureRecognizer) {
        let point = gesture.location(in: textField)
        if let closestPosition = textField.closestPosition(to: point) {
            textField?.selectedTextRange = textField?.textRange(from: closestPosition, to: closestPosition)
        }
    }
    
    @objc private func handlePan(_ gesture: UIPanGestureRecognizer) {
        switch gesture.state {
        case .began:
            startPoint = gesture.translation(in: contentView)
            startOffset = scrollView.contentOffset
            shouldScroll = true
        case .changed:
            let currentPoint = gesture.translation(in: contentView)
            let deltaX = currentPoint.x - startPoint.x
            let newOffset = CGPoint(x: startOffset.x - deltaX, y: startOffset.y)
            scrollView.setContentOffset(newOffset, animated: false)
            shouldScroll = true
        default:
            shouldScroll = false
            break
        }
    }
    
    // to update the width of text textField
    @objc func handleTextField(_ textField: UITextField) {
        guard let scrollView = self.scrollView, let hookedTF = textField as? InnerTextField else { return }

        guard let widthToCursor = hookedTF.getWidthFromDocumentBeginingToCursor(), let fullWidth = hookedTF.getWidthFromDocumentBeginingToEnd() else { return }
        
        var contentWidth = fullWidth + paddingRight
        if contentWidth < self.bounds.width {
            contentWidth = self.bounds.width
        }
        textFieldWidthConstraint.constant = contentWidth
        scrollView.contentSize.width = contentWidth
        self.layoutIfNeeded()
        
        if contentWidth > self.bounds.width {
            
            if contentWidth - widthToCursor < scrollView.bounds.width * 0.5  {
                // end of the scroll view --> remain fixed in position
                
                var contentOffset = scrollView.contentOffset
                contentOffset.x = contentWidth - scrollView.bounds.width
                scrollView.setContentOffset(contentOffset, animated: false)
            }

            else { // --> let its position move to the center.
                
                var contentOffset = scrollView.contentOffset
                contentOffset.x = widthToCursor - scrollView.bounds.width * 0.5
                scrollView.setContentOffset(contentOffset, animated: true)
            }
        }
    }
    
    func handleTextFieldPlaceholder(_ textField: UITextField) {
        guard let scrollView = self.scrollView, let hookedTF = textField as? InnerTextField else { return }
        if !textField.isEmpty {
            return
        }
        
        let widthToCursor = hookedTF.getWidthFrom(string: textField.placeholder.or(""))
        let fullWidth = hookedTF.getWidthFrom(string: textField.placeholder.or(""))
        var contentWidth = fullWidth + paddingRight
        if contentWidth < self.bounds.width {
            contentWidth = self.bounds.width
        }
        textFieldWidthConstraint.constant = contentWidth
        scrollView.contentSize.width = contentWidth
        self.layoutIfNeeded()
        
        if contentWidth > self.bounds.width {
            
            if contentWidth - widthToCursor < scrollView.bounds.width * 0.5  {
                // end of the scroll view --> remain fixed in position
                
                var contentOffset = scrollView.contentOffset
                contentOffset.x = contentWidth - scrollView.bounds.width
                scrollView.setContentOffset(contentOffset, animated: false)
            }

            else { // --> let its position move to the center.
                
                var contentOffset = scrollView.contentOffset
                contentOffset.x = widthToCursor - scrollView.bounds.width * 0.5
                scrollView.setContentOffset(contentOffset, animated: true)
            }
        }

    }
    
    func scroll(toOffset offset: CGPoint) {
        var newOffset = offset
        if newOffset.x < 0 {
            newOffset.x = 0
            scrollView.setContentOffset(newOffset, animated: true)
            return
        }
        
        let maxOffsetX = scrollView.contentSize.width - scrollView.bounds.width
        if newOffset.x > maxOffsetX {
            newOffset.x = maxOffsetX
        }
        
        scrollView.setContentOffset(newOffset, animated: true)
    }
    
    public override func hitTest(_ point: CGPoint, with event: UIEvent?) -> UIView? {
        if self.bounds.contains(point) {
            if shouldScroll {
                return scrollView
            } else {
                return textField
            }
        }
        
        return super.hitTest(point, with: event)
    }
    
    public override func layoutSubviews() {
        super.layoutSubviews()
        
        // udpate the width of textField
        handleTextField(self.textField)
    }
}

// MARK: - InnerTextFieldDelegate
extension ScrollableTextField: InnerTextFieldDelegate {
    func textDidSet() {
        handleTextField(textField)
    }
    
    func placeholderDidSet() {
        handleTextFieldPlaceholder(textField)
    }
}

// MARK: - UIScrollViewDelegate
extension ScrollableTextField: UIScrollViewDelegate {
    public func scrollViewDidScroll(_ scrollView: UIScrollView) {
        if scrollView.contentOffset.x < 0 {
            scrollView.contentOffset.x = 0
            return
        }
        
        let maxOffsetX = scrollView.contentSize.width - scrollView.bounds.width
        
        if maxOffsetX < 0 {
            return
        }
        
        if scrollView.contentOffset.x > maxOffsetX {
            scrollView.contentOffset.x = maxOffsetX
        }
    }
}

// MARK: - UIGestureRecognizerDelegate
extension ScrollableTextField: UIGestureRecognizerDelegate {
    public override func gestureRecognizerShouldBegin(_ gestureRecognizer: UIGestureRecognizer) -> Bool {
        if gestureRecognizer == self.tapGesture {
            if self.textField?.isFirstResponder ?? false {
                return true
            } else {
                return false
            }
        }
        return super.gestureRecognizerShouldBegin(gestureRecognizer)
    }
}

// MARK: - InnerTextField
class InnerTextField: UITextField {
    
    // MARK: - Public
    weak var innerDelegate: InnerTextFieldDelegate?
    
    func getWidthFromDocumentBeginingToCursor() -> CGFloat? {
        guard let selectedRange = self.selectedTextRange else {
            return nil
        }

        let width = getWidthFromDocumentBegining(to: selectedRange.start)

        return width
    }

    func getWidthFromDocumentBeginingToEnd() -> CGFloat? {
        guard let str = self.text else {
            return nil
        }

        let width = getWidthFrom(string: str)

        return width
    }

    // MARK: - Private
    private func changeType(oldRange: UITextRange, newRange: UITextRange) -> TextRangeChangedType {
        let oldStart = self.offset(from: beginningOfDocument, to: oldRange.start)
        let oldEnd = self.offset(from: beginningOfDocument, to: oldRange.end)

        let newStart = self.offset(from: beginningOfDocument, to: newRange.start)
        let newEnd = self.offset(from: beginningOfDocument, to: newRange.end)

        if (oldStart == newStart) && (oldEnd != newEnd) {
            if (newEnd > oldEnd) {
                return .rightAndForward
            } else if (newEnd < oldEnd) {
                return .rightAndBack
            }
            return .none
        }
        if (oldStart != newStart) && (oldEnd == newEnd) {
            if (newStart < oldStart) {
                return .leftAndBack
            } else if (newStart > oldStart) {
                return .leftAndForward
            }
            return .none
        }
        if (oldStart == oldEnd) && (newStart == newEnd) {
            if newStart > oldStart {
                return .rightAndForward
            } else if newStart < oldStart {
                return .leftAndBack
            }
        }
        return .none
    }

     func getWidthFrom(string text: String) -> CGFloat {
        let defaultFont = self.font ?? UIFont.systemFont(ofSize: 15)
        let exWidth = text.expectedWidth(withHeight: self.bounds.height, font: defaultFont)
        return exWidth
    }

    private func getWidthFromDocumentBegining(to position: UITextPosition) -> CGFloat? {
        if let textStr = self.text {
            let curText = textStr as NSString
            let offset = self.offset(from: beginningOfDocument, to: position)

            guard offset <= curText.length && offset >= 0 else {
                return nil
            }
            let subStr = curText.substring(to: offset)

            let width = getWidthFrom(string: subStr)
            return width
        }
        return nil
    }

    override var text: String? {
        didSet {
            self.innerDelegate?.textDidSet()
        }
    }
    
    override var placeholder: String? {
        didSet {
            self.innerDelegate?.placeholderDidSet()
        }
    }

    func setAttributedTextAndUpdateUI(_ text: NSAttributedString) {
        self.attributedText = text
        self.innerDelegate?.textDidSet()
    }
    
    override var selectedTextRange: UITextRange? {
        willSet {
            if let old = selectedTextRange, let `new` = newValue {
                let willChangeType = changeType(oldRange: old, newRange: new)
                if willChangeType == .leftAndBack || willChangeType == .leftAndForward {
                    if let width = getWidthFromDocumentBegining(to: new.start) {
                        selectedTextRangeChangedBlock?(willChangeType, width)
                    }
                } else if willChangeType == .rightAndForward || willChangeType == .rightAndBack {
                    if let width = getWidthFromDocumentBegining(to: new.end) {
                        selectedTextRangeChangedBlock?(willChangeType, width)
                    }
                }
            }
        }
    }

    var selectedTextRangeChangedBlock: ((_ changType: TextRangeChangedType, _ beforeTextWidth: CGFloat) -> ())?
}

extension String {
    func expectedHeight(withWidth width: CGFloat, font: UIFont) -> CGFloat {
        let constraintRect = CGSize(width: width, height: .greatestFiniteMagnitude)
        let boundingBox = self.boundingRect(with: constraintRect, options: [.usesFontLeading, .usesLineFragmentOrigin], attributes: [NSAttributedString.Key.font: font], context: nil)
        
        return boundingBox.height
    }
    
    func expectedWidth(withHeight height: CGFloat, font: UIFont) -> CGFloat {
        let constraintRect = CGSize(width: .greatestFiniteMagnitude, height: height)
        let boundingBox = self.boundingRect(with: constraintRect, options: [.usesFontLeading, .usesLineFragmentOrigin], attributes: [NSAttributedString.Key.font: font], context: nil)
        
        return boundingBox.width
    }
    
    func size(withFont font: UIFont) -> CGSize {
        let string = NSString(string: self)
        let size = string.size(withAttributes: [NSAttributedString.Key.font: font])
        return size
    }
}
