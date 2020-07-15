//
//  WitsSdk.swift
//  flutterwitsystem
//
//  Created by yyjjls on 2020/4/30.
//

import Foundation

protocol WitsSdk {


    /**
     * 获得所有设备的信息
     */
    func getDeviceInfo()->[DeviceBasicInfo];

//    func getBleLockDevice() -> String;

    //感应开锁
    func getInduceUnlock() -> Induce;

    //开锁
    func getBleUnlock() -> BleUnlock;

    //串口通信
    func getSerialPort() -> SerialPort;

    /**
     * 获得ble添加设备的对象
     */
    func getAddBleDevice() -> AddDevice;

}
