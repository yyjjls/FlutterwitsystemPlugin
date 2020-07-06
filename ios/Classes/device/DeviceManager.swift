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
        let vv: String = HttpsClient.POSTAction(urlStr: "/device/get_device", param: paramDic) ?? fail;
        if (vv == fail) {
            print("网络请求失败")
            return false;
        }
        print("网络请求失败\(vv)");
        let decoder = JSONDecoder();
        let data = vv.data(using: String.Encoding.utf8);
        let d = try? decoder.decode(DeviceInfo.self, from: data!)
        print("网络请求失败\(d)");
        return false;

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
