//
//  QuestionsViewController.swift
//  btccurrency
//
//  Created by admin on 2023/7/6.
//

import UIKit
import SnapKit
import MJRefresh
import RxSwift
import RxCocoa

class QuestionsViewController: YBaseViewController {
    private let bgView = UIView().then { v in
        v.backgroundColor = .white
        v.layer.cornerRadius = 16
        v.layer.shadowColor = UIColor.basicBlk.cgColor
        v.layer.shadowOpacity = 0.05
        v.layer.shadowRadius = 5
        v.layer.shadowOffset = CGSize(width: 0, height: 3)
    }
    
    private let progressTitleLabel = UILabel().then { v in
        v.font = .robotoBold(with: 14)
        v.textColor = .basicBlk
    }
    private let progressDescLabel = UILabel().then { v in
        v.font = .robotoLight(with: 14)
        v.textColor = .contentDisabled
    }
    private let progressFullView = UIView().then { v in
        v.backgroundColor = .bgSecondary
        v.layer.cornerRadius = 5
    }
    private let progressCurrentView = UIView().then { v in
        v.backgroundColor = .primaryBranding
        v.layer.cornerRadius = 5
    }
    
    private var scrollView: UIScrollView!
    
    private let fullNextBtn = UIButton(type: .custom).then { v in
        v.layer.cornerRadius = 8
        v.backgroundColor = .primaryBranding
        v.setTitleColor(.white, for: .normal)
        v.titleLabel?.font = .robotoRegular(with: 16)
        v.setTitle("Next".localized(), for: .normal)
        v.isHidden = true
    }
    private let backBtn = UIButton(type: .custom).then { v in
        v.layer.cornerRadius = 8
        v.backgroundColor = .bgDisabled
        v.setTitleColor(.contentDisabled, for: .normal)
        v.titleLabel?.font = .robotoRegular(with: 16)
        v.setTitle("Back".localized(), for: .normal)
        v.isHidden = true
    }
    private let nextBtn = UIButton(type: .custom).then { v in
        v.layer.cornerRadius = 8
        v.backgroundColor = .primaryBranding
        v.setTitleColor(.white, for: .normal)
        v.titleLabel?.font = .robotoRegular(with: 16)
        v.setTitle("Next".localized(), for: .normal)
        v.isHidden = true
    }
    
    private var progress: CGFloat = 0
    var sWidth: CGFloat {
        get {
            return UIDevice.kScreenWidth() - 40
        }
    }
    var sHeight: CGFloat {
        get {
            return UIDevice.kScreenHeight() - UIDevice.kStatusBarHeight() - 60 - 48 - 34 - 24 - UIDevice.kSafeBottomHeight() - 30 - 24 - 50 - 16
        }
    }
    
    var tableData = [QuizPageQuestionItem]()
    
    var currentIndexRelay: BehaviorRelay<Int> = .init(value: 1)
    var currentIndex: Int = 0
    var totalCount: Int = 0
    
    var submitData: [String: QuizAnswerListItem] = [String: QuizAnswerListItem]()
    
    // MARK: Super Method
    override func viewDidLoad() {
        super.viewDidLoad()
        p_setElements()
        updateViewConstraints()
    }
    
    override func updateViewConstraints() {
        super.updateViewConstraints()
        bgView.snp.makeConstraints { make in
            make.left.equalToSuperview().offset(20)
            make.right.equalToSuperview().offset(-20)
            make.top.equalTo(navigationView.snp.bottom).offset(16)
            make.bottom.equalTo(view.safeAreaLayoutGuide.snp.bottom).offset(-30)
        }
        
        progressTitleLabel.snp.makeConstraints { make in
            make.height.equalTo(16)
            make.top.equalToSuperview().offset(32)
            make.left.equalToSuperview().offset(16)
            make.width.greaterThanOrEqualTo(40)
        }
        progressDescLabel.snp.makeConstraints { make in
            make.height.centerY.equalTo(progressTitleLabel)
            make.width.greaterThanOrEqualTo(20)
            make.left.equalTo(progressTitleLabel.snp.right).offset(2)
        }
        progressFullView.snp.makeConstraints { make in
            make.left.equalTo(progressTitleLabel)
            make.height.equalTo(10)
            make.top.equalTo(progressTitleLabel.snp.bottom).offset(8)
            make.right.equalToSuperview().offset(-16)
        }
        progressCurrentView.snp.makeConstraints { make in
            make.left.height.top.equalTo(progressFullView)
            make.width.equalTo(progressFullView).multipliedBy(0)
        }
        
        scrollView.snp.makeConstraints { make in
            make.width.equalTo(sWidth)
            make.height.equalTo(sHeight)
            make.top.equalTo(progressFullView.snp.bottom).offset(24)
            make.left.equalToSuperview()
        }
        
        fullNextBtn.snp.makeConstraints { make in
            make.height.equalTo(50)
            make.bottom.equalToSuperview().offset(-24)
            make.left.equalToSuperview().offset(16)
            make.right.equalToSuperview().offset(-16)
        }
        backBtn.snp.makeConstraints { make in
            make.height.equalTo(fullNextBtn)
            make.left.equalToSuperview().offset(16)
            make.bottom.equalTo(fullNextBtn)
            make.width.equalTo((UIDevice.kScreenWidth() - 8.0 -  72.0) / 2.0)
        }
        nextBtn.snp.makeConstraints { make in
            make.width.height.bottom.equalTo(backBtn)
            make.left.equalTo(backBtn.snp.right).offset(8)
        }
    }
    
