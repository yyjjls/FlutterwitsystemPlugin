//
//  Induce.swift
//  flutterwitsystem
//
//  Created by yyjjls on 2020/4/30.
//

import Foundation
import CoreBluetooth
import CoreLocation

public class Induce: NSObject, CBCentralManagerDelegate, CBPeripheralDelegate, CLLocationManagerDelegate {
    fileprivate var beaconRegion: CLBeaconRegion!
    fileprivate var locationManager: CLLocationManager!
    let userDefault = UserDefaults.standard;
    private var timer: Timer?;
    public static let getInstance = Induce();

    private let SCAN = CBUUID.init(string: "0000fff1-0000-1000-8000-00805f9b34fb");
    //链接成功
    private let SERVICES = CBUUID.init(string: "0000fff1-0000-1000-8000-00805f9b34fb");

    // 读取token 的UUID
    private let TOKEN = CBUUID.init(string: "0000ff05-0000-1000-8000-00805f9b34fb");

    // 开锁特征
    private let UNLOCK = CBUUID.init(string: "0000ff04-0000-1000-8000-00805f9b34fb");

    var centralManager: CBCentralManager?;

    var tokenCharacteristic: CBCharacteristic?;

    var unlockCharacteristic: CBCharacteristic?;

    var peripheral: CBPeripheral?;

    override init() {
        super.init();
        initData();
    }

    func initData() {
      //  locationManager = CLLocationManager();
      //  locationManager.delegate = self
        //请求一直允许定位
       // locationManager.requestAlwaysAuthorization()
        // beaconRegion = CLBeaconRegion(proximityUUID: UUID(uuidString: "E2C56DB5-DFFB-48D2-B060-D0F5A71096E0")!, identifier: "qweiei")
      //  beaconRegion = CLBeaconRegion(proximityUUID: UUID(uuidString: "0000FF04-0000-1000-8000-00805F9B34FB")!, identifier: "qweiei")
      //  beaconRegion.notifyEntryStateOnDisplay = true
        centralManager = CBCentralManager.init(delegate: self, queue: nil, options: [CBCentralManagerOptionRestoreIdentifierKey: "0000ff04-0000-1000-8000-00805f9b34fb", CBCentralManagerOptionShowPowerAlertKey: true]);

    }


    ///开启感应开锁
    public func openInduceUnlock() -> Bool {
        //开始扫描
//        locationManager.startMonitoring(for: beaconRegion)
//        locationManager.startRangingBeacons(in: beaconRegion)
        if (centralManager?.state != .poweredOn) {
            return false;
        }
        let uuidString = userDefault.string(forKey: "Slock04EE033EABD7")
        if (uuidString == nil) {
            if (centralManager?.state != .poweredOn) {
                return false;
            }
            centralManager?.scanForPeripherals(withServices: [SCAN], options: [CBCentralManagerScanOptionAllowDuplicatesKey: true]);

        } else {
//            let uuid = UUID.init(uuidString: uuidString!)
//            let uuids: [UUID] = [UUID.init(uuidString: uuidString!)!]
            let targetPeripheral: CBPeripheral? = centralManager?.retrievePeripherals(withIdentifiers: [UUID.init(uuidString: uuidString!)!]).first;
            self.peripheral = targetPeripheral;
            print("直接连接2》》》》》\(targetPeripheral!)");
            centralManager!.connect(targetPeripheral!, options: nil);
        }

        return true;
    }











    ///关闭感应开锁
    public func stopInduceUnlock() -> Bool {
        //  locationManager.stopRangingBeacons(in: beaconRegion);
        closeBleTimer();
        if (centralManager == nil) {
            return false;
        }
        centralManager?.stopScan();

        if (peripheral != nil) {
            centralManager?.cancelPeripheralConnection(self.peripheral!);
        }
        return true;
    }


