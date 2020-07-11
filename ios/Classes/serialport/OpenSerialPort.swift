//
// Created by yyjjls on 2020/7/10.
//

import Foundation
import CoreBluetooth

/**
 * 开启串口只要获得了对象开启串口就成功
 */
class OpenSerialPort: NSObject, SerialPort, BleCall, CBPeripheralDelegate {

    private var peripheral: CBPeripheral?;

    private var serialPortListen: SerialPortListen?;

    private static var serialPort: SerialPort?;

    private var data: String?;

    private var deviceId: String?;

    //保存连接上的设备
    private var connectDevicesMap = [String: CBPeripheral]();

    public static func getInstance() -> SerialPort {
        if (serialPort == nil) {
            serialPort = OpenSerialPort();
        }
        return serialPort!;
    }

    func addCall(serialPortListen: SerialPortListen) {
        self.serialPortListen = serialPortListen;
    }

    func sendData(deviceId: String, data: String) -> Bool {
        if (data == "") {
            return false;
        }
        let device = DeviceManager.getInstance(appId: "", token: "").getDevice(deviceId: deviceId);
        if (device == nil) {
            return false;
        }
        self.data = data;
        self.deviceId = deviceId;
        connectDevice();
        return true;
    }

    func closeSerialPort() {

    }

    //连接设备
    private func connectDevice() {
        let peripheral = connectDevicesMap[deviceId!];
        if (peripheral == nil) {
            Ble.getInstance.scan(bleCall: self);
        } else {
            writeData(peripheral: peripheral!, data: Data.init(hex: data!));
        }
    }

    /** 《《《《《《《《《蓝牙的回调》》》》》》》》》》**/
    func belState(code: Int, msg: String) {
        //删除所有的连接设备
        if (code != BleCode.BLUE_NO) {
            connectDevicesMap.removeAll();
        }
    }

    private func stopScan() {
        Ble.getInstance.stopScan();
    }

    func scanDevice(central: CBCentralManager, peripheral: CBPeripheral, advertisementData: [String: Any], rssi: NSNumber) {
        if (peripheral.name != nil && peripheral.name == deviceId) {
            self.peripheral = peripheral;
            stopScan();
            Ble.getInstance.connect(peripheral);
        }
    }

    func error(code: Int, error: String) {
        print("异常\(code)");
        failCall(deviceId: peripheral == nil ? "" : peripheral!.name!, err: error, code: code);
        if (Ble.getInstance.getBleState() == BleCode.BLUE_NO) {
            Ble.getInstance.cancelConnection(peripheral!);
        }
    }

    func connect(central: CBCentralManager, didConnect peripheral: CBPeripheral) {
        //添加到已经连接的设备
        connectDevicesMap.updateValue(peripheral, forKey: peripheral.name!);
        //发现服务
        peripheral.delegate = self;
        peripheral.discoverServices([Ble.SERVICES]);
    }

    func disconnect(central: CBCentralManager, didDisconnectPeripheral peripheral: CBPeripheral, error: Error?) {
        // print("断开连接");
        connectDevicesMap.removeValue(forKey: peripheral.name!);
        if (error != nil) {
            failCall(deviceId: peripheral.name!, err: "Bluetooth accidental disconnect", code: BleCode.UNEXPECTED_DISCONNECT);
            Ble.getInstance.disConnect(peripheral);
            return;
        }
    }

    //发现服务
    func peripheral(_ peripheral: CBPeripheral, didDiscoverServices error: Error?) {
        if (error != nil) {
            failCall(deviceId: peripheral.name!, err: "Discover Characteristics failure", code: BleCode.GET_SERVICE_FAIL)
            Ble.getInstance.disConnect(peripheral);
            return;
        }
        peripheral.discoverCharacteristics(nil, for: peripheral.services![0])
    }

    //发现特征值
    func peripheral(_ peripheral: CBPeripheral, didDiscoverCharacteristicsFor service: CBService, error: Error?) {
        if (error != nil) {
            failCall(deviceId: peripheral.name!, err: "Discover Characteristics failure", code: BleCode.GET_CHARACTERISTIC_FAIL)
            Ble.getInstance.disConnect(peripheral);
            return;
        }
        //读取token
        peripheral.readValue(for: Ble.getInstance.getCharacteristic(services: peripheral.services![0], uuid: Ble.TOKEN)!);
    }

