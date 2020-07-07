//
//  Unlock.swift
//  flutterwitsystem
//
//  Created by yyjjls on 2020/7/2.
//


import Foundation
import CoreBluetooth


class Unlock: NSObject, BleCall, CBPeripheralDelegate {

    //private var centralManager: CBCentralManager?;
    private var peripheral: CBPeripheral?;

    private var ble: Ble

    private var unlockInfo: UnlockInfo?;


    private override init() {
        ble = Ble.getInstance;
        unlockInfo = nil;
    }

    //单例
    public static let getInstance = Unlock()

    //添加回调
    public func addCall(unlockInfo: UnlockInfo) {
        self.unlockInfo = unlockInfo;
    }


    /*
     开启附近的设备
     */

    public func unlock() -> Bool {
        scan();
        return true;
    }


    /*
     开启制定的设备
     */
    public func unlock(deviceId: String) -> Bool {

        return true;
    }


    private func scan() {
        ble.scan(bleCall: self);
    }

    private func stopScan() {
        ble.stopScan();
    }


    func belState(code: Int, msg: String) {

    }

    func scanDevice(central: CBCentralManager, peripheral: CBPeripheral, advertisementData: [String: Any], rssi: NSNumber) {
        if (peripheral.name != nil && DeviceManager.getInstance(appId: "", token: "").getDevice(deviceId: peripheral.name ?? "") != nil) {
            self.peripheral = peripheral;
            stopScan();
            //central.connect(peripheral, options: nil);
            ble.connect(peripheral, options: nil);
            print("扫描到的设备开始连接》》》》\(peripheral.name) \(rssi.intValue) \(peripheral.state)");
        }
        //central.connect(peripheral, options: nil);

    }

    func error(code: Int, error: String) {
        print("连接异常\(error) ");
    }


    func connect(central: CBCentralManager, didConnect peripheral: CBPeripheral) {
        print("连接成功\(peripheral.name) ");
        peripheral.delegate = self;
        peripheral.discoverServices([Ble.SERVICES])
    }

    func disconnect(central: CBCentralManager, didDisconnectPeripheral peripheral: CBPeripheral, error: Error?) {
        print("断开连接\(peripheral.name) ");
    }

    //发现服务
    func peripheral(_ peripheral: CBPeripheral, didDiscoverServices error: Error?) {
        print("发现服务");
        if (error != nil) {
            print("发现服务失败");
            return;
        }
        for service: CBService in peripheral.services! {
            if (service.uuid.isEqual(Ble.SERVICES)) {
                peripheral.discoverCharacteristics(nil, for: service)
                return;
            }
        }
    }

    //发现特征值
    func peripheral(_ peripheral: CBPeripheral, didDiscoverCharacteristicsFor service: CBService, error: Error?) {
        print("发现特征值");
        if (error != nil) {
            print("发现特征值失败");
            return;
        }
        //读取token
        // peripheral.discoverCharacteristics([Ble.TOKEN], for: peripheral.services![0])
        peripheral.readValue(for: getCharacteristic(services: peripheral.services![0], uuid: Ble.TOKEN)!);
    }


    //读取到的值
    func peripheral(_ peripheral: CBPeripheral, didUpdateValueFor characteristic: CBCharacteristic, error: Error?) {
        print("读取数据");
        if (error != nil) {
            print("读取数据失败");
            return;
        }
        if (characteristic.uuid.isEqual(Ble.TOKEN)) {
            let allData = characteristic.value;
            let key = DeviceManager.getInstance(appId: "", token: "").getDevice(deviceId: peripheral.name ?? "")?.bleDeviceKey ?? "";
            let encryptedPassword128 = allData?.aesEncrypt(keyData: Data.init(hex: key), operation: kCCEncrypt)
            peripheral.writeValue(Data(hex: "01\(encryptedPassword128!.toHexString())"), for: getCharacteristic(services: peripheral.services![0], uuid: Ble.UNLOCK)!, type: CBCharacteristicWriteType.withResponse)


        }
    }

    //写入值成功 代表开门成功
    func peripheral(_ peripheral: CBPeripheral, didWriteValueFor characteristic: CBCharacteristic, error: Error?) {
        print("写入值成功，开门成功");


        ble.disConnect(peripheral);
    }


    //获取制定的特征值
    private func getCharacteristic(services: CBService, uuid: CBUUID) -> CBCharacteristic? {
        for characteristic: CBCharacteristic in services.characteristics! {
            if (characteristic.uuid.isEqual(uuid)) {
                return characteristic;
            }
        }
        return nil;
    }

}



