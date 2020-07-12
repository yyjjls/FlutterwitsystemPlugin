//
// Created by yyjjls on 2020/7/11.
//

import Foundation

/**
 * 添加设备的回调
 */
protocol AddBleDeviceCall {

    /**
        * 扫描到设备的回调
        *
        */
    func scanDevice(deviceId: String?, rssi: Int);


    /**
     * 添加进度回调，不是百分比，只是当前进行到那一笔的回调码
     */
    func addProcess(deviceId: String?, code: Int);


    /**
     * 异常信息的回调
     */
    func error(deviceId: String?, err: String, code: Int);


    /**
     * 添加成功的回调
     */
    func addSuccess(deviceId: String?, code: Int);

}
