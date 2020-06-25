package com.witsystem.top.flutterwitsystem.serialport;

/**
 * 串口的回调
 */
public interface SerialPortListen {


    /**
     * 失败
     */
    void fail(String deviceId, String error, int code);

    /**
     * 成功
     */
    void success(String deviceId, int code);


    /**
     * 接受到的数据
     */
    void acceptedData(String deviceId, byte[] data);


}
