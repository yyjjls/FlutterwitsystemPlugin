//
//  Induce.swift
//  flutterwitsystem
//
//  Created by yyjjls on 2020/4/30.
//

import Foundation
import CoreBluetooth

class Induce:NSObject,CBCentralManagerDelegate{
    
    public static let getInstance = Induce();
    
    var centralManager:CBCentralManager?;
    var centralManager2:CBCentralManager?;

    override init() {
        super.init();
       
        centralManager=CBCentralManager.init(delegate:self, queue:nil);
        
    }
    
    ///开启感应开锁
   public func openInduceUnlock()->Bool{
    print("蓝牙已关闭\(centralManager == centralManager2)");
    centralManager2=centralManager;
//        centralManager?.scanForPeripherals(withServices:nil, options: [CBCentralManagerScanOptionAllowDuplicatesKey: true]);
    
        return true;
    }
    ///关闭感应开锁
    public func stopInduceUnlock()->Bool{
        if(centralManager==nil){
            return false;
        }
        centralManager?.stopScan();
        
        return true;
    }
   
    //蓝牙设备状态更新时候的回掉
     func centralManagerDidUpdateState(_ central: CBCentralManager) {
       
         switch central.state {
                case .poweredOff:
                    print("蓝牙已关闭")
                    break
                case .poweredOn:
                    print("蓝牙已打开,请扫描外设")
                    break
                case .resetting:
                    print("正在重置状态")
                    break
                case .unauthorized:
                    print("无权使用蓝牙低功耗")
                    break
                case .unknown:
                    print("未知设备")
                    break
                case .unsupported:
                    print("此设备不支持BLE")
                    break
                }
       
    }
     //扫描到的设备回掉
    func centralManager(_ central: CBCentralManager, didDiscover peripheral: CBPeripheral, advertisementData: [String : Any], rssi RSSI: NSNumber) {
        print("扫描到的设备\(peripheral.name)");
    }
    
    func centralManager(_ central: CBCentralManager, didFailToConnect peripheral: CBPeripheral, error: Error?) {
       
     
    }
    
 

}


