package com.witsystem.top.flutterwitsystem.operation;

/**
 * 设备的操作
 */
public interface Operation {

    /**
     * 发送管理员钥匙
     *
     * @param deviceId
     * @param recipient
     * @return
     */
    boolean sendAdminKey(String deviceId, String recipient);


}
