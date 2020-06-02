package com.witsystem.top.flutterwitsystem;

import android.content.Context;
import android.util.Log;


import androidx.annotation.NonNull;

import com.witsystem.top.flutterwitsystem.sdk.WitsSdk;
import com.witsystem.top.flutterwitsystem.sdk.WitsSdkInit;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;


public class FlutterwitsystemPlugin implements FlutterPlugin, MethodCallHandler {
    private static final String CHANNEL = "witsystem.top/blue";
    private static Context context;
    private WitsSdk witsSdkInit;

    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
        final MethodChannel channel = new MethodChannel(flutterPluginBinding.getFlutterEngine().getDartExecutor(), CHANNEL);
        channel.setMethodCallHandler(new FlutterwitsystemPlugin());
        context = flutterPluginBinding.getApplicationContext();
    }

    public static void registerWith(Registrar registrar) {
        final MethodChannel channel = new MethodChannel(registrar.messenger(), CHANNEL);
        channel.setMethodCallHandler(new FlutterwitsystemPlugin());
        context = registrar.activity().getApplication();
    }

    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
        if (call.method.equals("witsSdkInit")) {
            //初始化SDK成功返回true
            witsSdkInit = WitsSdkInit.getInstance().witsSdkInit(context, call.argument("appId"), call.argument("userToken"));
            result.success(witsSdkInit != null);
        } else if (call.method.equals("openInduceUnlock")) {
            //开启感应开锁
            result.success(witsSdkInit.getInduceUnlock().openInduceUnlock());
        } else if (call.method.equals("stopInduceUnlock")) {
            result.success(witsSdkInit.getInduceUnlock().stopInduceUnlock());
        } else if (call.method.equals("isRunningInduceUnlock")) {
            result.success(witsSdkInit.getInduceUnlock().isRunningInduceUnlock());
        } else if (call.method.equals("unlock")) {
           // Log.e("开门", "onCharacteristicWrite: 扫描到的设备unlock");
            result.success(witsSdkInit.getBleUnlock().unlock());
        }
    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    }

}
