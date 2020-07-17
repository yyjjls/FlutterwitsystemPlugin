package com.witsystem.top.flutterwitsystem.device.auth;

//设备信息接口
public interface GetDeviceInfo {

    String getKey();

    AuthInfo getAuthInfo();

    //是否被冻结
    boolean isFreeze();
}
