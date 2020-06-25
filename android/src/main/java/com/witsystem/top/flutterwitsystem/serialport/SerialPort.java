package com.witsystem.top.flutterwitsystem.serialport;

/**
 * 蓝牙串口通信
 */
public interface SerialPort {

    /**
     * 添加串口的回调
     */
    void addCall(SerialPortListen serialPortListen);


    /**
     * 发送串口数据
     */
    boolean sendData(String deviceId, String data);


    /**
     * 关闭串口
     */
    void closeSerialPort();


}
