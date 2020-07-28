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

    //添加超时
    public static final int   ADD_TIMEOUT=0x10005;

    //wifi密码错误
    public static final int   wifi_error=0x10005;

    //服务器异常
    public static final int   SERVER_EXCEPTION =0x10006;

    //基本信息错误
    public static final int BASIC_INFORMATION_ERROR =0x10007;



}
