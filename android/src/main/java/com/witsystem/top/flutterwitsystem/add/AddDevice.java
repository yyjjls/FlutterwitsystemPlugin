package com.witsystem.top.flutterwitsystem.add;
import com.witsystem.top.flutterwitsystem.add.ble.AddBleDeviceCall;

/**
 * 添加设备
 */
public interface AddDevice {


    /**
     * 添加回调
     */
    void addCall(AddBleDeviceCall addBleDeviceCall);


    /**
     * 扫描附近设备
     */
    void scanDevice();


    /**
     * 停止扫描
     */
    void stopDevice();


    /**
     * 添加指定设备
     *
     * @param deviceId
     */
    void addDevice(String deviceId);

    /**
     * 取消添加
     */
    void cancelAdd();



}
