//
//  ServerModels.swift
//  btccurrency
//
//  Created by admin on 2023/7/4.
//

import UIKit
import MJExtension

@objcMembers
class UserModel: NSObject {
    var avatar = ""
    var created: TimeInterval = Date().timeIntervalSince1970
    var pinChance: Int = 0
    var point: Int = 0
    var uid = ""
    var user_name = ""
    
    override class func mj_replacedKeyFromPropertyName() -> [AnyHashable : Any]! {
        return ["pinChance": "pin_chance", "userName": "user_name"]
    }
}

@objcMembers
class PinListItem: NSObject {
    var pinId = ""
    var pinReward = ""
    var pinRewardDesc = ""
    var pinPic = ""
    override class func mj_replacedKeyFromPropertyName() -> [AnyHashable : Any]! {
        return ["pinId": "pin_id",
                "pinReward": "pin_reward",
                "pinRewardDesc": "pin_reward_desc",
                "pinPic": "pin_pic"
        ]
    }
}

@objcMembers
class QuizAnswerListItem: NSObject {
    var id: String = ""
    var options: [Int] = [Int]()
    
    convenience init(id: String, options: [Int]) {
        self.init()
        self.id = id
        self.options = options
    }
}

enum kQuestionType: Int {
    case single
    case multi
}

@objcMembers
class QuizSonQuestionItem: NSObject {
    var id: String = ""
    var options = [String]()
    var qType: Int = 0
    var title = ""
    
    var questionType: kQuestionType {
        if qType == 1 {
            return .single
        } else {
            return .multi
        }
    }
    
    override class func mj_replacedKeyFromPropertyName() -> [AnyHashable : Any]! {
        return ["qType": "q_type"]
    }
}

@objcMembers
class QuizPageQuestionItem: NSObject {
    var sonQuestion = NSArray()
    var title = ""
    
    override class func mj_replacedKeyFromPropertyName() -> [AnyHashable : Any]! {
        return ["sonQuestion": "son_question"]
    }
    
    override class func mj_objectClassInArray() -> [AnyHashable : Any]! {
        return ["sonQuestion": QuizSonQuestionItem.self]
    }
}

@objcMembers
class RedeemHistoryItem: NSObject {
    var status: Int = 0
    var time: TimeInterval = Date().timeIntervalSince1970
    var title: String = ""
}

@objcMembers
class RedeemListItem: NSObject {
    var id = ""
    var pic = ""
    var ico = ""
    var reward: Int = 0
    var symbol = ""
    var total: Int = 0
    var left: Int = 0
    var pointRequire: Int = 0
    override class func mj_replacedKeyFromPropertyName() -> [AnyHashable : Any]! {
        return ["pointRequire": "point_require"]
    }
}

@objcMembers
class PointListItem: NSObject {
    var title = ""
    var pointChange = ""
    var time: TimeInterval = Date().timeIntervalSince1970
    
    override class func mj_replacedKeyFromPropertyName() -> [AnyHashable : Any]! {
        return ["pointChange": "point_change"]
    }
}

@objcMembers
class TaskListItem: NSObject {
    var taskId = ""
    var taskName = ""
    var spinTimes: Int = 0
    var taskType = ""
    var params = ""
    var done: Bool = false
    
    override class func mj_replacedKeyFromPropertyName() -> [AnyHashable : Any]! {
        return ["taskId": "task_id",
                "taskName": "task_name",
                "spinTimes": "spin_times",
                "taskType": "task_type"
        ]
    }
}

@objcMembers
class ResultBase: NSObject {
    var code: Int = 0
    var msg = ""
    
    var isSuccess: Bool {
        get {
            return code == 10000 // success
        }
    }
}

@objcMembers
class ResultPinCheck: ResultBase {
    var data = PinListItem()
}

@objcMembers
class ResultPinList: ResultBase {
    var list = NSArray()
    
    override class func mj_replacedKeyFromPropertyName() -> [AnyHashable : Any]! {
        return ["list": "data.lists"]
    }
    override class func mj_objectClassInArray() -> [AnyHashable : Any]! {
        return ["list": PinListItem.self]
    }
}

@objcMembers
class ResultQuizQuestions: ResultBase {
    var list = NSArray()
    
    override class func mj_replacedKeyFromPropertyName() -> [AnyHashable : Any]! {
        return ["list": "data.lists"]
    }
    
    override class func mj_objectClassInArray() -> [AnyHashable : Any]! {
        return ["list": QuizPageQuestionItem.self]
    }
}

@objcMembers
class ResultQuizSubmit: ResultBase {}

@objcMembers
class ResultRedeemHistory: ResultBase {
    var totalNum: Int = 0
    var list = NSArray()
    
    override class func mj_replacedKeyFromPropertyName() -> [AnyHashable : Any]! {
        return ["totalNum": "data.total_num", "list": "data.list"]
    }
    override class func mj_objectClassInArray() -> [AnyHashable : Any]! {
        return ["list": RedeemHistoryItem.self]
    }
}

@objcMembers
class ResultRedeemList: ResultBase {
    public var list = NSArray()
    
    override class func mj_replacedKeyFromPropertyName() -> [AnyHashable : Any]! {
        return ["list": "data.list"]
    }
    override class func mj_objectClassInArray() -> [AnyHashable : Any]! {
        return ["list": RedeemListItem.self]
    }
}

@objcMembers
class ResultPointList: ResultBase {
    var list = NSArray()
    var totalPoints: Int = 0
    var totalNum: Int = 0
    
    override class func mj_replacedKeyFromPropertyName() -> [AnyHashable : Any]! {
        return ["totalPoints": "data.total_points",
                "totalNum": "data.total_num",
                "list": "data.list"]
    }
    
    override class func mj_objectClassInArray() -> [AnyHashable : Any]! {
        return ["list": PointListItem.self]
    }
}

@objcMembers
class ResultRedeemRedeem: ResultBase {}

@objcMembers
class ResultTaskCheck: ResultBase {}

@objcMembers
class ResultTaskList: ResultBase {
    var expireTime: TimeInterval = Date().timeIntervalSince1970
    var pinNum: Int = 0
    var list = NSArray()
    
    override class func mj_replacedKeyFromPropertyName() -> [AnyHashable : Any]! {
        return ["expireTime": "data.expire_time",
                "pinNum": "data.pin_num",
                "list": "data.lists"]
    }
    
    override class func mj_objectClassInArray() -> [AnyHashable : Any]! {
        return ["list": TaskListItem.self]
    }
}

@objcMembers
class ResultUserProfile: ResultBase {
    var data = UserModel()
}
