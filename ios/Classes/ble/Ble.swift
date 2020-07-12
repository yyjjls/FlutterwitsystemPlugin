//
//  Ble.swift
//  flutterwitsystem
//
//  Created by yyjjls on 2020/7/2.
//

import Foundation
import CoreBluetooth


class Ble: NSObject, CBCentralManagerDelegate {

    private static let SCAN = CBUUID.init(string: "0000fff1-0000-1000-8000-00805f9b34fb");
    public static let SCAN2 = CBUUID.init(string: "0000f1ff-0000-1000-8000-00805f9b34fb");

    //链接成功
    public static let SERVICES = CBUUID.init(string: "0000fff1-0000-1000-8000-00805f9b34fb");

    // 读取token 的UUID
    public static let TOKEN = CBUUID.init(string: "0000ff05-0000-1000-8000-00805f9b34fb");

    // 开锁特征
    public static let UNLOCK = CBUUID.init(string: "0000ff04-0000-1000-8000-00805f9b34fb");

    //读取电量
    public static let BATTERY = CBUUID.init(string: "0000ff01-0000-1000-8000-00805f9b34fb");

    ///串口发送数据特征
    public static let SERIAL_PORT_WRITE = CBUUID.init(string: "0000ff06-0000-1000-8000-00805f9b34fb");

    ///串口发送接受通知特征
    public static let SERIAL_PORT_READ = CBUUID.init(string: "0000ff07-0000-1000-8000-00805f9b34fb");

    ///读取设备的key
    public static let KEY = CBUUID.init(string: "0000ff02-0000-1000-8000-00805f9b34fb");

    ///发送完成添加设备命令
    public static let ADD_FINISH = CBUUID.init(string: "0000ff03-0000-1000-8000-00805f9b34fb");


    private var centralManager: CBCentralManager?;

    private var bleCall: BleCall? = nil;

    private var bleState = 0;
    //单例
    public static let getInstance = Ble();

    private override init() {
        super.init();
        centralManager = CBCentralManager.init(delegate: self, queue: nil);
    }

    func centralManagerDidUpdateState(_ central: CBCentralManager) {
        switch central.state {
        case .poweredOff:
            print("蓝牙已关闭")
            bleState = BleCode.DEVICE_BLUE_OFF;
            break
        case .poweredOn:
            bleState = BleCode.BLUE_NO;
            print("蓝牙已打开,请扫描外设");
            break
        case .resetting:
            print("正在重置状态")
            bleState = BleCode.BLUE_OFF;
            break
        case .unauthorized:
            print("无权使用蓝牙低功耗")
            bleState = BleCode.BLE_UNKNOWN_MISTAKE;
            break
        case .unknown:
            print("未知设备")
            bleState = BleCode.BLE_UNKNOWN_MISTAKE;
            break
        case .unsupported:
            print("此设备不支持BLE")
            bleState = BleCode.DEVICE_NOT_BLUE;
            break
        default:
            break;

        }
        bleCall?.belState(code: bleState, msg: "");

    }

    //获得蓝牙的状态
    public func getBleState() -> Int {
        return bleState;
    }

    //是否在扫描
//    public func isScanning() -> Bool {
//        return centralManager!.isScanning;
//    }

    //扫描我们自己的设备设备
    public func scan(bleCall: BleCall) {
        self.bleCall = bleCall;
        if (bleState != BleCode.BLUE_NO) {
            bleCall.error(code: bleState, error: "Bluetooth exception see exception code");
            return;
        }
        centralManager?.scanForPeripherals(withServices: [Ble.SCAN, Ble.SCAN2], options: [CBCentralManagerScanOptionAllowDuplicatesKey: true]);
    }

    //扫描指定包含的设备
    public func scan(serviceUUIDs: [CBUUID]?, bleCall: BleCall) {
        self.bleCall = bleCall;
        if (bleState != BleCode.BLUE_NO) {
            bleCall.error(code: bleState, error: "Bluetooth exception see exception code");
            return;
        }
        centralManager?.scanForPeripherals(withServices: serviceUUIDs, options: [CBCentralManagerScanOptionAllowDuplicatesKey: true]);
    }


    //停止扫描
    public func stopScan() {
        centralManager?.stopScan();
    }

    //连接设备
    public func connect(_ peripheral: CBPeripheral, options: [String: Any]? = nil) {
        centralManager?.connect(peripheral, options: nil);
    }

    //取消连接
    public func cancelConnection(_ peripheral: CBPeripheral) {
        centralManager?.cancelPeripheralConnection(peripheral);
    }

    //断开设备
    public func disConnect(_ peripheral: CBPeripheral) {
        centralManager?.cancelPeripheralConnection(peripheral);
    }

    //扫描到的设备回掉
    func centralManager(_ central: CBCentralManager, didDiscover peripheral: CBPeripheral, advertisementData: [String: Any], rssi RSSI: NSNumber) {
        bleCall?.scanDevice(central: central, peripheral: peripheral, advertisementData: advertisementData, rssi: RSSI)
    }


    //链接成功
    func centralManager(_ central: CBCentralManager, didConnect peripheral: CBPeripheral) {
        bleCall?.connect(central: central, didConnect: peripheral);
    }

    //链接失败
    func centralManager(_ central: CBCentralManager, didFailToConnect peripheral: CBPeripheral, error: Error?) {
        bleCall?.error(code: BleCode.CONNECTION_FAIL, error: "Failed to connect device");
    }

    //设备断开链接
    func centralManager(_ central: CBCentralManager, didDisconnectPeripheral peripheral: CBPeripheral, error: Error?) {
        bleCall?.disconnect(central: central, didDisconnectPeripheral: peripheral, error: error);
    }


    //获取制定的特征值
    public func getCharacteristic(services: CBService, uuid: CBUUID) -> CBCharacteristic? {
        for characteristic: CBCharacteristic in services.characteristics! {
            if (characteristic.uuid.isEqual(uuid)) {
                return characteristic;
            }
        }
        return nil;
    }


}
