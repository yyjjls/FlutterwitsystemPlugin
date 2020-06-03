package com.witsystem.top.flutterwitsystem.sdk;

import android.content.Context;

import com.witsystem.top.flutterwitsystem.device.DeviceBasicInfo;
import com.witsystem.top.flutterwitsystem.device.DeviceManager;
import com.witsystem.top.flutterwitsystem.induce.Induce;
import com.witsystem.top.flutterwitsystem.induce.InduceUnlock;
import com.witsystem.top.flutterwitsystem.operation.BleOperation;
import com.witsystem.top.flutterwitsystem.operation.Operation;
import com.witsystem.top.flutterwitsystem.unlock.BleUnlock;
import com.witsystem.top.flutterwitsystem.unlock.Unlock;

import java.util.List;


/**
 * SDK初始化
 */
public final class WitsSdkInit implements Register, WitsSdk {


    private Context context;

    private static Register mRegister;

    private String appId;

    private String userToken;

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
        if (DeviceManager.getInstance(context, appId, userToken).getNetWorkDevice()) {
            WitsSdkInit.this.context = context;
        }
        this.appId = appId;
        this.userToken = userToken;
        return this.context == null ? null : this;
    }


    @Override
    public List<DeviceBasicInfo> getDeviceInfo() {
        return DeviceManager.getInstance(context, null, null).getThreeDevices();
    }

    @Override
    public InduceUnlock getInduceUnlock() {
        return context == null ? null : Induce.instance(context);
    }

    @Override
    public BleUnlock getBleUnlock() {
        return Unlock.instance(context);
    }

    @Override
    public Operation getOperation() {
        return BleOperation.instance(context, appId, userToken);
    }
}
