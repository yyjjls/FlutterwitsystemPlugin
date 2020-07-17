package com.witsystem.top.flutterwitsystem.device.auth;

//权限验证反馈
public class AuthBack {
    //验证结果
    public boolean results;
    //状态码
    public int code;
    //错误的时候异常说明
    public String error;
    //设备权限
    public AuthInfo authInfo;


    public boolean isResults() {
        return results;
    }

    public AuthBack setResults(boolean results) {
        this.results = results;
        return this;
    }

    public int getCode() {
        return code;
    }

    public AuthBack setCode(int code) {
        this.code = code;
        return this;
    }

    public String getError() {
        return error;
    }

    public AuthBack setError(String error) {
        this.error = error;
        return this;
    }

    public AuthInfo getAuthInfo() {
        return authInfo;
    }

    public AuthBack setAuthInfo(AuthInfo authInfo) {
        this.authInfo = authInfo;
        return this;
    }
}
