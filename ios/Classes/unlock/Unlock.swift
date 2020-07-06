//
//  Unlock.swift
//  flutterwitsystem
//
//  Created by yyjjls on 2020/7/2.
//


import Foundation
import CoreBluetooth


class Unlock: NSObject, BleCall, CBPeripheralDelegate {


    private let SCAN = CBUUID.init(string: "0000fff1-0000-1000-8000-00805f9b34fb");

    private var centralManager: CBCentralManager?;

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
        let paramDic: [String: String] = ["token": "sssssss"]
      //  let vv = HttpsClient.POSTAction(urlStr: "/device/get_device", param: paramDic);
      //  print("返回值111：\(vv)");
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
        ble.stopscan();
    }


    func belState(code: Int, msg: String) {

    }

    func scanDevice(central: CBCentralManager, peripheral: CBPeripheral, advertisementData: [String: Any], rssi: NSNumber) {
        centralManager = central;
        print("扫描到的设备\(peripheral.name) \(rssi.intValue) \(peripheral.state)");

        //central.connect(peripheral, options: nil);

    }

    func error(code: Int, error: String) {

    }


    func connect(central: CBCentralManager, didConnect peripheral: CBPeripheral) {

    }

    func disconnect(central: CBCentralManager, didDisconnectPeripheral peripheral: CBPeripheral, error: Error?) {

    }


}



