//
//  ToolWebModule.swift
//  cryptovise
//
//  Created by admin on 2023/7/21.
//

import UIKit

@objc(ToolWebModule)
class ToolWebModule: RCTViewManager {
    override func view() -> UIView! {
        return ToolWebView(frame: .zero)
    }
}
