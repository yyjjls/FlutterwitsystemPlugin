package com.witsystem.top.flutterwitsystem.device;


import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.witsystem.top.flutterwitsystem.device.auth.AuthInfo;
import com.witsystem.top.flutterwitsystem.net.HttpsClient;
import com.witsystem.top.flutterwitsystem.sdk.WitsSdkInit;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 设备管理
 */
public final class DeviceManager implements Device<DeviceInfo> {

    private static final String SAVE_DEVICE_INFO = "witsystem.top.deviceInfo";
    private Context context;
    private static Device<DeviceInfo> device;
    private String appId;
    private String userToken;
    private List<DeviceInfo> deviceList;
    private Map<String, DeviceInfo> deviceMap;
    private Map<String, DeviceInfo> macMap;
    private boolean cacheDataInitState = false; //缓存数据初始化结果
    private long serviceTime; //更新设备时候服务器时间


    private boolean state = true;

    private DeviceManager(Context context, String appId, String userToken) {
        this.appId = appId;
        this.userToken = userToken;
        this.deviceList = new ArrayList<>();
        this.deviceMap = new HashMap<>();
        this.macMap = new HashMap<>();
        this.context = context;
        cacheDataInitState = getCacheDevice();
    }

    public static Device<DeviceInfo> getInstance(Context context, String appId, String userToken) {
        if (device == null) {
            synchronized (WitsSdkInit.class) {
                if (device == null) {
                    device = new DeviceManager(context, appId, userToken);
                }
            }
        }
        return device;
    }


    @Override
    public boolean getNetWorkDevice() {
        state = true;
        Thread thread = new Thread() {
            public void run() {
                HashMap<String, Object> json = new HashMap<>();
                json.put("appId", appId);
                json.put("token", userToken);
                String https = HttpsClient.https("/device/get_device", json);
                if (https == null) {
                    state = false;
                    return;
                }
                try {
                    JSONObject jsonObject = new JSONObject(https);
                    if (!jsonObject.has("err") || jsonObject.getInt("err") != 0) {
                        cleanCache();
                        state = false;
                        cacheDataInitState = false;
                        return;
                    }
                    if (jsonObject.has("serviceTime")) {
                        serviceTime = jsonObject.getLong("serviceTime");
                    }
                    if (analyzaDevice(jsonObject.getJSONArray("data"))) {
                        saveDeviceInfo(jsonObject.toString());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    state = false;
                }

            }
        };
        thread.start();
        try {
            thread.join(6000);
        } catch (InterruptedException e) {
            e.printStackTrace();
            state = false;
        }
        return state;
    }

    @Override
    public boolean dataInitState() {
        return cacheDataInitState;
    }

    @Override
    public boolean getCacheDevice() {
        String saveDeviceInfo = getSaveDeviceInfo();
        if (saveDeviceInfo == null) {
            return false;
        }
        try {
            JSONObject jsonObject = new JSONObject(saveDeviceInfo);
            if (jsonObject.has("serviceTime")) {
                serviceTime = jsonObject.getLong("serviceTime");
            }
            analyzaDevice(jsonObject.getJSONArray("data"));
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


    @Override
    public DeviceInfo getDevice(String deviceId) {
        return deviceMap.get(deviceId);
    }

    @Override
    public DeviceInfo getMacDevice(String mac) {
        return macMap.get(mac);
    }

    @Override
    public List<DeviceInfo> getDevices() {
        return deviceList;
    }

    @Override
    public int getDevicesNumber() {
        return deviceList.size();
    }

    @Override
    public List<DeviceBasicInfo> getThreeDevices() {
        return DeviceBasicInfo.deviceInfoFormat(deviceList);
    }

    @Override
    public long getServiceTime() {
        return serviceTime;
    }


    //解析设备信息
    private boolean analyzaDevice(String str) {
        try {
            JSONArray jsonArray = new JSONArray(str);
            return analyzaDevice(jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean analyzaDevice(JSONArray jsonArray) throws JSONException {
        JSONObject jsonObjects;
        JSONObject authorityInfo;
        deviceMap.clear();
        macMap.clear();
        deviceList.clear();
        for (int i = 0; i < jsonArray.length(); i++) {
            jsonObjects = jsonArray.getJSONObject(i);
            authorityInfo = jsonObjects.getJSONObject("authorityInfo");
            DeviceInfo deviceInfo = new DeviceInfo()
                    .setBleDeviceId(jsonObjects.getString("bleDeviceId"))
                    .setBleDeviceModel(jsonObjects.getString("bleDeviceModel"))
                    .setBleMac(jsonObjects.getString("bleMac"))
                    .setFreeze(jsonObjects.getBoolean("isFreeze"))
                    .setBleVersion(jsonObjects.getString("bleVersion"))
                    .setBleDeviceBattery(jsonObjects.getInt("bleDeviceBattery"))
                    .setBleDeviceName(jsonObjects.getString("bleDeviceName"))
                    .setBleLineState(jsonObjects.getBoolean("bleLineState"))
                    .setBleDeviceKey(jsonObjects.getString("bleDeviceKey"))
                    .setAuthInfo(authorityInfo.length() == 0 ? null : new AuthInfo()
                            .setUserUuid(authorityInfo.getString("userUuid"))
                            .setType(authorityInfo.getInt("type"))
                            .setStartDate(authorityInfo.getLong("startDate"))
                            .setEndDate(authorityInfo.getLong("endDate"))
                            .setRepeatType(authorityInfo.getString("repeatType"))
                            .setDayInfo(authorityInfo.getString("dayInfo"))
                            .setStartTime(authorityInfo.getString("startTime"))
                            .setEndTime(authorityInfo.getString("endTime")));
            deviceList.add(deviceInfo);
            deviceMap.put(deviceInfo.getBleDeviceId(), deviceInfo);
            macMap.put(deviceInfo.getBleMac(), deviceInfo);
        }
        return true;
    }

    //保存设备信息
    private void saveDeviceInfo(String info) {
        SharedPreferences mySharedPreferences = context.getSharedPreferences(SAVE_DEVICE_INFO, Context.MODE_PRIVATE);
        mySharedPreferences.edit().putString(appId, info).apply();
    }

    //获取保存的设备信息
    private String getSaveDeviceInfo() {
        SharedPreferences mySharedPreferences = context.getSharedPreferences(SAVE_DEVICE_INFO, Context.MODE_PRIVATE);
        return mySharedPreferences.getString(appId, null);
    }

    //清空缓存
    private void cleanCache() {
        SharedPreferences mySharedPreferences = context.getSharedPreferences(SAVE_DEVICE_INFO, Context.MODE_PRIVATE);
        mySharedPreferences.edit().clear().apply();
        deviceList.clear();
        deviceMap.clear();
    }

}
