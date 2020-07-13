//
// Created by yyjjls on 2020/7/11.
//

import Foundation

protocol AddDevice {

    /**
    * 添加回调
    */
    func addCall(addBleDeviceCall: AddBleDeviceCall);


    /**
     * 扫描附近设备
     */
    func scanDevice();


    /**
     * 停止扫描
     */
    func stopDevice();


    /**
     * 添加指定设备
     *
     * @param deviceId
     */
    func addDevice(deviceId: String?);

    /**
     * 取消添加
     */
    func cancelAdd();
}
