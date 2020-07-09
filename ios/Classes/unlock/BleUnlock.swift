//
//  BleUnlock.swift
//  flutterwitsystem
//
//  Created by yyjjls on 2020/7/3.



import Foundation

protocol BleUnlock {

    /**
   * 开启附近的设备
   */
        func unlock()-> Bool;

    /**
     * 开启指定的设备
     *
     * @param deviceId
     */
    func unlock( deviceId:String)-> Bool;


    /**
     * 添加回调
     */
    func addCallBack( unlockInfo:UnlockInfo);



}
