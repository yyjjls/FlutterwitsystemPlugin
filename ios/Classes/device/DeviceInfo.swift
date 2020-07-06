//
// Created by yyjjls on 2020/7/4.
//

import Foundation

///解析的设备信息
public struct DeviceInfo: Decodable {

    var err: Int?;
    var serviceTime: Int?;
    var data: [Data];

    struct Data: Decodable {
        var bleDeviceId: String?;
        var bleDeviceModel: String?;
        var bleMac: String?;
        var bleVersion: String?;
        var bleDeviceBattery: Int?;
        var bleDeviceName: String?;
        var bleLineState: Bool?;
        var bleDeviceKey: String?;
        var authorityInfo: AuthorityInfo;

        struct AuthorityInfo: Decodable {
            var userUuid: String?;
            var type: Int?;
            var startDate: Int?;
            var endDate: Int?;
            var repeatType: String?;
            var dayInfo: String?;
            var startTime: Int?;
            var endTime: Int?;
        }
    }

}