    // MARK: Private Method
    private func p_setElements() {
        navigationView.backMode = .none
        navigationView.pinMode = .none
        navigationView.titleMode = .center
        navigationView.titleLabel.text = "Questionnaire".localized()
        scrollView = UIScrollView(frame: CGRect(x: 0, y: 0, width: sWidth, height: sHeight))
        scrollView.showsVerticalScrollIndicator = false
        scrollView.delegate = self
        scrollView.tag = 1
        scrollView.showsHorizontalScrollIndicator = false
        scrollView.isPagingEnabled = true
        bgView.addSubviews([progressTitleLabel,
                            progressDescLabel,
                            progressFullView,
                            progressCurrentView,
                            scrollView,
                            fullNextBtn,
                            backBtn,
                            nextBtn
                           ])
        view.addSubview(bgView)
        currentIndexRelay.asObservable()
            .distinctUntilChanged()
            .subscribe(onNext: { [weak self] val in
            guard let self = self else { return }
            let showIndex: Int = self.totalCount > 0 ? (val + 1) : 0
            self.progressTitleLabel.text = "Question".localized() + " \(showIndex)"
            
            self.progressCurrentView.snp.remakeConstraints { make in
                make.left.height.top.equalTo(self.progressFullView)
                make.width.equalTo(self.progressFullView).multipliedBy(CGFloat(NSString(format: "%.2f", CGFloat(showIndex) / CGFloat(self.totalCount)).floatValue))
            }
            UIView.animate(withDuration: 0.3) {
                self.view.layoutIfNeeded()
            }
            if val == 0 {
                self.fullNextBtn.isHidden = false
                self.backBtn.isHidden = true
                self.nextBtn.isHidden = true
                if showIndex == self.totalCount {
                    self.fullNextBtn.setTitle("Done".localized(), for: .normal)
                } else {
                    self.fullNextBtn.setTitle("Next".localized(), for: .normal)
                }
            } else {
                self.fullNextBtn.isHidden = true
                self.backBtn.isHidden = false
                self.nextBtn.isHidden = false
                if showIndex == self.totalCount {
                    self.nextBtn.setTitle("Done".localized(), for: .normal)
                } else {
                    self.nextBtn.setTitle("Next".localized(), for: .normal)
                }
            }
        })
        .disposed(by: rx.disposeBag)
        fullNextBtn.rx.tap
            .throttle(.seconds(1), scheduler: MainScheduler())
            .subscribe(onNext: { [weak self] _ in
            guard let self = self else { return }
            self.currentIndex += 1
            self.scrollView.setContentOffset(CGPoint(x: CGFloat((self.currentIndex)) * sWidth, y: 0), animated: true)
            self.currentIndexRelay.accept(self.currentIndex)
        }).disposed(by: rx.disposeBag)
        backBtn.rx.tap
            .throttle(.seconds(1), scheduler: MainScheduler())
            .subscribe(onNext: { [weak self] _ in
            guard let self = self else { return }
            self.currentIndex -= 1
            self.scrollView.setContentOffset(CGPoint(x: CGFloat((self.currentIndex)) * sWidth, y: 0), animated: true)
            self.currentIndexRelay.accept(self.currentIndex)
        })
        .disposed(by: rx.disposeBag)
        nextBtn.rx.tap
            .throttle(.seconds(1), scheduler: MainScheduler())
            .subscribe(onNext: { [weak self] _ in
            guard let self = self else { return }
            if (self.currentIndex + 1) == self.totalCount {
                let answers = self.submitData.map({ $0.value })
                SmartService().quizSubmit(answers: answers)
                    .subscribe(onNext: { [weak self] result in
                        guard let res = result, res.isSuccess, let self = self else {
                            self?.showAlert(message: result?.msg ?? "")
                            return
                        }
                        self.showAlert(message: result?.msg ?? "")
                        self.navigationController?.popViewController(animated: true)
                })
                .disposed(by: rx.disposeBag)
            } else {
                self.currentIndex += 1
                self.scrollView.setContentOffset(CGPoint(x: CGFloat((self.currentIndex)) * sWidth, y: 0), animated: true)
                self.currentIndexRelay.accept(self.currentIndex)
            }
        })
        .disposed(by: rx.disposeBag)
        p_refresh()
    }
    
