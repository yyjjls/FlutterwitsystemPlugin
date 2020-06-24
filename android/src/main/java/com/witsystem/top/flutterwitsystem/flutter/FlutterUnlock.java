package com.witsystem.top.flutterwitsystem.flutter;

//flutter 开门结果返回
public class FlutterUnlock {
    private String event;
    private String deviceId;
    private int code;
    private String error;
    private int battery;

    public String getEvent() {
        return event;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public int getCode() {
        return code;
    }

    public String getError() {
        return error;
    }

    public int getBattery() {
        return battery;
    }

    public static class Builder {
        private String event;
        private String deviceId;
        private int code;
        private String error;
        private int battery;

        public Builder setEvent(String event) {
            this.event = event;
            return this;
        }

        public Builder setDeviceId(String deviceId) {
            this.deviceId = deviceId;
            return this;
        }

        public Builder setCode(int code) {
            this.code = code;
            return this;
        }

        public Builder setError(String error) {
            this.error = error;
            return this;
        }

        public Builder setBattery(int battery) {
            this.battery = battery;
            return this;
        }

        public FlutterUnlock builder() {
            FlutterUnlock flutterUnlock = new FlutterUnlock();
            flutterUnlock.event = event;
            flutterUnlock.deviceId = deviceId;
            flutterUnlock.code = code;
            flutterUnlock.error = error;
            flutterUnlock.battery = battery;
            return flutterUnlock;
        }
    }

}
