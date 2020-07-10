//
// Created by yyjjls on 2020/7/10.
//

import Foundation

/**
 * 串口的回调
 */
protocol SerialPortListen {

    /**
   * 失败
   */
    func serialPortFail(deviceId: String, error: String, code: Int);

    /**
     * 成功
     */
    func serialPortSuccess(deviceId: String, code: Int);


    /**
     * 接受到的数据
     */
    func acceptedData(deviceId: String, data: Data);
}
