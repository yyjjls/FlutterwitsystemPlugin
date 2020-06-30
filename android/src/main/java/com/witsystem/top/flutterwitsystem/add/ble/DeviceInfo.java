package com.witsystem.top.flutterwitsystem.add.ble;

/**
 * 添加设备时候获取的基本信息
 */
public class DeviceInfo {

    ///是否是新设备
    private boolean isNewDevice = false;

    ///是否进去设置状态
    private boolean isSetup = false;

    ///固件的版本
    private String firmwareVersion = "000000";

    ///电池电量
    private int battery = 0;

    ///设备型号
    private String model = "00000000";

    ///未定义数据
    private String other = "";


    private String key = "";

    private String name = "imo智能门锁";


    public boolean isNewDevice() {
        return isNewDevice;
    }

    public void setNewDevice(boolean newDevice) {
        isNewDevice = newDevice;
    }

    public boolean isSetup() {
        return isSetup;
    }

    public void setSetup(boolean setup) {
        isSetup = setup;
    }

    public String getFirmwareVersion() {
        return firmwareVersion;
    }

    public void setFirmwareVersion(String firmwareVersion) {
        this.firmwareVersion = firmwareVersion;
    }

    public int getBattery() {
        return battery;
    }

    public void setBattery(int battery) {
        this.battery = battery;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getOther() {
        return other;
    }

    public void setOther(String other) {
        this.other = other;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
