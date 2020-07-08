//
//
//  Created by yyjjls on 2020/4/30.
//

import Foundation

class WitsSdkInit: WitsSdk, Register {

    private var appId: String? = "";
    private var token: String? = "";
    private static var register: Register? = nil;
    private var ble: Ble;

    private init() {
        ble = Ble.getInstance;
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
        if (!state) {
            return nil
        }
        return self;
    }

    func getBleLockDevice() -> String {
        return "";
    }

    func getInduceUnlock() -> Induce {
        return Induce.getInstance;
    }

    func getBleUnlock() -> BleUnlock {
        return Unlock.getInstance(appId: appId, token: token);
    }
}
