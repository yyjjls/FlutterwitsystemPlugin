package com.witsystem.top.flutterwitsystem.serialport;

/**
 * 开启串口只要获得了对象开启串口就成功
 */
public abstract class OpenSerialPort implements SerialPort {

    private static SerialPort serialPort;

    protected OpenSerialPort(SerialPort serialPort) {
        OpenSerialPort.serialPort = serialPort;
    }

    public static SerialPort instance() {
        return serialPort;
    }


}
