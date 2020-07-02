package com.witsystem.top.flutterwitsystem.operation;


import android.content.Context;

import com.witsystem.top.flutterwitsystem.net.HttpsClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 *
 */
public final class BleOperation implements Operation {

    private static BleOperation bleOperation;

    private String appId;

    private String userToken;

    private BleOperation(Context context) {
    }

    public static BleOperation instance(Context context, String appId, String userToken) {
        if (bleOperation == null) {
            synchronized (BleOperation.class) {
                if (bleOperation == null) {
                    bleOperation = new BleOperation(context);
                    bleOperation.appId = appId;
                    bleOperation.userToken = userToken;
                }
            }
        }
        return bleOperation;
    }

    @Override
    public boolean sendAdminKey(String deviceId, String recipient) {
        boolean sta = false;
        HashMap<String, Object> json = new HashMap<>();
        json.put("appId", appId);
        json.put("recipient", recipient);
        json.put("token", userToken);
        json.put("bleDeviceId", deviceId);
        json.put("type", 0);
        String https = HttpsClient.https("/device/ble/send_device", json);
        if (https == null) {
            return false;
        }
        try {
            JSONObject jsonObject = new JSONObject(https);
            if (jsonObject.has("err") && jsonObject.getInt("err") == 0) {
                sta = true;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return sta;
    }
}
