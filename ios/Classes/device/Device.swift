//
//  Device.swift
//  flutterwitsystem
//
//  Created by yyjjls on 2020/7/3.
//设备管理中

import Foundation

protocol Device {
    
        //从服务器获取设备
       func getNetWorkDevice()->Bool;

       //获取缓存设备，在没有网络的时候获取
       func getCacheDevice()->Bool;

       //设备ID获得该设备的信息
       func getDevice<T>(deviceId:String)->T;

       //获得所有的设备
       func getDevices<T>()->T;

       //获得设备的个数
       func getDevicesNumber()->Int;

        //三方获得设备信息
       func getThreeDevices<T>()->T;
    
    
}



