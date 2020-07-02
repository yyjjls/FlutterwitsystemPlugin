package com.witsystem.top.flutterwitsystem.device;


/**
 * 设备信息
 */
public class DeviceInfo {

    private String bleDeviceId;
    private String bleDeviceModel;
    private String bleMac;
    private String bleVersion;
    private int bleDeviceBattery;
    private String bleDeviceName;
    private boolean bleLineState;
    private String bleDeviceKey;
    private AuthorityInfo authorityInfo;

    public String getBleDeviceId() {
        return bleDeviceId;
    }

    public DeviceInfo setBleDeviceId(String bleDeviceId) {
        this.bleDeviceId = bleDeviceId;
        return this;
    }

    public String getBleDeviceModel() {
        return bleDeviceModel;
    }

    public DeviceInfo setBleDeviceModel(String bleDeviceModel) {
        this.bleDeviceModel = bleDeviceModel;
        return this;
    }

    public String getBleMac() {
        return bleMac;
    }

    public DeviceInfo setBleMac(String bleMac) {
        this.bleMac = bleMac;
        return this;
    }

    public String getBleVersion() {
        return bleVersion;
    }

    public DeviceInfo setBleVersion(String bleVersion) {
        this.bleVersion = bleVersion;
        return this;
    }

    public int getBleDeviceBattery() {
        return bleDeviceBattery;
    }

    public DeviceInfo setBleDeviceBattery(int bleDeviceBattery) {
        this.bleDeviceBattery = bleDeviceBattery;
        return this;
    }

    public String getBleDeviceName() {
        return bleDeviceName;
    }

    public DeviceInfo setBleDeviceName(String bleDeviceName) {
        this.bleDeviceName = bleDeviceName;
        return this;
    }


    public boolean isBleLineState() {
        return bleLineState;
    }

    public DeviceInfo setBleLineState(boolean bleLineState) {
        this.bleLineState = bleLineState;
        return this;
    }

    public String getBleDeviceKey() {
        return bleDeviceKey;
    }

    public DeviceInfo setBleDeviceKey(String bleDeviceKey) {
        this.bleDeviceKey = bleDeviceKey;
        return this;
    }

    public AuthorityInfo getAuthorityInfo() {
        return authorityInfo;
    }

    public DeviceInfo setAuthorityInfo(AuthorityInfo authorityInfo) {
        this.authorityInfo = authorityInfo;
        return this;
    }

    static class AuthorityInfo {
        private String userUuid;
        private int type;
        private String startDate;
        private String endDate;
        private String repeatType;
        private String dayInfo;
        private String startTime;
        private String endTime;

        public String getUserUuid() {
            return userUuid;
        }

        public AuthorityInfo setUserUuid(String userUuid) {
            this.userUuid = userUuid;
            return this;
        }

        public int getType() {
            return type;
        }

        public AuthorityInfo setType(int type) {
            this.type = type;
            return this;
        }

        public String getStartDate() {
            return startDate;
        }

        public AuthorityInfo setStartDate(String startDate) {
            this.startDate = startDate;
            return this;
        }

        public String getEndDate() {
            return endDate;
        }

        public AuthorityInfo setEndDate(String endDate) {
            this.endDate = endDate;
            return this;
        }

        public String getRepeatType() {
            return repeatType;
        }

        public AuthorityInfo setRepeatType(String repeatType) {
            this.repeatType = repeatType;
            return this;
        }

        public String getDayInfo() {
            return dayInfo;
        }

        public AuthorityInfo setDayInfo(String dayInfo) {
            this.dayInfo = dayInfo;
            return this;
        }

        public String getStartTime() {
            return startTime;
        }

        public AuthorityInfo setStartTime(String startTime) {
            this.startTime = startTime;
            return this;
        }

        public String getEndTime() {
            return endTime;
        }

        public AuthorityInfo setEndTime(String endTime) {
            this.endTime = endTime;
            return this;
        }
    }

}
