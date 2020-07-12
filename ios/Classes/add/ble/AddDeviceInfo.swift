//
// Created by yyjjls on 2020/7/12.
//

import Foundation

/**
 * 添加设备时候获取的基本信息
 */
struct AddDeviceInfo {
    ///是否是新设备
    public var isNewDevice: Bool = false;

    ///是否进去设置状态
    public var isSetup: Bool = false;

    ///固件的版本
    public var firmwareVersion: String = "000000";

    ///电池电量
    public var battery: Int = 0;

    ///设备型号
    public var model: String = "00000000";

    ///未定义数据
    public var other: String = "";

    public var key: String = "";

    public var name: String = "imo智能门锁";


}
