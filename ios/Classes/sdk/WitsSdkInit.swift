//
//  WitsSdkInit.swift
//  flutterwitsystem
//
//  Created by yyjjls on 2020/4/30.
//

import Foundation

class WitsSdkInit: WitsSdk,Register {
    
    private init() {}
    
    public static let getInstance = WitsSdkInit()
    
//    private  var witsSdkInit:WitsSdkInit;
//
//
//    public static func getInstance()->Register{
//        if(witsSdkInit==nil){
//            witsSdkInit=WitsSdkInit();
//        }
//     return witsSdkInit;
//    }
  
   
    
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
