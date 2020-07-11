//
// Created by yyjjls on 2020/7/11.
//

import Foundation
import CoreBluetooth

//添加蓝牙设备
class AddBleDevice: NSObject, AddDevice, BleCall, CBPeripheralDelegate {
    private var appId: String?;
    private var token: String?

    private var addBleDeviceCall: AddBleDeviceCall?
    //保存扫描到的设备
    private var connectDevicesMap = [String: CBPeripheral]();

    private static var addDevice: AddDevice?;

    public static func getInstance(appId: String?, token: String?) -> AddDevice {
        if (addDevice == nil) {
            addDevice = AddBleDevice();
            (addDevice as! AddBleDevice).appId = appId;
            (addDevice as! AddBleDevice).token = token;
        }
        return addDevice!;
    }

    func addCall(addBleDeviceCall: AddBleDeviceCall) {
        self.addBleDeviceCall = addBleDeviceCall;
    }

    func scanDevice() {
        connectDevicesMap.removeAll();
        Ble.getInstance.scan(bleCall: self);
    }

    func stopDevice() {
        Ble.getInstance.stopScan();
    }

    func addDevice(deviceId: String) {
    }

    func cancelAdd() {

    }


    /*》》》》》》》》》》》蓝牙的回调《《《《《《《《《《《《《《《《《*/
    func belState(code: Int, msg: String) {
    }

    func scanDevice(central: CBCentralManager, peripheral: CBPeripheral, advertisementData: [String: Any], rssi: NSNumber) {
        print("扫描到的数据\(peripheral.name)\((advertisementData["kCBAdvDataServiceUUIDs"]))")
    }

    func error(code: Int, error: String) {
    }

    func connect(central: CBCentralManager, didConnect peripheral: CBPeripheral) {
    }

    func disconnect(central: CBCentralManager, didDisconnectPeripheral peripheral: CBPeripheral, error: Error?) {
    }

}
