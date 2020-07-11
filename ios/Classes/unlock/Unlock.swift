//
//  Unlock.swift
//  flutterwitsystem
//
//  Created by yyjjls on 2020/7/2.
//


import Foundation
import CoreBluetooth


class Unlock: NSObject, BleUnlock, BleCall, CBPeripheralDelegate {


    //private var centralManager: CBCentralManager?;
    private var peripheral: CBPeripheral?;

    private var ble: Ble

    private var unlockInfo: UnlockInfo?;

    private var timer: Timer?;

    private static var bleUnlock: BleUnlock?;

    //当值不为nil的时候为开启制定设备
    private var deviceId: String? = nil;
    private var appId: String?;
    private var token: String?

    private override init() {
        ble = Ble.getInstance;
        unlockInfo = nil;
    }


    //单例
    public static func getInstance(appId: String?, token: String?) -> BleUnlock {
        if (bleUnlock == nil) {
            bleUnlock = Unlock();
            (bleUnlock as! Unlock).appId = appId;
            (bleUnlock as! Unlock).token = token;
        }
        return bleUnlock!;
    }


    //添加回调
    public func addCallBack(unlockInfo: UnlockInfo) {
        self.unlockInfo = unlockInfo;
    }


/*
 开启附近的设备
 */
    public func unlock() -> Bool {
        self.deviceId = nil;
        self.peripheral = nil;
        if (DeviceManager.getInstance(appId: "", token: "").getDevicesNumber() == 0) {
            failCall(error: "No equipment currently available", code: BleCode.NO_DEVICE);
            return false;
        }
        scan();
        return true;
    }


/*
 开启指定的设备
 */
    public func unlock(deviceId: String) -> Bool {
        self.deviceId = deviceId;
        self.peripheral = nil;
        if (DeviceManager.getInstance(appId: "", token: "").getDevice(deviceId: deviceId) == nil) {
            failCall(error: "Failed to obtain device information", code: BleCode.GET_DEVICE_INFO_FAIL);
            return false;
        }
        scan();
        return true;
    }

