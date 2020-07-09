package com.witsystem.top.flutterwitsystem.unlock;


import java.util.List;

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
     * @param battery
     */
    void battery(String deviceId, int battery);


    /**
     * 发现附近多个设备的时候回调，只有在开启附近设备才有可能回调
     * @param devices
     * @param code
     */
    void devices(List<String> devices, int code);


}
