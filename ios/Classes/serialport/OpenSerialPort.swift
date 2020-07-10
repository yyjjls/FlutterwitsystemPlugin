//
// Created by yyjjls on 2020/7/10.
//

import Foundation
import CoreBluetooth

/**
 * 开启串口只要获得了对象开启串口就成功
 */
class OpenSerialPort: NSObject, SerialPort, BleCall, CBPeripheralDelegate {

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

    func scanDevice(central: CBCentralManager, peripheral: CBPeripheral, advertisementData: [String: Any], rssi: NSNumber) {
        if (peripheral.name != nil && peripheral.name == deviceId) {
            Ble.getInstance.connect(peripheral);
        }
    }

    func error(code: Int, error: String) {
    }

    func connect(central: CBCentralManager, didConnect peripheral: CBPeripheral) {
        //添加到已经连接的设备
        connectDevicesMap.updateValue(peripheral, forKey: peripheral.name!);
        //发现服务
        peripheral.delegate = self;
        peripheral.discoverServices([Ble.SERVICES]);
    }

    func disconnect(central: CBCentralManager, didDisconnectPeripheral peripheral: CBPeripheral, error: Error?) {

    }

    //发现服务
    func peripheral(_ peripheral: CBPeripheral, didDiscoverServices error: Error?) {
        if (error != nil) {
            //  failCall(error: "Discover service failure", code: BleCode.GET_SERVICE_FAIL)
            //ble.disConnect(peripheral);
            return;
        }
        peripheral.discoverCharacteristics(nil, for: peripheral.services![0])
    }

    //发现特征值
    func peripheral(_ peripheral: CBPeripheral, didDiscoverCharacteristicsFor service: CBService, error: Error?) {
        if (error != nil) {
            //failCall(error: "Discover Characteristics failure", code: BleCode.GET_CHARACTERISTIC_FAIL)
            //ble.disConnect(peripheral);
            return;
        }
        //读取token
        peripheral.readValue(for: Ble.getInstance.getCharacteristic(services: peripheral.services![0], uuid: Ble.TOKEN)!);
    }

    //读取到的值
    func peripheral(_ peripheral: CBPeripheral, didUpdateValueFor characteristic: CBCharacteristic, error: Error?) {
        if (error != nil) {
            //failCall(error: "Failed to read data", code: BleCode.READ_DATA_FAIL)
            //ble.disConnect(peripheral);
            return;
        }
        if (characteristic.uuid.isEqual(Ble.TOKEN)) {
            let allData = characteristic.value;
            let key = DeviceManager.getInstance(appId: "", token: "").getDevice(deviceId: peripheral.name ?? "")?.bleDeviceKey ?? "";
            let encryptedPassword128 = allData?.aesEncrypt(keyData: Data.init(hex: key), operation: kCCEncrypt)
            peripheral.writeValue(Data(hex: "02\(encryptedPassword128!.toHexString())"), for: Ble.getInstance.getCharacteristic(services: peripheral.services![0], uuid: Ble.UNLOCK)!, type: CBCharacteristicWriteType.withResponse)
        } else if (characteristic.uuid.isEqual(Ble.BATTERY)) {
            let allData = characteristic.value;

            //    print("读取出来的电量\(allData?[6])");
        }
    }

    //写入值成功
    func peripheral(_ peripheral: CBPeripheral, didWriteValueFor characteristic: CBCharacteristic, error: Error?) {
        if (error != nil) {
            //failCall(error: "Failed to write data", code: BleCode.WRITE_DATA_FAIL)
            //ble.disConnect(peripheral);
            return;
        }
        //认证成功
        if (characteristic.uuid.isEqual(Ble.UNLOCK)) {
            peripheral.setNotifyValue(true, for: characteristic);
        }

    }

    //接受到通知
    func peripheral(_ peripheral: CBPeripheral, didUpdateNotificationStateFor characteristic: CBCharacteristic, error: Error?) {
        if error != nil {
            //  println("更改通知状态错误：\(error.localizedDescription)")
        }
        print("收到的特性数据：\(characteristic.value)")
    }



//发送数据
    private func writeData(peripheral: CBPeripheral, data: Data) {
        peripheral.writeValue(data, for: Ble.getInstance.getCharacteristic(services: peripheral.services![0], uuid: Ble.SERIAL_PORT_WRITE)!, type: CBCharacteristicWriteType.withResponse);
    }

}
