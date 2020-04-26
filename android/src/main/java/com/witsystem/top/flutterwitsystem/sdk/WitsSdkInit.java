package com.witsystem.top.flutterwitsystem.sdk;

import android.content.Context;
import android.util.Log;

import com.witsystem.top.flutterwitsystem.induce.Induce;
import com.witsystem.top.flutterwitsystem.induce.InduceUnlock;
import com.witsystem.top.flutterwitsystem.net.HttpsClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;


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
        Thread thread = new Thread() {
            public void run() {
                HashMap<String, Object> json = new HashMap<>();
                json.put("appId", appId);
                json.put("token", userToken);
                String https = HttpsClient.https("/device/get_device", json);
                Log.e("返回值", ">>>>>>>>>>>>>>>>>>>>>>>>" + https);
                if (https != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(https);
                        if (jsonObject.has("err") && jsonObject.getInt("err") == 0) {
                            WitsSdkInit.this.context = context;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        thread.start();
        try {
            thread.join(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return this.context == null ? null : this;
    }


    @Override
    public InduceUnlock getInduceUnlock() {
        return context == null ? null : Induce.instance(context);
    }
}
