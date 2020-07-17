package com.witsystem.top.flutterwitsystem.device.auth;

public class AuthInfo {
    private String userUuid;
    private int type;
    private long startDate;
    private long endDate;
    private String repeatType;
    private String dayInfo;
    private String startTime;
    private String endTime;

    public String getUserUuid() {
        return userUuid;
    }

    public AuthInfo setUserUuid(String userUuid) {
        this.userUuid = userUuid;
        return this;
    }

    public int getType() {
        return type;
    }

    public AuthInfo setType(int type) {
        this.type = type;
        return this;
    }

    public long getStartDate() {
        return startDate;
    }

    public AuthInfo setStartDate(long startDate) {
        this.startDate = startDate;
        return this;
    }

    public long getEndDate() {
        return endDate;
    }

    public AuthInfo setEndDate(long endDate) {
        this.endDate = endDate;
        return this;
    }

    public String getRepeatType() {
        return repeatType;
    }

    public AuthInfo setRepeatType(String repeatType) {
        this.repeatType = repeatType;
        return this;
    }

    public String getDayInfo() {
        return dayInfo;
    }

    public AuthInfo setDayInfo(String dayInfo) {
        this.dayInfo = dayInfo;
        return this;
    }

    public String getStartTime() {
        return startTime;
    }

    public AuthInfo setStartTime(String startTime) {
        this.startTime = startTime;
        return this;
    }

    public String getEndTime() {
        return endTime;
    }

    public AuthInfo setEndTime(String endTime) {
        this.endTime = endTime;
        return this;
    }
}
