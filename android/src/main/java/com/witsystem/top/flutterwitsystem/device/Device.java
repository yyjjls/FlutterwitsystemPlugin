package com.witsystem.top.flutterwitsystem.device;

import java.util.List;

///设备信息
public interface Device<T> {

    //从服务器获取设备
    boolean getNetWorkDevice();

    //数据初始化状态也就是结果 返回true代表设备初始化完成
    boolean dataInitState();

    //获取缓存设备，在没有网络的时候获取
    boolean getCacheDevice();

    //设备ID获得该设备的信息
    T getDevice(String deviceId);

    T getMacDevice(String mac);

    //获得所有的设备
    List<T> getDevices();

    //获得设备的个数
    int getDevicesNumber();

    List<DeviceBasicInfo> getThreeDevices();
}
