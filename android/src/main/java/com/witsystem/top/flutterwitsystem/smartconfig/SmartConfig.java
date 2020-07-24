package com.witsystem.top.flutterwitsystem.smartconfig;

/**
 * wifi 配置
 */
public interface SmartConfig {


    //开始配置
    void startSmartConfig(String ssid, String bssid, String pass);

    //当前是否在配置中
    boolean isSmartConfig();

    //停止配置
    boolean stopSmartConfig();

    //添加回调
    void addSmartConfigCallBack(SmartConfigCall smartConfigCall);


}
