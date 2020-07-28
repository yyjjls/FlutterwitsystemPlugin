package com.witsystem.top.flutterwitsystem.flutter;

//flutter wifi 配置 smartConfig
public class FlutterSmartConfig {
    private String event;

    private int code;
    private String error;
    private String bssid;
    private String address;


    public String getEvent() {
        return event;
    }


    public int getCode() {
        return code;
    }

    public String getError() {
        return error;
    }


    public String getBssid() {
        return bssid;
    }

    public String getAddress() {
        return address;
    }

    public static class Builder {
        private String event;
        private int code;
        private String error;

        private String bssid;
        private String address;

        public Builder setEvent(String event) {
            this.event = event;
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

        public Builder setBssid(String bssid) {
            this.bssid = bssid;
            return this;
        }

        public Builder setAddress(String address) {
            this.address = address;
            return this;
        }

        public FlutterSmartConfig builder() {
            FlutterSmartConfig flutterUnlock = new FlutterSmartConfig();
            flutterUnlock.event = event;
            flutterUnlock.code = code;
            flutterUnlock.error = error;
            flutterUnlock.bssid = bssid;
            flutterUnlock.address = address;
            return flutterUnlock;
        }
    }

}
