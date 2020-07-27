package com.witsystem.top.flutterwitsystem.smartconfig;

//配置结果回调

public interface SmartConfigCall {

    //失败的回调
    void smartConfigFail(int code, String error);

    //成功的回调
    void smartConfigSuccess(String bssid, String address, boolean suc, boolean cancel);


}
