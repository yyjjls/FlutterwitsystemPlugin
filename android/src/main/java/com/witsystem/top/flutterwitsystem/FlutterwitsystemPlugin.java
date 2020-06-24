package com.witsystem.top.flutterwitsystem;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.witsystem.top.flutterwitsystem.sdk.WitsSdk;
import com.witsystem.top.flutterwitsystem.sdk.WitsSdkInit;
import com.witsystem.top.flutterwitsystem.unlock.UnlockInfo;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;

/**
 * 状态更新发送
 */
public class FlutterwitsystemPlugin implements FlutterPlugin, MethodCallHandler {
    private static final String CHANNEL = PluginConfig.CHANNEL + "/method";
    private static Context context;
    private WitsSdk witsSdkInit;

    private static final String bleEvent = PluginConfig.CHANNEL + "/event/ble";
    private static final String unlockEvent = PluginConfig.CHANNEL + "/event/unlock";
    private static final String addBleEvent = PluginConfig.CHANNEL + "/event/addBleDevice";

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
            witsSdkInit.getBleUnlock().addCallBack(new UnlockInfo() {
                @Override
                public void success(String deviceId, int code) {
                    Log.e("开门", "onCharacteristicWrite: 开门成功" + deviceId);
                }

                @Override
                public void fail(String error, int code) {
                    Log.e("开门", "onCharacteristicWrite: 开门失败" + code);
                }

                @Override
                public void battery(String deviceId, int b) {
                    Log.e("开门", "onCharacteristicWrite: 设备电量" + b + "%");
                }
            });
            result.success(witsSdkInit.getBleUnlock().unlock("Slock04EE033EA8CF"));
        }
    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    }

}