    private func scan() {
        closeBleTimer();
        ble.scan(bleCall: self);
        bleTimer(timeInterval: 6, aSelector: #selector(scanOutTime))
    }

    //扫描超时
    @objc func scanOutTime() {
        failCall(error: "Scan device timeout", code: BleCode.SCAN_OUT_TIME);
        stopScan();
    }

    private func stopScan() {
        ble.stopScan();
    }

    func belState(code: Int, msg: String) {
        if (ble.getBleState() == BleCode.DEVICE_BLUE_OFF) {
            closeBleTimer();//蓝牙关闭直接关闭定时器
        }
    }

    func scanDevice(central: CBCentralManager, peripheral: CBPeripheral, advertisementData: [String: Any], rssi: NSNumber) {
        if (peripheral.name == nil) {
            return;
        }
        //判断是否开启指定设备
        if (self.deviceId != nil && self.deviceId != peripheral.name) {
            return;
        } else //开启附近设备，判断是否有该设备的权限
        if (self.deviceId == nil && DeviceManager.getInstance(appId: "", token: "").getDevice(deviceId: peripheral.name!) == nil) {
            return;
        }
        self.peripheral = peripheral;
        stopScan();
        closeBleTimer();
        ble.connect(peripheral, options: nil);
        bleTimer(timeInterval: 3, aSelector: #selector(connectOutTime));
        //  print("扫描到的设备开始连接》》》》\(peripheral.name) \(rssi.intValue) \(peripheral.state)");
    }

    //连接设备超时
    @objc func connectOutTime() {
        ble.cancelConnection(peripheral!);
        failCall(error: "Connect device timeout", code: BleCode.CONNECTION_TIMEOUT);
    }

    func error(code: Int, error: String) {
        // print("连接异常\(error) ");
        if (ble.getBleState() == BleCode.BLUE_NO) {
            ble.cancelConnection(peripheral!);
        }
        failCall(error: error, code: code);
    }

    func connect(central: CBCentralManager, didConnect peripheral: CBPeripheral) {
        // print("连接成功\(peripheral.name) ");
        closeBleTimer();
        peripheral.delegate = self;
        peripheral.discoverServices([Ble.SERVICES])
    }

    func disconnect(central: CBCentralManager, didDisconnectPeripheral peripheral: CBPeripheral, error: Error?) {
        // print("断开连接\(peripheral.name)");
        if (error != nil) {
            failCall(error: "Bluetooth accidental disconnect", code: BleCode.UNEXPECTED_DISCONNECT)
        }
    }

    //发现服务
    func peripheral(_ peripheral: CBPeripheral, didDiscoverServices error: Error?) {
        // print("发现服务");
        if (error != nil) {
            failCall(error: "Discover service failure", code: BleCode.GET_SERVICE_FAIL)
            ble.disConnect(peripheral);
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
        //  print("发现特征值");
        if (error != nil) {
            failCall(error: "Discover Characteristics failure", code: BleCode.GET_CHARACTERISTIC_FAIL)
            ble.disConnect(peripheral);
            return;
        }
        //读取token
        // peripheral.discoverCharacteristics([Ble.TOKEN], for: peripheral.services![0])
        peripheral.readValue(for: ble.getCharacteristic(services: peripheral.services![0], uuid: Ble.TOKEN)!);
    }

    //读取到的值
    func peripheral(_ peripheral: CBPeripheral, didUpdateValueFor characteristic: CBCharacteristic, error: Error?) {
        //   print("读取数据");
        if (error != nil) {
            failCall(error: "Failed to read data", code: BleCode.READ_DATA_FAIL)
            ble.disConnect(peripheral);
            return;
        }
        if (characteristic.uuid.isEqual(Ble.TOKEN)) {
            let allData = characteristic.value;
            let key = DeviceManager.getInstance(appId: "", token: "").getDevice(deviceId: peripheral.name ?? "")?.bleDeviceKey ?? "";
            let encryptedPassword128 = allData?.aesEncrypt(keyData: Data.init(hex: key), operation: kCCEncrypt)
            peripheral.writeValue(Data(hex: "01\(encryptedPassword128!.toHexString())"), for: ble.getCharacteristic(services: peripheral.services![0], uuid: Ble.UNLOCK)!, type: CBCharacteristicWriteType.withResponse)
        } else if (characteristic.uuid.isEqual(Ble.BATTERY)) {
            let allData = characteristic.value;
            closeBleTimer();
            batteryCall(deviceId: peripheral.name!, battery: NSString(format: "%d", allData![6]).integerValue);
            ble.disConnect(peripheral);
            //    print("读取出来的电量\(allData?[6])");
        }
    }

    //写入值成功 代表开门成功
    func peripheral(_ peripheral: CBPeripheral, didWriteValueFor characteristic: CBCharacteristic, error: Error?) {
        if (error != nil) {
            failCall(error: "Failed to write data", code: BleCode.WRITE_DATA_FAIL)
            ble.disConnect(peripheral);
            return;
        }
        // print("写入值成功，开门成功");
        successCall(deviceId: peripheral.name!, code: BleCode.UNLOCK_SUCCESS);
        bleTimer(timeInterval: 0.5, aSelector: #selector(waitReadBattery));
        peripheral.readValue(for: ble.getCharacteristic(services: peripheral.services![0], uuid: Ble.BATTERY)!);
    }

    //等待读取电量超时
    @objc func waitReadBattery() {
        ble.disConnect(peripheral!);
        uploadRecord(state: 0, deviceId: peripheral?.name ?? " ", battery: -1);
    }


    //定时器
    private func bleTimer(timeInterval: Double, aSelector: Selector) {
        timer = Timer.scheduledTimer(timeInterval: timeInterval, target: self, selector: aSelector, userInfo: nil, repeats: false);
    }

    //关闭定时
    private func closeBleTimer() {
        if (timer != nil) {
            timer?.invalidate()
        }
    }

    //失败的回调
    private func failCall(error: String, code: Int) {
        if (peripheral != nil) {
            uploadRecord(state: 1, deviceId: peripheral?.name ?? " ", battery: -1);
        }
        if (unlockInfo != nil) {
            unlockInfo?.fail(error: error, code: code);
        }
    }

    //成功的回调
    private func successCall(deviceId: String, code: Int) {
        if (unlockInfo != nil) {
            unlockInfo?.success(deviceId: deviceId, code: code);
        }
    }

    //电量的回调
    private func batteryCall(deviceId: String, battery: Int) {
        uploadRecord(state: 0, deviceId: deviceId, battery: battery);
        if (unlockInfo != nil) {
            unlockInfo?.battery(deviceId: deviceId, battery: battery);
        }
    }

    //上传开锁记录
    private func uploadRecord(state: Int, deviceId: String, battery: Int) {
        var paramDic: [String: String] = ["token": token!, "appId": appId!, "state": String(state), "deviceId": deviceId]
        if (battery >= 0) {
            paramDic.updateValue(String(battery), forKey: "battery");
        }
        //print("电量\(paramDic)")
        HttpsClient.POSTTaskAction(urlStr: "/device/upload_record", param: paramDic);
    }

}




