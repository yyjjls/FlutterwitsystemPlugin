package com.witsystem.top.flutterwitsystem.smartconfig;

/**
 * smartconfig配置错误码
 */
public class SmartConfigCode {

    //wifi 为连接
    public static final int  WIFI_DISCONNECT=0x10001;

    //当前wifi连接的是5G 不支持
    public static final int  WIFI_CONNECT_5G=0x10002;

    //缺少必要权限
    public static final int  PERMISSION_GRANTED=0x10003;

    //没有定位信息无法获得wifi信息
    public static final int  LOCATION_REQUIREMENT=0x10004;



}
