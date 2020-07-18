//
// Created by yyjjls on 2020/7/18.
//

import Foundation

public class IosWitSDkInit: NSObject {
    public override init() {
        super.init()
    }

    public func getWitsSdk() -> Register? {
        return WitsSdkInit.getInstance();
    }

    public func getWitsSdk(appId: String?, token: String?) -> WitsSdk? {
        return getWitsSdk()?.witsSdkInit(appId: appId, token: token);
    }

}
