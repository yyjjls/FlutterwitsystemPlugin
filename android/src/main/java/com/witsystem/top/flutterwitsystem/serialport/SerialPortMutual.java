package com.witsystem.top.flutterwitsystem.serialport;

/**
 * 串口数据公有的
 */
public class SerialPortMutual extends OpenSerialPort {

    protected SerialPortMutual(SerialPort serialPort) {
        super(serialPort);
    }

    @Override
    public void addCall(SerialPortListen serialPortListen) {

    }

    @Override
    public boolean sendData(String deviceId, String data) {
        return false;
    }
}
