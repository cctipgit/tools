//
//  UIScrollView+Extension.swift
//  cryptovise
//
//  Created by admin on 2023/6/29.
//

import Foundation
import MJRefresh

extension UIScrollView {
    func configMJHeader(refreshingBlock:@escaping () -> Void) -> MJRefreshNormalHeader {
        let header =  MJRefreshNormalHeader.init(refreshingBlock: {
            refreshingBlock()
        })
        header.lastUpdatedTimeLabel?.isHidden = false
        header.setTitle("pull to refresh".localized(), for: .idle)
        header.setTitle("refresh now".localized(), for: .pulling)
        header.setTitle("will load".localized(), for: .willRefresh)
        header.setTitle("loading...".localized(), for: .refreshing)
        self.mj_header = header
        return header
    }
    
    func configMJFooter(refreshingBlock:@escaping () -> Void) -> MJRefreshAutoNormalFooter {
        let footer =  MJRefreshAutoNormalFooter.init(refreshingBlock: {
            refreshingBlock()
        })
        footer.stateLabel?.font = .robotoRegular(with: 12)
        footer.stateLabel?.textColor = .basicBlk
        footer.setTitle("load more", for: .idle)
        footer.setTitle("loading more".localized(), for: .pulling)
        footer.setTitle("will load".localized(), for: .willRefresh)
        footer.setTitle("loading...".localized(), for: .refreshing)
        footer.setTitle("--  bottom  --", for: MJRefreshState.noMoreData)
        self.mj_footer = footer
        return footer
    }
}
