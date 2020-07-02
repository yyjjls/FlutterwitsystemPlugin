package com.witsystem.top.flutterwitsystem.unlock;


/**
 * 开锁的回调
 */
public interface UnlockInfo {

    /**
     * 成功的回调
     *
     * @param deviceId
     * @param code
     */
    void success(String deviceId, int code);

    /**
     * 失败的回调
     *
     * @param error
     * @param code
     */
    void fail(String error, int code);

    /**
     * 电量的回调
     *
     * @param b
     */
    void battery(String deviceId,int b);


}
