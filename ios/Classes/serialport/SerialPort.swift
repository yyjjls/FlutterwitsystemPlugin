//
// Created by yyjjls on 2020/7/10.
//

import Foundation

/**
 * 蓝牙串口通信
 */
protocol SerialPort {

    /**
     * 添加串口的回调
     */
    func addCall(serialPortListen: SerialPortListen);


    /**
     * 发送串口数据
     */
    func sendData(deviceId: String, data: String)->Bool;


    /**
     * 关闭串口
     */
    func closeSerialPort();


}
