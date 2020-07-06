//
//  DeviceManager.swift
//  flutterwitsystem
//
//  Created by yyjjls on 2020/7/3.
//设备管理者

import Foundation

class DeviceManager: Device {

    private static var device: Device? = nil;
    private static var appId: String?;
    private static var token: String?;
    private var deviceInfoList: [DeviceInfo.Data]?;
    private var deviceNumber: Int=0;

    public static func getInstance(appId: String, token: String) -> Device {
        if (device == nil) {
            device = DeviceManager();
            self.appId = appId;
            self.token = token;
        }
        return device!;
    }

    func getNetWorkDevice() -> Bool {
        let fail = "0x0001";
        let paramDic: [String: String] = ["token": DeviceManager.token!, "appId": DeviceManager.appId!]
        let value: String = HttpsClient.POSTAction(urlStr: "/device/get_device", param: paramDic) ?? fail;
        if (value == fail) {
            print("网络请求失败")
            return false;
        }
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
            return false;
        }
        print("格式句化好的shu\(deviceInfo)");
        deviceInfoList = deviceInfo?.data;
        deviceNumber=deviceInfoList?.count as! Int;
        return true;
    }


    func getCacheDevice() -> Bool {

        return false;
    }

    func getDevice<T>(deviceId: String) -> T {

        return NSNull() as! T;
    }

    func getDevices<T>() -> T {

        return NSNull() as! T;
    }

    func getDevicesNumber() -> Int {

        return 0;
    }

    func getThreeDevices<T>() -> T {

        return NSNull() as! T;
    }


}
