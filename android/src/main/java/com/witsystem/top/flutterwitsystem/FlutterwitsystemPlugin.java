package com.witsystem.top.flutterwitsystem;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.witsystem.top.flutterwitsystem.add.ble.AddBleDeviceCall;
import com.witsystem.top.flutterwitsystem.flutter.FlutterAddBleDevice;
import com.witsystem.top.flutterwitsystem.flutter.FlutterSerialPort;
import com.witsystem.top.flutterwitsystem.flutter.FlutterSmartConfig;
import com.witsystem.top.flutterwitsystem.flutter.FlutterUnlock;
import com.witsystem.top.flutterwitsystem.sdk.WitsSdk;
import com.witsystem.top.flutterwitsystem.sdk.WitsSdkInit;
import com.witsystem.top.flutterwitsystem.serialport.SerialPortListen;
import com.witsystem.top.flutterwitsystem.smartconfig.SmartConfigCall;
import com.witsystem.top.flutterwitsystem.tools.ByteToString;
import com.witsystem.top.flutterwitsystem.unlock.UnlockInfo;

import java.util.List;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;

/**
 * 状态更新发送
 */
public class FlutterwitsystemPlugin implements FlutterPlugin, MethodCallHandler, UnlockInfo, SerialPortListen, AddBleDeviceCall, SmartConfigCall {
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
        channel.setMethodCallHandler(this);
        FlutterwitsystemEventPlugin.create().onAttachedToEngine(flutterPluginBinding);

    }


    public static void registerWith(Registrar registrar) {
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
            result.success(witsSdkInit != null);
        } else if (call.method.equals("openInduceUnlock")) { //开启感应开锁
            result.success(witsSdkInit.getInduceUnlock().openInduceUnlock());
        } else if (call.method.equals("getDeviceInfo")) {//获得设备信息
            result.success(witsSdkInit.getDeviceInfo());
        } else if (call.method.equals("stopInduceUnlock")) {
            result.success(witsSdkInit.getInduceUnlock().stopInduceUnlock());
        } else if (call.method.equals("isRunningInduceUnlock")) {
            result.success(witsSdkInit.getInduceUnlock().isRunningInduceUnlock());
        } else if (call.method.equals("unlock")) {
            witsSdkInit.getBleUnlock().addCallBack(this);
            result.success(call.argument("deviceId") == null ? witsSdkInit.getBleUnlock().unlock() : witsSdkInit.getBleUnlock().unlock(call.argument("deviceId")));
        } else if (call.method.equals("serialPortSendData")) {
            //Log.e("初始化", "调用串口发送数据" + call.argument("deviceId"));
            witsSdkInit.getSerialPort().addCall(this);
            result.success(witsSdkInit.getSerialPort().sendData(call.argument("deviceId"), call.argument("data")));
        } else if (call.method.equals("closeSerialPort")) {
            witsSdkInit.getSerialPort().closeSerialPort();
            result.success(true);
        } else if (call.method.equals("scanDevice")) {
            witsSdkInit.getAddBleDevice().addCall(this);
            witsSdkInit.getAddBleDevice().scanDevice();
            result.success(true);
        } else if (call.method.equals("stopDevice")) {
            witsSdkInit.getAddBleDevice().addCall(this);
            witsSdkInit.getAddBleDevice().stopDevice();
            result.success(true);
        } else if (call.method.equals("addDevice")) {
            witsSdkInit.getAddBleDevice().addCall(this);
            if (call.argument("deviceId") != null)
                witsSdkInit.getAddBleDevice().addDevice(call.argument("deviceId"));
            result.success(call.argument("deviceId") != null);
        } else if (call.method.equals("cancelAdd")) {
            witsSdkInit.getAddBleDevice().cancelAdd();
            result.success(true);
        } else if (call.method.equals("startSmartConfig")) {
            witsSdkInit.getSmartConfig().startSmartConfig(call.argument("ssid"),call.argument("bssid"),
                    call.argument("pass"),call.argument("deviceName"));
            result.success(true);
        } else if (call.method.equals("stopSmartConfig")) {
            witsSdkInit.getSmartConfig().addSmartConfigCallBack(this);
            result.success(witsSdkInit.getSmartConfig().stopSmartConfig());
        } else if (call.method.equals("isSmartConfig")) {
            witsSdkInit.getSmartConfig().addSmartConfigCallBack(this);
            result.success(witsSdkInit.getSmartConfig().isSmartConfig());
        } else if (call.method.equals("getWifiInfo")) {
            witsSdkInit.getSmartConfig().addSmartConfigCallBack(this);
            result.success(witsSdkInit.getSmartConfig().getWifiInfo());
        }
    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    }


    /*    下面是开门的回调*/
    @Override
    public void success(String deviceId, int code) {
        //Log.e("开门", "onCharacteristicWrite: 开门成功" + deviceId);
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
        //  Log.e("开门", "onCharacteristicWrite: 设备电量" + b + "%");
        FlutterUnlock flutterUnlock = new FlutterUnlock.Builder().setEvent("battery").setDeviceId(deviceId).setBattery(b).builder();
        handler.post(() -> FlutterwitsystemEventPlugin.create().sendUnlockBleEvent(gson.toJson(flutterUnlock)));

    }

    @Override
    public void devices(List<String> devices, int code) {

    }


    /* 下面是蓝牙串口的回调*/
    @Override
    public void serialPortFail(String deviceId, String error, int code) {
        //  Log.e("初始化", "发送数据serialPortFail" + code);
        FlutterSerialPort flutterSerialPort = new FlutterSerialPort.Builder().setEvent("serialPortFail").setDeviceId(deviceId).setError(error).setCode(code).builder();
        handler.post(() -> FlutterwitsystemEventPlugin.create().sendSerialPortEvent(gson.toJson(flutterSerialPort)));

    }

    @Override
    public void serialPortSuccess(String deviceId, int code) {
        //   Log.e("初始化", "发送数据serialPortSuccess" + code);
        FlutterSerialPort flutterSerialPort = new FlutterSerialPort.Builder().setEvent("serialPortSuccess").setDeviceId(deviceId).setCode(code).builder();
        handler.post(() -> FlutterwitsystemEventPlugin.create().sendSerialPortEvent(gson.toJson(flutterSerialPort)));
    }

    @Override
    public void acceptedData(String deviceId, byte[] data) {
        //  Log.e("初始化", "发送数据acceptedData" + data);
        FlutterSerialPort flutterSerialPort = new FlutterSerialPort.Builder().setEvent("acceptedData").setDeviceId(deviceId).setData(ByteToString.bytesToHexString(data)).builder();
        handler.post(() -> FlutterwitsystemEventPlugin.create().sendSerialPortEvent(gson.toJson(flutterSerialPort)));
    }


    /* 下面是添加设备的回调*/
    @Override
    public void scanDevice(String deviceId, int rssi) {
        //  Log.e("初始化", "发送数据scanDevice" + deviceId);
        FlutterAddBleDevice flutterAddBleDevice = new FlutterAddBleDevice.Builder()
                .setEvent("scanDevice")
                .setDeviceId(deviceId)
                .setCode(rssi)
                .setData(String.valueOf(rssi))
                .builder();
        handler.post(() -> FlutterwitsystemEventPlugin.create().sendAddBleEvent(gson.toJson(flutterAddBleDevice)));

    }

    @Override
    public void addProcess(String deviceId, int code) {
        // Log.e("初始化", "添加进度" + code);
        FlutterAddBleDevice flutterAddBleDevice = new FlutterAddBleDevice.Builder()
                .setEvent("addProcess")
                .setDeviceId(deviceId)
                .setCode(code)
                .builder();
        handler.post(() -> FlutterwitsystemEventPlugin.create().sendAddBleEvent(gson.toJson(flutterAddBleDevice)));
    }

    @Override
    public void error(String deviceId, String err, int code) {
        //Log.e("初始化", "添加失败" + err+":::"+code);
        FlutterAddBleDevice flutterAddBleDevice = new FlutterAddBleDevice.Builder()
                .setEvent("error")
                .setDeviceId(deviceId)
                .setCode(code)
                .setError(err)
                .builder();
        handler.post(() -> FlutterwitsystemEventPlugin.create().sendAddBleEvent(gson.toJson(flutterAddBleDevice)));
    }

    @Override
    public void addSuccess(String deviceId, int code) {
        // Log.e("初始化", "添加成功" + deviceId);
        FlutterAddBleDevice flutterAddBleDevice = new FlutterAddBleDevice.Builder()
                .setEvent("addSuccess")
                .setDeviceId(deviceId)
                .setCode(code)
                .builder();
        handler.post(() -> FlutterwitsystemEventPlugin.create().sendAddBleEvent(gson.toJson(flutterAddBleDevice)));
    }

    /* wifi smartConfig的回调*/
    @Override
    public void smartConfigFail(int code, String error) {
        FlutterSmartConfig flutterSmartConfig = new FlutterSmartConfig.Builder()
                .setEvent("smartConfigFail")
                .setCode(code)
                .setError(error)
                .builder();
        handler.post(() -> FlutterwitsystemEventPlugin.create().sendSmartConfigEvent(gson.toJson(flutterSmartConfig)));

    }

    @Override
    public void smartConfigSuccess(String bssid, String address, boolean suc) {
        FlutterSmartConfig flutterSmartConfig = new FlutterSmartConfig.Builder()
                .setEvent("smartConfigSuccess")
                .setBssid(bssid)
                .setAddress(address)
                .builder();
        handler.post(() -> FlutterwitsystemEventPlugin.create().sendSmartConfigEvent(gson.toJson(flutterSmartConfig)));

    }
}
