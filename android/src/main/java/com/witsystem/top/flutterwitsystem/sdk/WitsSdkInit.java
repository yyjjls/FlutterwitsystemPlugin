package com.witsystem.top.flutterwitsystem.sdk;

import android.content.Context;

import com.witsystem.top.flutterwitsystem.device.Device;
import com.witsystem.top.flutterwitsystem.device.DeviceInfo;
import com.witsystem.top.flutterwitsystem.device.DeviceManager;
import com.witsystem.top.flutterwitsystem.induce.Induce;
import com.witsystem.top.flutterwitsystem.induce.InduceUnlock;


/**
 * SDK初始化
 */
public final class WitsSdkInit implements Register, WitsSdk {


    private Context context;

    private static Register mRegister;

    private WitsSdkInit() {
    }

    public static Register getInstance() {
        if (mRegister == null) {
            synchronized (WitsSdkInit.class) {
                if (mRegister == null) {
                    mRegister = new WitsSdkInit();
                }
            }
        }
        return mRegister;
    }


    @Override
    public WitsSdk witsSdkInit(Context context, String appId, String userToken) {
        if (appId == null || userToken == null) {
            return null;
        }
        if(DeviceManager.getInstance(context, appId, userToken).getNetWorkDevice()){
            WitsSdkInit.this.context = context;
        }
        return this.context == null ? null : this;
    }


    @Override
    public Device<DeviceInfo> getBleLockDevice() {
        return context == null ? null : DeviceManager.getInstance(null, null, null);
    }

    @Override
    public InduceUnlock getInduceUnlock() {
        return context == null ? null : Induce.instance(context);
    }
}
