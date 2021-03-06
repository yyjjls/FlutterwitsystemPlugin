package com.witsystem.top.flutterwitsystem.device.auth;

import android.content.Context;

import com.witsystem.top.flutterwitsystem.ble.BleCode;
import com.witsystem.top.flutterwitsystem.device.DeviceManager;

import java.util.Calendar;

/**
 * 权限管理者
 */
public class AuthManager implements Auth {
    private enum RepeatType {每天, 每周, 每月}

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
        AuthBack authBack = new AuthBack();
        GetDeviceInfo device = null;
        if (deviceId.startsWith("Slock")) {//判断是否是门锁
            device = DeviceManager.getInstance(context, "", "").getDevice(deviceId);
        } else if (deviceId.startsWith("Relay")) { //判断是否是中继

        } else if (deviceId.startsWith("Ammet")) { //判断是否是电表

        } else {
            return null;
        }
        if (device == null || device.getKey() == null || device.getKey().equals("")) {
            return authBack.setResults(false).setCode(BleCode.EXCEED_THE_TIME_LIMIT).setError("exceed the time limit").setAuthInfo(null);
        }
        AuthInfo authInfo = device.getAuthInfo();
        if (authInfo.getType() == 0) { //管理员
            return authBack.setResults(true).setCode(BleCode.AUTH_SUCCESS).setError("").setAuthInfo(authInfo);
        } else if (device.isFreeze()) {
            return authBack.setResults(false).setCode(BleCode.DEVICE_FROZEN).setError("device frozen").setAuthInfo(null);
        } else if ((DeviceManager.getInstance(context, "", "").getServiceTime() - 60000) > System.currentTimeMillis()) {//判断服务器时间和手机时间，如果手机时间小于服务器时间超过一份，出管理员外其他用户都将无法开启
            return authBack.setResults(false).setCode(BleCode.PHONE_TIME_ERROR).setError("Time error").setAuthInfo(null);
        } else if (authInfo.getType() == 1) { //普通用户
            generalUser(authInfo, authBack);
            return authBack;
        } else if (authInfo.getType() == 2) {//周期用户
            periodUser(authInfo, authBack);
            return authBack;
        } else if (authInfo.getType() == 3) {//一次性用户
            disposableUser(authInfo, authBack);
            return authBack;
        }else{
            return authBack.setResults(false).setCode(BleCode.VIEW_PERMISSIONS).setError("View permissions ").setAuthInfo(null);
        }

    }

    //验证普通用户
    private void generalUser(AuthInfo authInfo, AuthBack authBack) {
        long millis = System.currentTimeMillis();
        if (authInfo.getStartDate() > millis) {
            authBack.setResults(false).setCode(BleCode.NO_DATE_TO_USE).setError("No date to use").setAuthInfo(null);
            return;
        }
        if (authInfo.getEndDate() < millis) {
            authBack.setResults(false).setCode(BleCode.EXCEED_THE_TIME_LIMIT).setError("exceed the time limit").setAuthInfo(null);
            return;
        }
        authBack.setResults(true).setCode(BleCode.AUTH_SUCCESS).setError("").setAuthInfo(authInfo);
    }

    //一次性用户验证
    private void disposableUser(AuthInfo authInfo, AuthBack authBack) {
        generalUser(authInfo, authBack); //一次性用户也只验证日期
    }

    //周期用户
    private void periodUser(AuthInfo authInfo, AuthBack authBack) {
        generalUser(authInfo, authBack); //一周期用户验证日期
        if (!authBack.isResults()) { //判断日期是否验证通过
            return;
        }
        if (authInfo.getRepeatType().equals(RepeatType.每天.name())) {

        } else if (authInfo.getRepeatType().equals(RepeatType.每周.name())) {
            if (!authInfo.getDayInfo().contains(String.valueOf(getWeeks()))) { //判断是否今天周几能开
                authBack.setResults(false).setCode(BleCode.CURRENT_TIME_CAN_NOT_BE_TURNED_ON).setError("exceed the time limit").setAuthInfo(null);
                return;
            }
        } else if (authInfo.getRepeatType().equals(RepeatType.每月.name())) {
            if (!isMonth(authInfo)) {
                authBack.setResults(false).setCode(BleCode.CURRENT_TIME_CAN_NOT_BE_TURNED_ON).setError("exceed the time limit").setAuthInfo(null);
                return;
            }
        }
        //判断时间 如果为null 代表全天都可以
        if (authInfo.getStartTime() == null || authInfo.getEndTime() == null) {
            // authBack.setResults(false).setCode(BleCode.AUTH_INFO_ERROR).setError("exceed the time limit").setAuthInfo(null);
            return;
        }
        long todayZero2 = getTodayZero2();
        if (Long.parseLong(authInfo.getStartTime()) > todayZero2) {
            authBack.setResults(false).setCode(BleCode.NO_TIME_TO_USE).setError("No time to use").setAuthInfo(null);
        } else if (Long.parseLong(authInfo.getEndTime()) < todayZero2) {
            authBack.setResults(false).setCode(BleCode.OVER_TIME_AVAILABLE).setError("Over time available").setAuthInfo(null);
        }
    }


    //判断每月
    private boolean isMonth(AuthInfo authInfo) {
        String[] split = authInfo.getDayInfo().split(",");
        String month = String.valueOf(Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        for (String s : split) {
            if (s.equals(month)) {
                return true;
            }
        }
        return false;
    }


    //获得今天日期的毫秒数
    public static long getTodayZero2() {
        return (((System.currentTimeMillis() + (8 * 60 * 60 * 1000)) % (24 * 60 * 60 * 1000)) - (8 * 60 * 60 * 1000)); //先加8小时时间是为整除天数
    }


    //获得周几
    public int getWeeks() {
        //String[] weekDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        int[] weekDays = {7, 1, 2, 3, 4, 5, 6};
        Calendar calendar = Calendar.getInstance();
        return weekDays[calendar.get(Calendar.DAY_OF_WEEK) - 1];
    }

}
