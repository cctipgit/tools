//
//  SwitchCurrencyViewModel.swift
//  btccurrency
//
//  Created by fd on 2022/10/29.
//

import Foundation
import RxDataSources
import RxRelay
import RxSwift

class SwitchCurrencyViewModel: NSObject {
    struct Input {
        var searchTrigger: Observable<String>
        var refreshTrigger: Observable<Void>
    }

    struct Output {
        var datasource: BehaviorRelay<[SwitchCurrencyGroup]>
        var searchDatasource: BehaviorRelay<[SwitchCurrencyGroup]>
    }

    var datasource: BehaviorRelay<[SwitchCurrencyGroup]>

    override init() {
        datasource = BehaviorRelay(value: [])
        super.init()
    }

    func transform(input: Input) -> Output {
        let searchResult = BehaviorRelay<[SwitchCurrencyGroup]>(value: [])
        input.searchTrigger.map { text -> [SwitchCurrencyGroup] in
            let values = self.datasource.value
            var searchResult: [SwitchCurrencyCellViewModel] = []
            let searchText = text.uppercased().trimmed
            for section in values {
                if section.sectionType != .fixed {
                    continue
                }
                for item in section.items {
                    if item.title.contains(text)
                        || item.response.token.contains(searchText) {
                        searchResult.append(item)
                    }
                }
            }
            return [SwitchCurrencyGroup(header: "",
                                        items: searchResult,
                                        sectionType: .fixed)]
        }
        .bind(to: searchResult)
        .disposed(by: rx.disposeBag)

        input.refreshTrigger
            .take(1)
            .delay(.milliseconds(100), scheduler: MainScheduler())
            .subscribe(onNext: { [weak self] in
                self?.datasource.accept(CurrencyTokensList.shared.datasource.value)
            })
            .disposed(by: rx.disposeBag)
        return Output(datasource: datasource,
                      searchDatasource: searchResult)
    }
}
