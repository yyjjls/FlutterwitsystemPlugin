//
//  Unlock.swift
//  flutterwitsystem
//
//  Created by yyjjls on 2020/7/2.
//


import Foundation
import CoreBluetooth



class Unlock:NSObject,BleCall,CBPeripheralDelegate{
  
    
     
    private let SCAN = CBUUID.init(string:"0000fff1-0000-1000-8000-00805f9b34fb");
    
    private var centralManager:CBCentralManager?;
    
    private var ble:Ble
    
    private override init() {
        ble = Ble.getInstance;
       }
    
    //单例
    public static let getInstance = Unlock()
    
  
    
    
    
    /*
     开启附近的设备
     */
    public func unlock() -> Bool {
        print("返回值：\(centralManager)");
        scan();
        return true;
    }
    
    
    /*
     开启制定的设备
     */
    public func unlcok(deviceId:String) -> Bool {
     
        return true;
     }
     
 
    private func scan(){
        ble.scan(bleCall: self);
    }
    
    private func stopscan(){
        ble.stopscan();
    }
    
    
    
    
      func belState(code: Int, msg: String) {
           
        }
        
      func scanDevice(central: CBCentralManager, peripheral: CBPeripheral, advertisementData: [String : Any], rssi: NSNumber) {
          centralManager=central;
           print("扫描到的设备\(peripheral.name) \(rssi.intValue) \(peripheral.state)");
        }
        
      func error(code: Int, error: String) {
           
        }
    
    
    
    
}



