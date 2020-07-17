package com.witsystem.top.flutterwitsystem.device.auth;

import android.content.Context;

import com.witsystem.top.flutterwitsystem.ble.BleCode;
import com.witsystem.top.flutterwitsystem.device.DeviceInfo;
import com.witsystem.top.flutterwitsystem.device.DeviceManager;

/**
 * 权限管理者
 */
public class AuthManager implements Auth {

    private static Auth auth;
    private Context context;

    private AuthManager(Context context) {
        this.context = context;
    }


    public static Auth getInstance(Context context) {
        if (auth == null) {
            auth = new AuthManager(context);
        }
        return auth;
    }


    @Override
    public AuthBack isAuth(String deviceId) {
        if (deviceId == null || deviceId.equals("")) {
            return null;
        }
        GetDeviceInfo device = null;
        if (deviceId.startsWith("Slock")) {//判断是否是门锁
            device = DeviceManager.getInstance(context, "", "").getDevice(deviceId);
        } else if (deviceId.startsWith("Relay")) { //判断是否是中继

        } else if (deviceId.startsWith("Ammet")) { //判断是否是电表

        } else {
            return null;
        }
        if (device == null || device.getKey() == null || device.getKey().equals("")) {
            return null;
        }
        AuthInfo authInfo = device.getAuthInfo();
        if (authInfo == null) {
            return null;
        }
        AuthBack authBack = new AuthBack();
        if (authInfo.getType() == 0) {
            return authBack.setResults(true).setCode(BleCode.AUTH_SUCCESS).setError("").setAuthInfo(authInfo);
        }


        return null;
    }
}
