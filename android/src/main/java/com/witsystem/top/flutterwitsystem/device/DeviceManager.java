package com.witsystem.top.flutterwitsystem.device;


import android.content.Context;
import android.content.SharedPreferences;

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
    private static final String DEVICE_INFO = "deviceInfo";
    private Context context;
    private static Device<DeviceInfo> device;
    private String appId;
    private String userToken;
    private List<DeviceInfo> deviceList;
    private Map<String, DeviceInfo> deviceMap;
    private Map<String, DeviceInfo> macMap;

    private DeviceManager(Context context, String appId, String userToken) {
        this.appId = appId;
        this.userToken = userToken;
        this.deviceList = new ArrayList<>();
        this.deviceMap = new HashMap<>();
        this.macMap = new HashMap<>();
        this.context = context;
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
        Thread thread = new Thread() {
            public void run() {
                HashMap<String, Object> json = new HashMap<>();
                json.put("appId", appId);
                json.put("token", userToken);
                String https = HttpsClient.https("/device/get_device", json);
                if (https != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(https);
                        if (!jsonObject.has("err") || jsonObject.getInt("err") != 0) {
                            cleanCache();
                            return;
                        }
                        if (analyzaDevice(jsonObject.getJSONArray("data"))) {
                            saveDeviceInfo(jsonObject.getJSONArray("data").toString());
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    //返回空读取缓存
                    String saveDeviceInfo = getSaveDeviceInfo();
                    if (saveDeviceInfo != null)
                        analyzaDevice(saveDeviceInfo);
                }
            }
        };
        thread.start();
        try {
            thread.join(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return deviceList.size() != 0;
    }

    @Override
    public boolean getCacheDevice() {
        String saveDeviceInfo = getSaveDeviceInfo();
        if (saveDeviceInfo != null) {
            analyzaDevice(saveDeviceInfo);
            return true;
        }

        return false;
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
                    .setBleVersion(jsonObjects.getString("bleVersion"))
                    .setBleDeviceBattery(jsonObjects.getInt("bleDeviceBattery"))
                    .setBleDeviceName(jsonObjects.getString("bleDeviceName"))
                    .setBleLineState(jsonObjects.getBoolean("bleLineState"))
                    .setBleDeviceKey(jsonObjects.getString("bleDeviceKey"))
                    .setAuthorityInfo(authorityInfo.length() == 0 ? null : new DeviceInfo.AuthorityInfo()
                            .setUserUuid(authorityInfo.getString("userUuid"))
                            .setType(authorityInfo.getInt("type"))
                            .setStartDate(authorityInfo.getString("startDate"))
                            .setEndTime(authorityInfo.getString("endDate"))
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
        mySharedPreferences.edit().putString(DEVICE_INFO, info).apply();
    }

    //获取保存的设备信息
    private String getSaveDeviceInfo() {
        SharedPreferences mySharedPreferences = context.getSharedPreferences(SAVE_DEVICE_INFO, Context.MODE_PRIVATE);
        return mySharedPreferences.getString(DEVICE_INFO, null);
    }

    //清空缓存
    private void cleanCache() {
        SharedPreferences mySharedPreferences = context.getSharedPreferences(SAVE_DEVICE_INFO, Context.MODE_PRIVATE);
        mySharedPreferences.edit().clear().apply();
        deviceList.clear();
        deviceMap.clear();
    }

}
