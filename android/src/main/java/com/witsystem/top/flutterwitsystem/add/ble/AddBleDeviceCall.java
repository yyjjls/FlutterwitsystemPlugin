package com.witsystem.top.flutterwitsystem.add.ble;

/**
 * 添加设备的回调
 */
public interface AddBleDeviceCall {


    /**
     * 扫描到设备的回调
     *
     * @param deviceId
     * @param rssi
     */
    void scanDevice(String deviceId, int rssi);


    /**
     * 添加进度回调，不是百分比，只是当前进行到那一笔的回调码
     *
     * @param code
     */
    void addProcess(String deviceId,int code);


    /**
     * 异常信息的回调
     */
    void error(String deviceId,String err, int code);


    /**
     * 添加成功的回调
     */
    void addSuccess(String deviceId, int code);


}