    //读取到的值
    func peripheral(_ peripheral: CBPeripheral, didUpdateValueFor characteristic: CBCharacteristic, error: Error?) {
        if (error != nil) {
            failCall(deviceId: peripheral.name!, err: "Failed to read data", code: BleCode.READ_DATA_FAIL);
            Ble.getInstance.disConnect(peripheral);
            return;
        }
        if (characteristic.uuid.isEqual(Ble.TOKEN)) {
            let allData = characteristic.value;
            let key = DeviceManager.getInstance(appId: "", token: "").getDevice(deviceId: peripheral.name ?? "")?.bleDeviceKey ?? "";
            let encryptedPassword128 = allData?.aesEncrypt(keyData: Data.init(hex: key), operation: kCCEncrypt)
            peripheral.writeValue(Data(hex: "02\(encryptedPassword128!.toHexString())"), for: Ble.getInstance.getCharacteristic(services: peripheral.services![0], uuid: Ble.UNLOCK)!, type: CBCharacteristicWriteType.withResponse)
        } else if (characteristic.uuid.isEqual(Ble.SERIAL_PORT_READ)) {
            print("接受到通知 数据：\(characteristic.value?.toHexString())");
            acceptedDataCall(deviceId: peripheral.name!, data: characteristic.value!);
        }
    }

    //写入值成功
    func peripheral(_ peripheral: CBPeripheral, didWriteValueFor characteristic: CBCharacteristic, error: Error?) {
        if (error != nil) {
            failCall(deviceId: peripheral.name!, err: "Failed to write data", code: BleCode.WRITE_DATA_FAIL)
            Ble.getInstance.disConnect(peripheral);
            return;
        }
        if (characteristic.uuid.isEqual(Ble.UNLOCK)) {
            //认证成功
            successCall(deviceId: peripheral.name!, code: BleCode.SERIAL_PORT_SUCCESS);
            peripheral.setNotifyValue(true, for: Ble.getInstance.getCharacteristic(services: peripheral.services![0], uuid: Ble.SERIAL_PORT_READ)!);
        } else if (characteristic.uuid.isEqual(Ble.SERIAL_PORT_WRITE)) {
            //发送数据成功
            successCall(deviceId: peripheral.name!, code: BleCode.SERIAL_PORT_SEND_DATA_SUCCESS);
        }
    }

    //通知设置成功的回调
    func peripheral(_ peripheral: CBPeripheral, didUpdateNotificationStateFor characteristic: CBCharacteristic, error: Error?) {
        if (error != nil) {
            failCall(deviceId: peripheral.name!, err: "Failed to listen for notification", code: BleCode.NOTIFICATION_DATA_FAIL)
            Ble.getInstance.disConnect(peripheral);
            return;
        }
        writeData(peripheral: peripheral, data: Data.init(hex: data!));
    }

    //发送数据
    private func writeData(peripheral: CBPeripheral, data: Data) {
        peripheral.writeValue(data, for: Ble.getInstance.getCharacteristic(services: peripheral.services![0], uuid: Ble.SERIAL_PORT_WRITE)!, type: CBCharacteristicWriteType.withResponse);
    }

    //回调错误信息
    private func failCall(deviceId: String?, err: String, code: Int) {
        if (serialPortListen == nil) {
            return;
        }

        serialPortListen!.serialPortFail(deviceId: deviceId!, error: err, code: code);
    }

    //回调成功
    private func successCall(deviceId: String, code: Int) {
        if (serialPortListen == nil) {
            return;
        }
        serialPortListen!.serialPortSuccess(deviceId: deviceId, code: code);
    }

    //回调接受到的数据
    private func acceptedDataCall(deviceId: String, data: Data) {
        if (serialPortListen == nil) {
            return;
        }
        serialPortListen!.acceptedData(deviceId: deviceId, data: data);
    }
}
