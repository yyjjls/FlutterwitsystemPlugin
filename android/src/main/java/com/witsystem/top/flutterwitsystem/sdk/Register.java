package com.witsystem.top.flutterwitsystem.sdk;

import android.content.Context;

/**
 * 注册SDK的接口
 */
public interface Register {


    /**
     * 注册sdk 所有的功能都必须完成注册后才可以使用 注册成功返回对象
     * @param context
     * @param appId
     @param userToken
      * @return
     */
    WitsSdk witsSdkInit(Context context, String appId, String userToken);
}
