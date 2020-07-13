//
// Created by yyjjls on 2020/7/11.
//

import Foundation
import CoreBluetooth

//添加蓝牙设备
class AddBleDevice: NSObject, AddDevice, BleCall, CBPeripheralDelegate {
    private var appId: String?;
    private var token: String?;
    private var deviceId: String? = "";
    private var timer: Timer?;
    //安全认证通过
    private static let SECURITY_OK: Int = 0x00;
    //安全认证失败
    private static let SECURITY_FAIL: Int = 0x01;
    //沒有进入设置状态
    private static let SECURITY_NO_SETTING_STATE: Int = 0x02;

    private var addDeviceInfo: AddDeviceInfo?

    private var addBleDeviceCall: AddBleDeviceCall?
    //保存扫描到的设备
    private var scanDevicesMap = [String: CBPeripheral]();

    private static var addDevice: AddDevice?;

    //网络认证的权限码
    private var checkCode: String?;

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
        self.deviceId = "";
        scanDevicesMap.removeAll();
        Ble.getInstance.scan(serviceUUIDs: [Ble.SCAN2], bleCall: self);
        processCall(deviceId: deviceId, code: BleCode.SCANNING);
    }

    func stopDevice() {
        Ble.getInstance.stopScan();
    }

    func addDevice(deviceId: String?) {
        if (deviceId == nil) {
            return;
        }
        let deviceInfo = scanDevicesMap[deviceId!];
        if (deviceInfo == nil) {//如果没有换成代表用户是直接指定连接添加的设备
            self.deviceId = deviceId;
            scanDevicesMap.removeAll();
            Ble.getInstance.scan(serviceUUIDs: [Ble.SCAN2], bleCall: self);
            bleTimer(timeInterval: 5, aSelector: #selector(directConnectionOverTimer))
        } else {//已经扫描完成用户直接连接指定设备
            Ble.getInstance.connect(deviceInfo!);
        }
        processCall(deviceId: deviceId, code: BleCode.CONNECTING);
    }

    //当之前输入设备id进行添加，主要是出现扫描二维码添加，定时器
    @objc private func directConnectionOverTimer() {
        cancelAdd();
        errorCall(deviceId: deviceId!, err: "Device connection timeout", code: BleCode.CONNECTION_TIMEOUT);
    }

    func cancelAdd() {
        Ble.getInstance.stopScan();
        scanDevicesMap.forEach { key, value in
            if (value.state == CBPeripheralState.connected) {
                Ble.getInstance.disConnect(value);
            } else {
                Ble.getInstance.cancelConnection(value);
            }
        }
    }

    /*》》》》》》》》》》》蓝牙的回调《《《《《《《《《《《《《《《《《*/
    func belState(code: Int, msg: String) {
        if (code == BleCode.DEVICE_BLUE_OFF) {
            closeBleTimer(); //蓝牙关闭直接关闭定时器
        }
    }

    func scanDevice(central: CBCentralManager, peripheral: CBPeripheral, advertisementData: [String: Any], rssi: NSNumber) {
        if (peripheral.name != nil) {return;}
        if (deviceId! != "" && peripheral.name! == deviceId) { //连接指定的设备
            scanDevicesMap[peripheral.name!] = peripheral;
            Ble.getInstance.stopScan();
            Ble.getInstance.connect(peripheral);
        } else if (((peripheral.name?.contains("Slock")) != nil)) { //扫描附近的设备
            scanDevicesMap[peripheral.name!] = peripheral;
            processCall(deviceId: peripheral.name!, code: BleCode.SCAN_ADD_DEVICE_INFO);
            scanDeviceCall(deviceId: peripheral.name!, rssi: Int(truncating: rssi));
            Ble.getInstance.stopScan();
            bleTimer(timeInterval: 5, aSelector: #selector(connectionOverTimer))
        }
    }

    //连接设备超时
    @objc private func connectionOverTimer() {
        cancelAdd();
        errorCall(deviceId: deviceId!, err: "Device connection timeout", code: BleCode.CONNECTION_TIMEOUT);
    }

    func error(code: Int, error: String) {
        closeBleTimer();
        errorCall(deviceId: deviceId!, err: error, code: code);
    }

    func connect(central: CBCentralManager, didConnect peripheral: CBPeripheral) {
        closeBleTimer();
        processCall(deviceId: peripheral.name!, code: BleCode.CONNECT_SUCCESS);
        peripheral.delegate = self;
        peripheral.discoverServices([Ble.SERVICES])
        processCall(deviceId: peripheral.name!, code: BleCode.SECURITY_CERTIFICATION_ONGOING);
    }


    func disconnect(central: CBCentralManager, didDisconnectPeripheral peripheral: CBPeripheral, error: Error?) {
        if (error != nil) {
            errorCall(deviceId: peripheral.name!, err: "Bluetooth accidental disconnect", code: BleCode.UNEXPECTED_DISCONNECT)
        }
    }

    //发现服务
    func peripheral(_ peripheral: CBPeripheral, didDiscoverServices error: Error?) {
        if (error != nil) {
            errorCall(deviceId: peripheral.name!, err: "Discover service failure", code: BleCode.GET_SERVICE_FAIL)
            Ble.getInstance.disConnect(peripheral);
            return;
        }
        peripheral.discoverCharacteristics(nil, for: peripheral.services![0])
    }

    //发现特征值
    func peripheral(_ peripheral: CBPeripheral, didDiscoverCharacteristicsFor service: CBService, error: Error?) {
        if (error != nil) {
            errorCall(deviceId: peripheral.name!, err: "Discover Characteristics failure", code: BleCode.GET_CHARACTERISTIC_FAIL)
            Ble.getInstance.disConnect(peripheral);
            return;
        }
        //设备电量信息等
        peripheral.readValue(for: Ble.getInstance.getCharacteristic(services: peripheral.services![0], uuid: Ble.BATTERY)!);

    }

    //读取到的值
    func peripheral(_ peripheral: CBPeripheral, didUpdateValueFor characteristic: CBCharacteristic, error: Error?) {
        if (error != nil) {
            errorCall(deviceId: peripheral.name!, err: "Failed to read data", code: BleCode.READ_DATA_FAIL)
            Ble.getInstance.disConnect(peripheral);
            return;
        }
        if (characteristic.uuid.isEqual(Ble.BATTERY)) {
            handlerFf01(peripheral: peripheral, didUpdateValueFor: characteristic);
        } else if (characteristic.uuid.isEqual(Ble.KEY)) {
            addDeviceInfo?.key = characteristic.value!.toHexString();
            processCall(deviceId: peripheral.name!, code: BleCode.ACCESS_INFORMATION_COMPLETED);
            uploadDeviceInfo(peripheral: peripheral);
        }
    }

    //通知设置成功的回调
    func peripheral(_ peripheral: CBPeripheral, didUpdateNotificationStateFor characteristic: CBCharacteristic, error: Error?) {
        if (error != nil) {
            errorCall(deviceId: peripheral.name!, err: "Failed to listen for notification", code: BleCode.NOTIFICATION_DATA_FAIL);
            Ble.getInstance.disConnect(peripheral);
            return;
        }
        // processCall(deviceId: peripheral.name!, code: BleCode.ACCESS_INFORMATION_COMPLETED);
    }

    //写入值成功
    func peripheral(_ peripheral: CBPeripheral, didWriteValueFor characteristic: CBCharacteristic, error: Error?) {
        if (error != nil) {
            errorCall(deviceId: peripheral.name!, err: "Failed to write data", code: BleCode.WRITE_DATA_FAIL)
            Ble.getInstance.disConnect(peripheral);
            return;
        }
        if (characteristic.uuid.isEqual(Ble.ADD_FINISH)) {
            Ble.getInstance.disConnect(peripheral);
            //添加成功
            processCall(deviceId: peripheral.name!, code: BleCode.ADD_SUCCESS);
            addSuccessCall(deviceId: peripheral.name!, code: BleCode.ADD_SUCCESS);
        }
    }

    /**
     * 处理读取的ff01数据
     */
    private func handlerFf01(peripheral: CBPeripheral, didUpdateValueFor characteristic: CBCharacteristic) {
        addDeviceInfo = analyze(ff01: characteristic.value!);
        let state = security(deviceId: peripheral.name!, addDeviceInfo: addDeviceInfo);
        if (state == AddBleDevice.SECURITY_FAIL) {//安全认证失败直接结束
            Ble.getInstance.disConnect(peripheral);
            return;
        } else if (state == AddBleDevice.SECURITY_NO_SETTING_STATE) { //等待进入设置状态
            peripheral.setNotifyValue(true, for: Ble.getInstance.getCharacteristic(services: peripheral.services![0], uuid: Ble.BATTERY)!);
        } else if (state == AddBleDevice.SECURITY_OK) {//安全认证通过去读取key
            processCall(deviceId: peripheral.name!, code: BleCode.ACCESS_INFORMATION);
            peripheral.readValue(for: Ble.getInstance.getCharacteristic(services: peripheral.services![0], uuid: Ble.KEY)!);

        }
    }

    /**
    * 解析发ff01的数据
    */
    private func analyze(ff01: Data) -> AddDeviceInfo? {
        if (ff01.count < 6) {
            return nil;
        }
        var addDeviceInfo = AddDeviceInfo();
        addDeviceInfo.isNewDevice = (ff01[0] == 1);
        addDeviceInfo.isSetup = (ff01[1] == 1);
        addDeviceInfo.firmwareVersion = ff01.subdata(in: 2..<5).toHexString();
        addDeviceInfo.battery = Int(ff01[6]);
        if (ff01.count <= 10) {
            return addDeviceInfo;
        }
        addDeviceInfo.model = ff01.subdata(in: 7..<11).toHexString();
        if (ff01.count >= 11) {
            addDeviceInfo.other = ff01.subdata(in: 11..<ff01.count).toHexString();
        }
        return addDeviceInfo;
    }

    /**
    * 进行安全认证
    */
    private func security(deviceId: String?, addDeviceInfo: AddDeviceInfo?) -> Int {
        if (addDeviceInfo == nil) {
            errorCall(deviceId: deviceId!, err: "Inconsistent equipment information", code: BleCode.FAILED_SECURITY_FAIL);
            return AddBleDevice.SECURITY_FAIL;
        }

        if (!addDeviceInfo!.isNewDevice) {
            errorCall(deviceId: deviceId!, err: "Device is not new.", code: BleCode.NO_NEW_DEVICE);
            return AddBleDevice.SECURITY_FAIL;
        }
        if (!addDeviceInfo!.isSetup) {
            processCall(deviceId: deviceId, code: BleCode.NO_DEVICE_SET_UP);
            return AddBleDevice.SECURITY_NO_SETTING_STATE;
        }

//        if (!NetWork.isNetworkConnected(context)) {
//            errorCall(mac, "Current network not available.", BleCode.NO_NETWORK);
//            return SECURITY_FAIL;
//        }
        //网络认证设备
        let paramDic: [String: String] = ["deviceId": deviceId!, "appId": appId!, "token": token!]
        let clientData: NSDictionary? = HttpsClient.POSTAction(urlStr: "/device/get_verify_device", param: paramDic);
        if (clientData == nil) {
            errorCall(deviceId: deviceId!, err: "Failed to get service.", code: BleCode.SERVER_EXCEPTION);
            return AddBleDevice.SECURITY_FAIL;
        }
        if (clientData!["err"] as! Int != 0) {
            errorCall(deviceId: deviceId!, err: "Server authentication failed.", code: BleCode.SERVER_VERIFY_EXCEPTION);
            return AddBleDevice.SECURITY_FAIL;
        }
        checkCode = (((((clientData!["data"] as! NSArray)[0]) as! NSDictionary)["code"]) as! String);
        processCall(deviceId: deviceId, code: BleCode.SAFETY_CERTIFICATION_COMPLETED);
        return AddBleDevice.SECURITY_OK;
    }

    /**
    * 提交设备信息到服务器
    */
    private func uploadDeviceInfo(peripheral: CBPeripheral) {
        processCall(deviceId: peripheral.name, code: BleCode.ADDITIONS_BEING_COMPLETED);
//        if (AppLocation.getLocation() != null) {
//        map.put("bleLongitude", AppLocation.getLocation().getLongitude());
//        map.put("bleLatitude", AppLocation.getLocation().getLatitude());
//        map.put("blePosition", AppLocation.getLocationAddress(context, AppLocation.getLocation()));
//        }
        let deviceName = peripheral.name!
        var hexMac = deviceName.suffix(12);
        let per = 2;
        let spaceCount = Int(hexMac.count / per)
        for i in 1..<spaceCount {
            hexMac.insert(contentsOf: ":", at: hexMac.index(hexMac.startIndex, offsetBy: per * i + i - 1))
        }
        let deviceNumber = DeviceManager.getInstance(appId: appId!, token: token!).getDevicesNumber();
        let paramDic: [String: String] = ["bleDeviceId": deviceName, "appId": appId!,
                                          "token": token!, "checkCode": checkCode!, "bleDeviceKey": addDeviceInfo!.key,
                                          "bleMac": String(hexMac), "bleDeviceModel": addDeviceInfo!.model, "bleVersion": addDeviceInfo!.firmwareVersion,
                                          "bleDeviceName": addDeviceInfo!.name + String(deviceNumber)//设备名字默认为
                                          , "bleDeviceBattery": String(addDeviceInfo!.battery)]

        let clientData: NSDictionary? = HttpsClient.POSTAction(urlStr: "/device/ble/add_ble_device", param: paramDic);
        if (clientData == nil) {
            Ble.getInstance.disConnect(peripheral);
            errorCall(deviceId: peripheral.name!, err: "Failed to get service.", code: BleCode.SERVER_EXCEPTION);
            return;
        }

        if (clientData!["err"] as! Int != 0) {
            Ble.getInstance.disConnect(peripheral);
            errorCall(deviceId: peripheral.name!, err: "Server authentication failed.", code: BleCode.SERVER_VERIFY_EXCEPTION);
            return;
        }
        sendSuccessCommand(peripheral: peripheral);
    }

    /**
        * 发送添加成功的指令
        */
    private func sendSuccessCommand(peripheral: CBPeripheral) {
        peripheral.writeValue(Data.init(hex: "0x03"), for: Ble.getInstance.getCharacteristic(services: peripheral.services![0], uuid: Ble.ADD_FINISH)!, type: CBCharacteristicWriteType.withResponse);
        processCall(deviceId: peripheral.name!, code: BleCode.ADD_FINISH);
        //errorCall(gatt.getDevice().getAddress(), "Waiting for equipment confirmation timeout", BleCode.CONFIRMATION_TIMEOUT);
    }


/**
* 回调异常
*/
    private func errorCall(deviceId: String, err: String, code: Int) {
        if (addBleDeviceCall != nil) {
            addBleDeviceCall?.error(deviceId: deviceId, err: err, code: code);
        }
    }

    /**
    * 回调进度
    */
    private func processCall(deviceId: String?, code: Int) {
        if (addBleDeviceCall != nil) {
            addBleDeviceCall?.addProcess(deviceId: deviceId, code: code);
        }
    }


    /**
    * 回调扫描到的设备
    */
    private func scanDeviceCall(deviceId: String, rssi: Int) {
        if (addBleDeviceCall != nil) {
            addBleDeviceCall?.scanDevice(deviceId: deviceId, rssi: rssi);
        }
    }


    /**
    * 添加成功的回调
    */
    private func addSuccessCall(deviceId: String, code: Int) {
        if (addBleDeviceCall != nil) {
            addBleDeviceCall?.addSuccess(deviceId: deviceId, code: code);
        }
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

}
