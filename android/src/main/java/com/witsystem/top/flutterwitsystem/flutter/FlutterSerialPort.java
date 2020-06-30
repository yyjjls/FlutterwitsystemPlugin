package com.witsystem.top.flutterwitsystem.flutter;

//flutter 串口结果
public class FlutterSerialPort {
    private String event;
    private String deviceId;
    private int code;
    private String error;
    private String data;
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

    public String getData() {
        return data;
    }

    public static class Builder {
        private String event;
        private String deviceId;
        private int code;
        private String error;
        private String data;

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



        public Builder setData(String data) {
            this.data = data;
            return this;
        }

        public FlutterSerialPort builder() {
            FlutterSerialPort flutterUnlock = new FlutterSerialPort();
            flutterUnlock.event = event;
            flutterUnlock.deviceId = deviceId;
            flutterUnlock.code = code;
            flutterUnlock.error = error;
            flutterUnlock.data = data;
            return flutterUnlock;
        }
    }

}
