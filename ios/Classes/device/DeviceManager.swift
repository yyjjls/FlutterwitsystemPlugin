//
//  DeviceManager.swift
//  flutterwitsystem
//
//  Created by yyjjls on 2020/7/3.
//设备管理者

import Foundation

class DeviceManager: Device {

    private static var device: Device? = nil;
    private var appId: String?;
    private var token: String?;
    private var deviceInfoList: [DeviceInfo.Data]?;
    private var deviceNumber: Int? = 0;
    private var deviceMap = [String: DeviceInfo.Data]();

    public static func getInstance(appId: String, token: String) -> Device {
        if (device == nil) {
            device = DeviceManager();
            (device as! DeviceManager).appId = appId;
            (device as! DeviceManager).token = token;
            _ = (device as! DeviceManager).getCacheDevice();
        }
        return device!;
    }

    //从网络获得设备
    func getNetWorkDevice() -> Bool {
        let fail = "0x0001";
        let paramDic: [String: String] = ["token": token!, "appId": appId!]
        let value: String = HttpsClient.POSTAction(urlStr: "/device/get_device", param: paramDic) ?? fail;
        if (value == fail) {
            print("网络请求失败");
            return false;
        }
        saveDeviceInfo(info: value);
        return any(value: value);
    }

    /**
     * 解析函数
     **/
    private func any(value: String) -> Bool {
        let decoder = JSONDecoder();
        let data = value.data(using: String.Encoding.utf8);
        let deviceInfo = try? decoder.decode(DeviceInfo.self, from: data!);
        if (deviceInfo == nil) {
            cleanCache();
            return false;
        }
        deviceInfoList = deviceInfo?.data;
        deviceNumber = deviceInfoList?.count;
        deviceMap.removeAll();
        deviceInfoList?.forEach { data in
            deviceMap[data.bleDeviceId!] = data;
        }
        //  print("格式句化好的shu\(deviceNumber)");
        return true;
    }

    //从缓存获得设备
    func getCacheDevice() -> Bool {
        let saveDeviceInfo = getSaveDeviceInfo();
        if (saveDeviceInfo != nil) {
            return any(value: saveDeviceInfo!);
        }
        return false;
    }

    func dataInitState() -> Bool {
        return deviceInfoList != nil;
    }

    /**
    *通过id获得设备信息
    */
    func getDevice(deviceId: String) -> DeviceInfo.Data? {
        return deviceMap[deviceId];
    }


    func getDevices() -> [DeviceInfo.Data] {
        return deviceInfoList!;
    }

    func getDevicesNumber() -> Int {
        return deviceNumber!;
    }

    func getThreeDevices() -> [DeviceBasicInfo] {
        return deviceInfoFormat();
    }


    //保存设备信息
    private func saveDeviceInfo(info: String) {
        cleanCache();
        let userDefault = UserDefaults.standard;
        userDefault.set(info, forKey: appId!)
    }

    //获取保存的设备信息
    private func getSaveDeviceInfo() -> String? {
        let userDefault = UserDefaults.standard;
        return userDefault.string(forKey: appId!);
    }

    //清空缓存
    private func cleanCache() {
        let userDefault = UserDefaults.standard;
        userDefault.removeObject(forKey: appId!);
        deviceMap.removeAll();
        deviceInfoList?.removeAll();
        deviceInfoList = nil;
    }


    func deviceInfoFormat() -> [DeviceBasicInfo] {
        var list = [DeviceBasicInfo]();
        for deviceInfo in deviceInfoList! {
            var deviceBasicInfo = DeviceBasicInfo();
            deviceBasicInfo.bleDeviceId = deviceInfo.bleDeviceId;
            deviceBasicInfo.bleDeviceBattery = deviceInfo.bleDeviceBattery;
            deviceBasicInfo.bleDeviceName = deviceInfo.bleDeviceName;
            deviceBasicInfo.bleLineState = deviceInfo.bleLineState;
            var authInfo = DeviceBasicInfo.AuthInfo();
            deviceBasicInfo.authInfo = authInfo;
            authInfo.type = deviceInfo.authorityInfo.type;
            authInfo.startDate = deviceInfo.authorityInfo.startDate;
            authInfo.endDate = deviceInfo.authorityInfo.endDate;
            authInfo.repeatType = deviceInfo.authorityInfo.repeatType;
            authInfo.dayInfo = deviceInfo.authorityInfo.dayInfo;
            authInfo.startTime = deviceInfo.authorityInfo.startTime;
            authInfo.endTime = deviceInfo.authorityInfo.endTime;
            list.append(deviceBasicInfo);
        }
        return list;
    }

}
