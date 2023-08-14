//
//  ToolWebModule.swift
//  cryptovise
//
//  Created by admin on 2023/7/21.
//

import UIKit

@objc(ToolWebModule)
class ToolWebModule: RCTViewManager {
    var toolWebView: ToolWebView?
    
    override func view() -> UIView! {
        if toolWebView == nil {
            toolWebView = ToolWebView(frame: .zero)
        }
        return toolWebView
    }
}
