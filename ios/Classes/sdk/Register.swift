//
//  Register.swift
//  flutterwitsystem
//注册sdk的接口
//  Created by yyjjls on 2020/4/30.
//

import Foundation

public protocol Register : NSObject{


    func witsSdkInit(appId: String?, token: String?)->WitsSdk?;
    
}
