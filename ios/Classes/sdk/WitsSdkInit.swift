//
//
//  Created by yyjjls on 2020/4/30.
//

import Foundation

public class WitsSdkInit: NSObject, WitsSdk, Register {

    private var appId: String? = "";
    private var token: String? = "";
    private static var register: Register? = nil;
    private var ble: Ble;

     override init() {
        ble = Ble.getInstance;
        Induce.getInstance;
    }

    public static func getInstance() -> Register {
        if (register == nil) {
            register = WitsSdkInit();
        }
        return register!;
    }

    public func witsSdkInit(appId: String?, token: String?) -> WitsSdk? {
        self.appId = appId;
        self.token = token;
        let state = DeviceManager.getInstance(appId: appId!, token: token!).getNetWorkDevice();
        // 启动定位
        AppLocation.getInstance.startLocation();
        if (state) {
            return self
        } else if (DeviceManager.getInstance(appId: appId!, token: token!).dataInitState()) {
            return self;
        }
        return nil;
    }

    public func getDeviceInfo() -> [DeviceBasicInfo] {
        return DeviceManager.getInstance(appId: appId!, token: token!).getThreeDevices();
    }


    public func getInduceUnlock() -> Induce {
        return Induce.getInstance;
    }

    public func getBleUnlock() -> BleUnlock {
        return Unlock.getInstance(appId: appId, token: token);
    }


    public func getSerialPort() -> SerialPort {
        return OpenSerialPort.getInstance();
    }

    public func getAddBleDevice() -> AddDevice {
        return AddBleDevice.getInstance(appId: appId, token: token);
    }
}
