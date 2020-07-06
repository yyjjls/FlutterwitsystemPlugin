//
//  WitsSdkInit.swift
//  flutterwitsystem
//
//  Created by yyjjls on 2020/4/30.
//

import Foundation

class WitsSdkInit: WitsSdk, Register {

    private var appId: String? = "";
    private var token: String? = "";

    private var ble: Ble;

    private init() {
        ble = Ble.getInstance;
    }

    public static let getInstance = WitsSdkInit();


    public func witsSdkInit(appId: String?, token: String?) -> WitsSdk {
        self.appId = appId;
        self.token = token;
        DeviceManager.getInstance(appId: appId!, token: token!).getNetWorkDevice();
        return self;
    }

    func getBleLockDevice() -> String {
        return "";
    }

    func getInduceUnlock() -> Induce {
        return Induce.getInstance;
    }


}
