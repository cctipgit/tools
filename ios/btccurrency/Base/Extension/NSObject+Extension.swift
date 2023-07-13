//
//  NSObject+Extension.swift
//  btccurrency
//
//  Created by admin on 2023/7/3.
//

import UIKit
import Foundation
import KeychainSwift

extension NSObject {
    func randomUserName() -> String {
        let names = ["Emma", "Liam", "Olivia", "Noah", "Ava", "Isabella", "Sophia", "Mia", "Charlotte", "Amelia", "Harper", "Evelyn", "Abigail", "Emily", "Lily", "Grace", "Madison", "Scarlett", "Elizabeth", "Henry", "James", "Benjamin", "Alexander", "Sebastian", "Carter", "Matthew", "Samuel", "Joseph", "David", "Michael", "Daniel", "Andrew", "Lucas", "Logan", "William", "Gabriel", "Julian", "Joshua", "Elijah", "Lucy", "Oliver", "Owen", "Jackson", "Aiden", "Ethan", "Jacob", "William", "Nicholas", "Hudson", "Lincoln", "Wyatt", "Jonathan", "Theodore", "Luke", "Nathan", "Zachary", "Isaac", "Caleb", "Ezra", "Cameron", "Aaron", "Anthony", "Gavin", "Hunter", "Leonardo", "Elias", "Max", "Arthur", "Sam", "Thomas", "Connor", "Cooper", "David", "Dominic", "Isaiah", "Jack", "Jaxon", "Adam", "Adrian", "Charles", "Evan", "Ezekiel", "Jace", "Jaden", "Jasper", "Joel", "Nolan", "Peter", "Simon", "Tristan", "Xavier", "Rebecca", "Sophie", "Aria", "Chloe", "Eleanor", "Hannah", "Isabelle", "Ivy", "Mia", "Nora", "Victoria", "Zoe", "Audrey", "Bella", "Claire", "Emma", "Grace", "Hazel", "Lily", "Lucy", "Naomi", "Penelope", "Ruby", "Stella", "Violet", "Aiden", "Benjamin", "Caleb", "Daniel", "Elijah", "Henry", "Isaac", "Liam", "Matthew", "Nicholas", "Oliver", "Samuel", "Steven", "Thomas", "William", "Wyatt", "Zachary"]
        let randomNum = Int(arc4random_uniform(UInt32(names.count - 1)))
        return names[randomNum]
    }
    
    func randomAvatar() -> String {
        let randomNum = Int(arc4random_uniform(8))
        return "n_avator-" + "\(randomNum)"
    }
    
    func userId() -> String {
        if let userId = KeychainSwift().get(AppConfig.kUserIdKeychain), userId.count > 0 {
            return userId
        }
        let newUserId = UUID().uuidString
        KeychainSwift().set(newUserId, forKey: AppConfig.kUserIdKeychain, withAccess: AppConfig.keychainAccess)
        return newUserId
    }
    
    func getTaskInfo() -> LocalTaskModel {
        if let jsonStr = UserDefaults.standard.object(forKey: AppConfig.kUserDefaultTaskKey) {
            if let task = LocalTaskModel.mj_object(withKeyValues: jsonStr) {
                return task
            }
        }
        return LocalTaskModel()
    }
    
    func getUserInfo() -> LocalUserModel {
        if let jsonStr = UserDefaults.standard.object(forKey: AppConfig.kUserDefaultUserInfoKey) {
            if let user = LocalUserModel.mj_object(withKeyValues: jsonStr) {
                return user
            }
        }
        let newModel = LocalUserModel()
        newModel.userName = self.randomUserName()
        newModel.avatarImgName = self.randomAvatar()
        self.setUserInfo(model: newModel)
        return newModel
    }
    
    @discardableResult
    func setUserInfo(model: LocalUserModel) -> Bool {
        let josnStr = model.mj_JSONString()
        UserDefaults.standard.set(josnStr, forKey: AppConfig.kUserDefaultUserInfoKey)
        UserDefaults.standard.synchronize()
        return true
    }
    
    @discardableResult
    func setLocalTaskModel(model: LocalTaskModel) -> Bool {
        let josnStr = model.mj_JSONString()
        UserDefaults.standard.set(josnStr, forKey: AppConfig.kUserDefaultTaskKey)
        UserDefaults.standard.synchronize()
        return true
    }

    // Print logs
    func printLog<T>(message: T, file: String = #file, method: String = #function, line: Int = #line) {
        #if (DEBUG)
        print("\((file as NSString).lastPathComponent)[\(line)], \(method): \(message)")
        #endif
    }
}
