package com.witsystem.top.flutterwitsystem.device;

import java.util.ArrayList;
import java.util.List;

public class DeviceBasicInfo {

    private String bleDeviceId;

    private int bleDeviceBattery;

    private String bleDeviceName;

    private boolean bleLineState;

    private AuthInfo authInfo;


    public String getBleDeviceId() {
        return bleDeviceId;
    }

    public int getBleDeviceBattery() {
        return bleDeviceBattery;
    }

    public String getBleDeviceName() {
        return bleDeviceName;
    }

    public boolean isBleLineState() {
        return bleLineState;
    }

    public AuthInfo getAuthInfo() {
        return authInfo;
    }

    static class AuthInfo {

        private int type;

        private String startDate;

        private String endDate;

        private String repeatType;

        private String dayInfo;

        private String startTime;

        private String endTime;

        public int getType() {
            return type;
        }

        public String getStartDate() {
            return startDate;
        }

        public String getEndDate() {
            return endDate;
        }

        public String getRepeatType() {
            return repeatType;
        }

        public String getDayInfo() {
            return dayInfo;
        }

        public String getStartTime() {
            return startTime;
        }

        public String getEndTime() {
            return endTime;
        }
    }

    protected static List<DeviceBasicInfo> deviceInfoFormat(List<DeviceInfo> list) {
        if (list == null) return null;
        List<DeviceBasicInfo> deviceList = new ArrayList<>();
        for (DeviceInfo deviceInfo : list) {
            DeviceBasicInfo deviceBasicInfo = new DeviceBasicInfo();
            deviceBasicInfo.bleDeviceId = deviceInfo.getBleDeviceId();
            deviceBasicInfo.bleDeviceBattery = deviceInfo.getBleDeviceBattery();
            deviceBasicInfo.bleDeviceName = deviceInfo.getBleDeviceName();
            deviceBasicInfo.bleLineState = deviceInfo.isBleLineState();
            AuthInfo authInfo = new AuthInfo();
            deviceBasicInfo.authInfo = authInfo;
            authInfo.type =authInfo.getType();
            authInfo.startDate = authInfo.getStartDate();
            authInfo.endDate = authInfo.getEndDate();
            authInfo.repeatType = authInfo.getRepeatType();
            authInfo.dayInfo = authInfo.getDayInfo();
            authInfo.startTime = authInfo.getStartTime();
            authInfo.endTime = authInfo.getEndTime();
            deviceList.add(deviceBasicInfo);
        }
        return deviceList;
    }


}