    //进入beacon区域
    public func locationManager(_ manager: CLLocationManager, didEnterRegion region: CLRegion) {
        locationManager.startRangingBeacons(in: beaconRegion)
        print("进入beacon区域")
        if (centralManager == nil) {
            initData();
        }
    }

    //离开beacon区域
    public func locationManager(_ manager: CLLocationManager, didExitRegion region: CLRegion) {
        locationManager.stopRangingBeacons(in: beaconRegion)
        print("离开beacon区域")


        closeBleTimer();
        if (centralManager == nil) {
            return;
        }
        centralManager?.stopScan();
        if (peripheral != nil) {
            centralManager?.cancelPeripheralConnection(self.peripheral!);
        }
    }

    public func locationManager(_ manager: CLLocationManager, didRangeBeacons beacons: [CLBeacon], in region: CLBeaconRegion) {
        //返回是扫描到的beacon设备数组，这里取第一个设备
        guard beacons.count > 0 else {
            return
        }
        let beacon = beacons.first!
        //accuracy可以获取到当前距离beacon设备距离
        let location = String(format: "%.3f", beacon.accuracy)
        print("距离beacon\(location)m")
        print("距离beacon\(beacon)")
        if (beacon.accuracy <= 1 && beacon.accuracy >= 0) {
            if (centralManager == nil) {
                initData();
            }
            if (centralManager?.state == .poweredOn) {
                centralManager?.scanForPeripherals(withServices: [SCAN], options: [CBCentralManagerScanOptionAllowDuplicatesKey: true]);
            }
        }

    }

    public func locationManager(_ manager: CLLocationManager, monitoringDidFailFor region: CLRegion?, withError error: Error) {
        print("Failed monitoring region: \(error.localizedDescription)")
    }

    public func locationManager(_ manager: CLLocationManager, didFailWithError error: Error) {
        print("Location manager failed: \(error.localizedDescription)")
    }


