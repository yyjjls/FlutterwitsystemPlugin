//
//  WitsSdkInit.swift
//  flutterwitsystem
//
//  Created by yyjjls on 2020/4/30.
//

import Foundation

class WitsSdkInit: WitsSdk,Register {
    private var ble:Ble;
    
    private init() {
        ble=Ble.getInstance;
    }
    
    public static let getInstance = WitsSdkInit()
    
    
   public func witsSdkInit()->WitsSdk{
           
           return self;
       }
    
    func getBleLockDevice()->String{
        return "";
    }
       
    func getInduceUnlock()->Induce{
        return Induce.getInstance;
    }
       
    
   
    
}
