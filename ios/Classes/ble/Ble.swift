//
//  Ble.swift
//  flutterwitsystem
//
//  Created by yyjjls on 2020/7/2.
//

import Foundation
import CoreBluetooth


class Ble:NSObject,CBCentralManagerDelegate{
    
    private let SCAN = CBUUID.init(string:"0000fff1-0000-1000-8000-00805f9b34fb");
       
    private var centralManager:CBCentralManager?;
    
    private var bleCall:BleCall? = nil;
    
    private var bleState = 0;
    //单例
    public static  let getInstance = Ble();
     
    private override init() {
             super.init();
             centralManager=CBCentralManager.init(delegate:self, queue:nil);
         }
     
     func centralManagerDidUpdateState(_ central: CBCentralManager) {
        switch central.state {
                case .poweredOff:
                    print("蓝牙已关闭")
                    bleState=BleCode.DEVICE_BLUE_OFF;
                    break
                case .poweredOn:
                    bleState=BleCode.BLUE_NO;
                    print("蓝牙已打开,请扫描外设");
                    break
                case .resetting:
                    print("正在重置状态")
                    bleState=BleCode.BLUE_OFF;
                    break
                case .unauthorized:
                    print("无权使用蓝牙低功耗")
                     bleState=BleCode.BLE_UNKNOWN_MISTAKE;
                    break
                case .unknown:
                    print("未知设备")
                     bleState=BleCode.BLE_UNKNOWN_MISTAKE;
                    break
                case .unsupported:
                    print("此设备不支持BLE")
                     bleState=BleCode.DEVICE_NOT_BLUE;
                    break
             default:
                 break;
                 
                }
        bleCall?.belState(code: bleState, msg:"");
         
     }

    //获得蓝牙的状态
    public func getBleState()->Int{
        return bleState;
    }
    
     //扫描设备
     public func scan(bleCall:BleCall){
        self.bleCall=bleCall;
        if(bleState != BleCode.BLUE_NO){
            bleCall.error(code: bleState, error: "Bluetooth exception see exception code");
            return;
        }
       centralManager?.scanForPeripherals(withServices:[SCAN], options: [CBCentralManagerScanOptionAllowDuplicatesKey: true]);
     }
    
    
    
     
    //停止扫描
    public func stopscan(){
        //self.bleCall!=NSNull;
         centralManager?.stopScan();
     }
    
    
     //扫描到的设备回掉
      func centralManager(_ central: CBCentralManager, didDiscover peripheral: CBPeripheral, advertisementData: [String : Any], rssi RSSI: NSNumber) {
        //if(self.bleCall?!=nil){
        bleCall?.scanDevice(central: central, peripheral: peripheral, advertisementData: advertisementData, rssi: RSSI)
          //  }
       }
        
    
    
    
}