    //蓝牙设备状态更新时候的回掉
    public func centralManagerDidUpdateState(_ central: CBCentralManager) {

        switch central.state {
        case .poweredOff:
            print("蓝牙已关闭")
            break
        case .poweredOn:
            print("蓝牙已打开,请扫描外设")
             openInduceUnlock();
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
    public func centralManager(_ central: CBCentralManager, didDiscover peripheral: CBPeripheral, advertisementData: [String: Any], rssi RSSI: NSNumber) {
        print("扫描到的设备\(peripheral.name) \(RSSI.intValue) \(peripheral.identifier.uuidString)");
        if (peripheral.name == nil || DeviceManager.getInstance(appId: "smart09cdcb9ebb2c4169957f0d5423432ff2", token: "4659a0fd6c0443ac8ac946c4709b8d31-1595065244621").getDevice(deviceId: peripheral.name!) == nil) {
            return;
        }
        userDefault.set(peripheral.identifier.uuidString, forKey: peripheral.name!)
        print("保存数据》》》\(peripheral.name!):::\(peripheral.identifier.uuidString)");
        if (RSSI.intValue > -80) {
            _ = stopInduceUnlock();
            self.peripheral = peripheral;
            central.connect(peripheral, options: nil);
        }
    }

    public func centralManager(_ central: CBCentralManager, didConnect peripheral: CBPeripheral) {
        print("链接成功");
        peripheral.delegate = self;
        //peripheral.discoverServices([SERVICES]);
        peripheral.readRSSI();
    }

    //链接失败
    public func centralManager(_ central: CBCentralManager, didFailToConnect peripheral: CBPeripheral, error: Error?) {
        print("链接设备失败");

    }

    //设备断开
    public func centralManager(_ central: CBCentralManager, didDisconnectPeripheral peripheral: CBPeripheral, error: Error?) {
        print("设备连接断开");

    }


    public func centralManager(_ central: CBCentralManager, connectionEventDidOccur event: CBConnectionEvent, for peripheral: CBPeripheral) {
        print("connectionEventDidOccur之前连接》》》》》》》》》》》》》》》》》》》》》》》》》》》》》》》》》》》》》")
    }

    public func centralManager(_ central: CBCentralManager, didUpdateANCSAuthorizationFor peripheral: CBPeripheral) {
        print("didUpdateANCSAuthorizationFor")
    }

    public func centralManager(_ central: CBCentralManager, willRestoreState dict: [String: Any]) {
        print("willRestoreState")
    }

    public func peripheral(_ peripheral: CBPeripheral, didReadRSSI RSSI: NSNumber, error: Error?) {
        print("读取到的信号\(RSSI)")
        if (Int.init(truncating: RSSI) < -70) {
            centralManager?.cancelPeripheralConnection(peripheral);
            bleTimer(timeInterval: 1.5, aSelector: #selector(resConnect))
        }else if(Int.init(truncating: RSSI) < -60){
         peripheral.readRSSI();
        }else{
            peripheral.discoverServices([SERVICES]);
        }
    }

    @objc func resConnect(){
        _ = openInduceUnlock();
    }

    //发现服务
    public func peripheral(_ peripheral: CBPeripheral, didDiscoverServices error: Error?) {
        print("发现服务");
        if (error != nil) {
            print("发现服务失败");
        }
        for service: CBService in peripheral.services! {
            if (service.uuid.isEqual(SERVICES)) {
                peripheral.discoverCharacteristics(nil, for: service)
                return;
            }
        }

    }

    //发现特征值
    public func peripheral(_ peripheral: CBPeripheral, didDiscoverCharacteristicsFor service: CBService, error: Error?) {
        print("发现特征值");
        for characteristic: CBCharacteristic in service.characteristics! {
            if (characteristic.uuid.isEqual(TOKEN)) {
                tokenCharacteristic = characteristic;
            } else if (characteristic.uuid.isEqual(UNLOCK)) {
                unlockCharacteristic = characteristic;
            }
        }
        peripheral.readValue(for: tokenCharacteristic!);
        //peripheral.readRSSI();

    }

    //读取到的值
    public func peripheral(_ peripheral: CBPeripheral, didUpdateValueFor characteristic: CBCharacteristic, error: Error?) {
        print("读取数据");
        if (characteristic.uuid.isEqual(TOKEN)) {
            let allData = characteristic.value;
            print("读取到的token\(allData?.toHexString())");
            //let key = "491b86de5a7258a63df7277760881ded"
            let key = DeviceManager.getInstance(appId: "", token: "").getDevice(deviceId: peripheral.name!)?.bleDeviceKey;
            let encryptedPassword128 = allData?.aesEncrypt(keyData: Data.init(hex: key!), operation: kCCEncrypt)
            peripheral.writeValue(Data(hex: "01\(encryptedPassword128!.toHexString())"), for: unlockCharacteristic!, type: CBCharacteristicWriteType.withResponse)
        }
    }

    //写入值成功 代表开门成功
    public func peripheral(_ peripheral: CBPeripheral, didWriteValueFor characteristic: CBCharacteristic, error: Error?) {
        print("写入值成功，开门成功");
        centralManager?.cancelPeripheralConnection(peripheral);
        bleTimer(timeInterval: 8, aSelector: #selector(UnlockSuccessWartTime))
        // openInduceUnlock();
    }

    @objc func UnlockSuccessWartTime() {
        //centralManager?.connect(peripheral!)
        openInduceUnlock();
    }


    //data 转string
    func dataToString(data: Data) -> String {
        return String(format: "%@", data as CVarArg)
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


//[bytes]转NSData
// func string(from data: Data) -> NSData {
//let bytes:[UInt8] = [0x07,0x4d,0x45,0x53,0x48,0x31,0x32,0x33,0x00,0x00,0x00,0x00,0x00,0x00,0x00]
//let data = NSData(bytes: bytes, length: bytes.count)
//print(data)
//}
}




