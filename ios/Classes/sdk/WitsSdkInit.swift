//
//  WitsSdkInit.swift
//  flutterwitsystem
//
//  Created by yyjjls on 2020/4/30.
//

import Foundation

class WitsSdkInit: WitsSdk,Register {
    
    private init() {}
    
    static let getInstance = WitsSdkInit()
    
    
    
    func getBleLockDevice()->String{
        return "";
    }
       
    func getInduceUnlock()->String{
        return "";
    }
       
    
    func witsSdkInit()->Bool{
        
        return false;
    }
    
}
