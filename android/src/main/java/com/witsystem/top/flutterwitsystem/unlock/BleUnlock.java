package com.witsystem.top.flutterwitsystem.unlock;

/**
 * 蓝牙开锁
 */
public interface BleUnlock {

    /**
     * 开启附近的设备
     */
    boolean unlock();

    /**
     * 开启指定的设备
     *
     * @param deviceId
     */
    boolean unlock(String deviceId);


}