    private func p_refresh() {
        SmartService().quizQuestions()
            .retry()
            .subscribe(onNext: { [weak self] result in
                guard let self = self, let res = result else { return }
                guard let listArray = res.list as? [QuizPageQuestionItem] else {
                    return
                }
                let contentWidth: CGFloat = self.sWidth * CGFloat(listArray.count)
                listArray.enumerated().forEach { item in
                    let table = UITableView(frame: CGRect(x: CGFloat(item.offset) * self.sWidth, y: 0, width: self.sWidth, height: self.sHeight), style: .plain)
                    table.tag = item.offset
                    table.separatorStyle = .none
                    table.delegate = self
                    table.dataSource = self
                    table.showsHorizontalScrollIndicator = false
                    table.showsVerticalScrollIndicator = false
                    self.scrollView.addSubview(table)
                }
                self.tableData = listArray
                scrollView.contentSize = CGSize(width: contentWidth, height: self.sHeight)
                self.totalCount = listArray.count
                if listArray.count > 0 {
                    self.currentIndexRelay.accept(0)
                }
                self.progressDescLabel.text = "out of".localized() + " \(listArray.count)"
            })
            .disposed(by: rx.disposeBag)
    }
}

extension QuestionsViewController: UIScrollViewDelegate {
    func scrollViewDidEndDecelerating(_ scrollView: UIScrollView) {
        if scrollView.tag == 1 {
            self.currentIndex = Int(floor(scrollView.contentOffset.x / sWidth))
            self.currentIndexRelay.accept(currentIndex)
        }
    }
}

extension QuestionsViewController: UITableViewDelegate, UITableViewDataSource {
    
    func tableView(_ tableView: UITableView, heightForHeaderInSection section: Int) -> CGFloat {
        return 80
    }
    
    func tableView(_ tableView: UITableView, viewForHeaderInSection section: Int) -> UIView? {
        let bgView = UIView(frame: CGRect(x: 0, y: 0, width: UIDevice.kScreenWidth() - 72, height: 80)).then { v in
            v.backgroundColor = .white
        }
        let label = UILabel(frame: CGRect(x: 16, y: 0, width: UIDevice.kScreenWidth() - 72, height: 40)).then { v in
            v.font = .robotoBold(with: 32)
        }
        label.text = self.tableData[tableView.tag].title
        bgView.addSubview(label)
        return bgView
    }
    
    func numberOfSections(in tableView: UITableView) -> Int {
        return 1
    }

    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        let index = tableView.tag
        guard tableData.count > index else { return 0 }
        return tableData[index].sonQuestion.count
    }

    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        guard tableData.count > tableView.tag else { return UITableViewCell(frame: .zero) }
        let data = tableData[tableView.tag]
        var cellIdentifier: String!
        var cell: UITableViewCell!
        if data.sonQuestion.count > 1 {
            cellIdentifier = "questionMultiCellID"
            cell = tableView.dequeueReusableCell(withIdentifier: cellIdentifier)
            if cell == nil {
                cell = QuestionMultiCell.init(style: .default, reuseIdentifier: cellIdentifier)
            }
            if let mCell = cell as? QuestionMultiCell {
                let sonItem = (data.sonQuestion[indexPath.row] as? QuizSonQuestionItem) ?? QuizSonQuestionItem()
                mCell.setData(item: sonItem)
                mCell.delegate = self
                submitData[sonItem.id] = QuizAnswerListItem(id: sonItem.id, options: [0])
            }
        } else {
            cellIdentifier = "questionSimpleCellID"
            cell = tableView.dequeueReusableCell(withIdentifier: cellIdentifier)
            if cell == nil {
                cell = QuestionSimpleCell.init(style: .default, reuseIdentifier: cellIdentifier)
            }
            if let mCell = cell as? QuestionSimpleCell {
                let sonItem = (data.sonQuestion[indexPath.row] as? QuizSonQuestionItem) ?? QuizSonQuestionItem()
                mCell.setData(item: sonItem)
                mCell.delegate = self
                submitData[sonItem.id] = QuizAnswerListItem(id: sonItem.id, options: [0])
            }
        }
        cell.selectionStyle = .none
        return cell
    }
    
    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        var height: CGFloat = 0
        guard tableData.count > tableView.tag else {
            return height
        }
        let data = tableData[tableView.tag]
        if data.sonQuestion.count > 1 {
            let item = (data.sonQuestion[indexPath.row] as? QuizSonQuestionItem) ?? QuizSonQuestionItem()
            var height: CGFloat = 36
            var itemTotalWidth: CGFloat = 0
            let width = UIDevice.kScreenWidth() - 72.0
            item.options.forEach { str in
                itemTotalWidth += (QuestionMultiCell.calculateItemSize(str: str).width + 8)
            }
            let intVal: Int = Int(itemTotalWidth / width)
            height += CGFloat(intVal) * 48
            if (itemTotalWidth / width) > CGFloat(intVal) {
                height += 48
            }
            return height + 24
        } else {
            height = 36 + CGFloat((data.sonQuestion.firstObject as? QuizSonQuestionItem)?.options.count ?? 0) * 56
        }
        return height
    }
}

extension QuestionsViewController: QuestionCellDelegate {
    func didSelectIndexChange(qID: String, selected: Set<Int>) {
        self.submitData[qID] = QuizAnswerListItem(id: qID, options: Array(selected))
    }
}
