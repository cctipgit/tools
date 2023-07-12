//
//  UserModel.swift
//  btccurrency
//
//  Created by admin on 2023/7/4.
//

import UIKit
import MJExtension

@objcMembers
class LocalUserModel: NSObject {
    var userName: String = ""
    var avatarImgName: String = ""
}

@objcMembers
class LocalTaskModel: NSObject {
    var addTokenCount: Int = 0
    var viewDetailCount: Int = 0
    var quizDoneTime: String = "" // customJoinTime
    var redeemCount: Int = 0
    var pin1012Count: Int = 0
    var pin1416Count: Int = 0
    var inviteFriendCount: Int = 0
}
