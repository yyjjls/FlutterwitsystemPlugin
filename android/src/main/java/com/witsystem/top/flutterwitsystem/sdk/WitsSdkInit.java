package com.witsystem.top.flutterwitsystem.sdk;

import android.content.Context;

import com.witsystem.top.flutterwitsystem.add.AddDevice;
import com.witsystem.top.flutterwitsystem.add.ble.AddBleDevice;
import com.witsystem.top.flutterwitsystem.device.DeviceBasicInfo;
import com.witsystem.top.flutterwitsystem.device.DeviceManager;
import com.witsystem.top.flutterwitsystem.induce.Induce;
import com.witsystem.top.flutterwitsystem.induce.InduceUnlock;
import com.witsystem.top.flutterwitsystem.location.AppLocation;
import com.witsystem.top.flutterwitsystem.operation.BleOperation;
import com.witsystem.top.flutterwitsystem.operation.Operation;
import com.witsystem.top.flutterwitsystem.serialport.OpenSerialPort;
import com.witsystem.top.flutterwitsystem.serialport.SerialPort;
import com.witsystem.top.flutterwitsystem.smartconfig.SmartConfig;
import com.witsystem.top.flutterwitsystem.smartconfig.SmartConfigManager;
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
        if (DeviceManager.getInstance(context, appId, userToken).getNetWorkDevice()) {//网络初始化结果
            this.context = context;
        }else if(DeviceManager.getInstance(context, appId, userToken).dataInitState()){//网络初始化失败在看本地初始化结果
            this.context = context;
        }
        this.appId = appId;
        this.userToken = userToken;
        if (context != null)
            AppLocation.startLocation(context);
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
        return Unlock.instance(context, appId, userToken);
    }

    @Override
    public Operation getOperation() {
        return BleOperation.instance(context, appId, userToken);
    }

    @Override
    public SerialPort getSerialPort() {
        return OpenSerialPort.instance(context);
    }

    @Override
    public AddDevice getAddBleDevice() {
        return AddBleDevice.instance(context, appId, userToken);
    }

    @Override
    public SmartConfig getSmartConfig() {
        return SmartConfigManager.getInstance(context,appId, userToken);
    }
}
