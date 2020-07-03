//
//  UnlockInfo.swift
//  flutterwitsystem
//
//  Created by yyjjls on 2020/7/3.
//开锁回调

import Foundation

protocol UnlockInfo {
    
     /**
        * 成功的回调
        *
        * @param deviceId
        * @param code
        */
    func success(deviceId:String, code:Int);

       /**
        * 失败的回调
        *
        * @param error
        * @param code
        */
    func fail(error:String, code:Int);

       /**
        * 电量的回调
        *
        * @param b
        */
    func battery(deviceId:String,b:Int);
    
    
    /**
     *  发现附近有多个设备的回调 只有转开启附近设备的时候才有可能回调¬
     */
    func dDevice(deviceId:String);
    
    
    
}
