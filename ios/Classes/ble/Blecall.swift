//
//  Blecall.swift
//  flutterwitsystem
//
//  Created by yyjjls on 2020/7/2.
//
//蓝牙的回调
import Foundation
import CoreBluetooth

public protocol  BleCall {
    
    //蓝牙状态回调
    func belState( code:Int, msg:String);
    
    //扫描到设备的回调
    func scanDevice(central:CBCentralManager,peripheral:CBPeripheral,advertisementData: [String : Any], rssi: NSNumber);
    
    //蓝牙异常回调
    func error(code:Int,error:String);
    
    
    //连接成功
    func connect(central: CBCentralManager, didConnect peripheral: CBPeripheral);
    
    //设备断开连接
    func disconnect(central: CBCentralManager, didDisconnectPeripheral peripheral: CBPeripheral, error: Error?);
    
    
}
