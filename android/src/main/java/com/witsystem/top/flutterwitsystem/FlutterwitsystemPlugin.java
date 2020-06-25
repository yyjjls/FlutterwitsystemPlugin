package com.witsystem.top.flutterwitsystem;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.witsystem.top.flutterwitsystem.flutter.FlutterUnlock;
import com.witsystem.top.flutterwitsystem.sdk.WitsSdk;
import com.witsystem.top.flutterwitsystem.sdk.WitsSdkInit;
import com.witsystem.top.flutterwitsystem.serialport.SerialPortListen;
import com.witsystem.top.flutterwitsystem.unlock.UnlockInfo;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;

/**
 * 状态更新发送
 */
public class FlutterwitsystemPlugin implements FlutterPlugin, MethodCallHandler, UnlockInfo, SerialPortListen {
    private static final String CHANNEL = PluginConfig.CHANNEL + "/method";
    private static Context context;
    private WitsSdk witsSdkInit;
    private Gson gson = new Gson();
    private Handler handler = new Handler();

    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
        if (context != null) {
            return;
        }
        context = flutterPluginBinding.getApplicationContext();
        final MethodChannel channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), CHANNEL);
        channel.setMethodCallHandler(new FlutterwitsystemPlugin());
        FlutterwitsystemEventPlugin.create().onAttachedToEngine(flutterPluginBinding);

    }


    public void registerWith(Registrar registrar) {
        if (context != null) {
            return;
        }
        context = registrar.activity().getApplication();
        final MethodChannel channel = new MethodChannel(registrar.messenger(), CHANNEL);
        channel.setMethodCallHandler(new FlutterwitsystemPlugin());
        FlutterwitsystemEventPlugin.create().registerWith(registrar);
    }

    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {

        if (call.method.equals("witsSdkInit")) {
            //初始化SDK成功返回true
            witsSdkInit = WitsSdkInit.getInstance().witsSdkInit(context, call.argument("appId"), call.argument("userToken"));
            Log.e("初始化", "初始化结果" + witsSdkInit);
            result.success(witsSdkInit != null);
        } else if (call.method.equals("openInduceUnlock")) {
            //开启感应开锁
            result.success(witsSdkInit.getInduceUnlock().openInduceUnlock());
        } else if (call.method.equals("stopInduceUnlock")) {
            result.success(witsSdkInit.getInduceUnlock().stopInduceUnlock());
        } else if (call.method.equals("isRunningInduceUnlock")) {
            result.success(witsSdkInit.getInduceUnlock().isRunningInduceUnlock());
        } else if (call.method.equals("unlock")) {
            witsSdkInit.getBleUnlock().addCallBack(this);
            //result.success(witsSdkInit.getBleUnlock().unlock("Slock04EE033EA882"));
            result.success(witsSdkInit.getBleUnlock().unlock());
        } else if (call.method.equals("serialPortSendData")) {
            //Log.e("初始化", "调用串口发送数据" + call.argument("deviceId"));
            witsSdkInit.getSerialPort().addCall(this);
            result.success(witsSdkInit.getSerialPort().sendData(call.argument("deviceId"), call.argument("data")));
        }
    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    }


    /*    下面是开门的回调*/
    @Override
    public void success(String deviceId, int code) {
        Log.e("开门", "onCharacteristicWrite: 开门成功" + deviceId);
        FlutterUnlock flutterUnlock = new FlutterUnlock.Builder().setEvent("success").setDeviceId(deviceId).setCode(code).builder();
        handler.post(() -> FlutterwitsystemEventPlugin.create().sendUnlockBleEvent(gson.toJson(flutterUnlock)));
    }

    @Override
    public void fail(String error, int code) {
        Log.e("开门", "onCharacteristicWrite: 开门失败" + code);
        FlutterUnlock flutterUnlock = new FlutterUnlock.Builder().setEvent("fail").setError(error).setCode(code).builder();
        handler.post(() -> FlutterwitsystemEventPlugin.create().sendUnlockBleEvent(gson.toJson(flutterUnlock)));

    }

    @Override
    public void battery(String deviceId, int b) {
        Log.e("开门", "onCharacteristicWrite: 设备电量" + b + "%");
        FlutterUnlock flutterUnlock = new FlutterUnlock.Builder().setEvent("battery").setDeviceId(deviceId).setBattery(b).builder();
        handler.post(() -> FlutterwitsystemEventPlugin.create().sendUnlockBleEvent(gson.toJson(flutterUnlock)));

    }


    /* 下面是蓝牙串口的回调*/
    @Override
    public void serialPortFail(String deviceId, String error, int code) {
        //Log.e("初始化", "发送数据serialPortFail" + code);
        FlutterUnlock flutterUnlock = new FlutterUnlock.Builder().setEvent("serialPortFail").setDeviceId(deviceId).setError(error).setCode(code).builder();
        handler.post(() -> FlutterwitsystemEventPlugin.create().sendSerialPortEvent(gson.toJson(flutterUnlock)));

    }

    @Override
    public void serialPortSuccess(String deviceId, int code) {
      //  Log.e("初始化", "发送数据serialPortSuccess" + code);
        FlutterUnlock flutterUnlock = new FlutterUnlock.Builder().setEvent("serialPortSuccess").setDeviceId(deviceId).setCode(code).builder();
        handler.post(() -> FlutterwitsystemEventPlugin.create().sendSerialPortEvent(gson.toJson(flutterUnlock)));
    }

    @Override
    public void acceptedData(String deviceId, byte[] data) {
      //  Log.e("初始化", "发送数据acceptedData" + data);
        FlutterUnlock flutterUnlock = new FlutterUnlock.Builder().setEvent("acceptedData").setDeviceId(deviceId).setData(data).builder();
        handler.post(() -> FlutterwitsystemEventPlugin.create().sendSerialPortEvent(gson.toJson(flutterUnlock)));
    }
}
