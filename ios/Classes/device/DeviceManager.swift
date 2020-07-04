//
//  DeviceManager.swift
//  flutterwitsystem
//
//  Created by yyjjls on 2020/7/3.
//设备管理者

import Foundation

class DeviceManager:Device{
    
    private static var device:Device=NSNull() as! Device;
    
 
    
    public static func getIntance()->Device{
        if(device is NSNull){
            device =  DeviceManager();
        }
        return device;
    }
    
    
    func getNetWorkDevice() -> Bool {
       
        
        return false;
        
    }
    
    func getCacheDevice() -> Bool {
       
        return false;
    }
    
    func getDevice<T>(deviceId: String) -> T {
       
        return NSNull() as! T;
    }
    
    func getDevices<T>() -> T {
       
          return NSNull() as! T;
    }
    
    func getDevicesNumber() -> Int {
       
          return 0;
    }
    
    func getThreeDevices<T>() -> T {
       
          return NSNull() as! T;
    }
    
    
}
