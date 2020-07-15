//
//  Device.swift
//  flutterwitsystem
//
//  Created by yyjjls on 2020/7/3.
//设备管理中

import Foundation

protocol Device {

    //从服务器获取设备
    func getNetWorkDevice() -> Bool;

    //数据初始化状态也就是结果 返回true代表设备初始化完成
    func dataInitState() -> Bool;

    //获取缓存设备，在没有网络的时候获取
    func getCacheDevice() -> Bool;

    //设备ID获得该设备的信息
    func getDevice(deviceId: String) -> DeviceInfo.Data?;

    //获得所有的设备
    func getDevices() -> [DeviceInfo.Data];

    //获得设备的个数
    func getDevicesNumber() -> Int;

    //三方获得设备信息
    func getThreeDevices<T>() -> T;


}



