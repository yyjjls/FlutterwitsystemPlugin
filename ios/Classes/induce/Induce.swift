//
//  Induce.swift
//  flutterwitsystem
//
//  Created by yyjjls on 2020/4/30.
//

import Foundation
import CoreBluetooth

class Induce:NSObject,CBCentralManagerDelegate,CBPeripheralDelegate{
    
    public static let getInstance = Induce();
    
    
    private   let SCAN = CBUUID.init(string:"0000fff1-0000-1000-8000-00805f9b34fb");
    //链接成功
    private   let SERVICES = CBUUID.init(string:"0000fff1-0000-1000-8000-00805f9b34fb");
    
    // 读取token 的UUID
    private  let TOKEN = CBUUID.init(string:"0000ff05-0000-1000-8000-00805f9b34fb");
       
    // 开锁特征
    private  let UNLOCK = CBUUID.init(string: "0000ff04-0000-1000-8000-00805f9b34fb");
    
    var centralManager:CBCentralManager?;
    
    var tokenCharacteristic:CBCharacteristic?;
    
    var unlockCharacteristic:CBCharacteristic?;
    
    var peripheral: CBPeripheral?;

    override init() {
        super.init();
        centralManager=CBCentralManager.init(delegate:self, queue:nil);
    }
    
    ///开启感应开锁
   public func openInduceUnlock()->Bool{
        centralManager?.scanForPeripherals(withServices:[SCAN], options: [CBCentralManagerScanOptionAllowDuplicatesKey: true]);
    
        return true;
    }
    ///关闭感应开锁
    public func stopInduceUnlock()->Bool{
        if(centralManager==nil){
            return false;
        }
        centralManager?.stopScan();
        
        if(peripheral != nil){
               centralManager?.cancelPeripheralConnection(self.peripheral!);
          }
      
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
        print("扫描到的设备\(peripheral.name) \(RSSI.intValue) \(peripheral.state)");
        if(peripheral.name == "Slock04EE033EA8CF" && RSSI.intValue > -100){
             stopInduceUnlock();
            //central.cancelPeripheralConnection(peripheral);
            self.peripheral=peripheral;
            central.connect(peripheral, options: nil);
        }
    }
    
    
    func centralManager(_ central: CBCentralManager, didConnect peripheral: CBPeripheral) {
        print("链接成功");
       
        peripheral.delegate=self;
        peripheral.discoverServices([SERVICES])
       }
    
    //链接失败
    func centralManager(_ central: CBCentralManager, didFailToConnect peripheral: CBPeripheral, error: Error?) {
        print("链接设备失败");
     
    }
    
    //设备断开
      func centralManager(_ central: CBCentralManager, didDisconnectPeripheral peripheral: CBPeripheral, error: Error?) {
          print("设备连接断开");
       
      }
 
    //发现服务
    func peripheral(_ peripheral: CBPeripheral, didDiscoverServices error: Error?) {
        print("发现服务");
        if(error != nil){
            print("发现服务失败");
        }
        for service:CBService in peripheral.services!{
            if(service.uuid.isEqual(SERVICES)){
                peripheral.discoverCharacteristics(nil, for: service)
                return;
            }
        }
        
    }
    
    //发现特征值
    func peripheral(_ peripheral: CBPeripheral, didDiscoverCharacteristicsFor service: CBService, error: Error?) {
        print("发现特征值");
        for characteristic:CBCharacteristic in service.characteristics!{
            if(characteristic.uuid.isEqual(TOKEN)){
                tokenCharacteristic=characteristic;
            }else if(characteristic.uuid.isEqual(UNLOCK)){
                unlockCharacteristic=characteristic;
            }
        }
         peripheral.readValue(for: tokenCharacteristic!);
        //peripheral.readRSSI();
        
    }
    //读取到的值
    func peripheral(_ peripheral: CBPeripheral, didUpdateValueFor characteristic: CBCharacteristic, error: Error?) {
        print("读取数据");
        if(characteristic.uuid.isEqual(TOKEN)){
            let allData = characteristic.value;
            print("读取到的token\(allData?.toHexString())");
            let key = "491b86de5a7258a63df7277760881ded"
            let encryptedPassword128 = allData?.aesEncrypt(keyData:Data.init(hex: key), operation: kCCEncrypt)
            peripheral.writeValue(Data(hex: "01\(encryptedPassword128!.toHexString())"), for: unlockCharacteristic!, type: CBCharacteristicWriteType.withResponse)
         
             
            
            
        }
    }
    
    //写入值成功 代表开门成功
    func peripheral(_ peripheral: CBPeripheral, didWriteValueFor characteristic: CBCharacteristic, error: Error?) {
        print("写入值成功，开门成功");
        
        
    }
    
    
    
    
    
   //data 转string

    func dataToString(data: Data) -> String {
            return String(format: "%@", data as CVarArg)
        }

    
//[bytes]转NSData
// func string(from data: Data) -> NSData {
//let bytes:[UInt8] = [0x07,0x4d,0x45,0x53,0x48,0x31,0x32,0x33,0x00,0x00,0x00,0x00,0x00,0x00,0x00]
//let data = NSData(bytes: bytes, length: bytes.count)
//print(data)
//}
}




