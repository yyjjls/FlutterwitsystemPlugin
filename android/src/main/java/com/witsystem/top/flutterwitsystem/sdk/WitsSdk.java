package com.witsystem.top.flutterwitsystem.sdk;


import com.witsystem.top.flutterwitsystem.device.DeviceBasicInfo;
import com.witsystem.top.flutterwitsystem.induce.InduceUnlock;
import com.witsystem.top.flutterwitsystem.operation.Operation;
import com.witsystem.top.flutterwitsystem.serialport.SerialPort;
import com.witsystem.top.flutterwitsystem.unlock.BleUnlock;

import java.util.List;

/**
 * sdk接口
 */
public interface WitsSdk {


    /**
     * 获得设备信息对象
     */
    //Device<DeviceInfo> getBleLockDevice();

    /**
     * 获得所有设备的信息
     */
    List<DeviceBasicInfo> getDeviceInfo();


    /**
     * 获得感应开锁对象
     *
     * @return
     */
    InduceUnlock getInduceUnlock();


    /**
     * 获得开锁对象 还是没有实现
     */
    BleUnlock getBleUnlock();



    /**
     * 获得设备的操作对象
     * @return
     */
    Operation getOperation();


    /**
     * 获得ble串口通信对象
     */
    SerialPort getSerialPort();

}
