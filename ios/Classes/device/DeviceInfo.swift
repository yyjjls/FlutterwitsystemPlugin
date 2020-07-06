//
// Created by yyjjls on 2020/7/4.
//

import Foundation

public struct DeviceInfo: Decodable {
    var bleDeviceId: String?;
    var bleDeviceModel: String?;
    var bleMac: String?;
    var bleVersion: String?;
    var bleDeviceBattery: Int?;
    var bleDeviceName: String?;
    var bleLineState: Bool?;
    var bleDeviceKey: String?;
    //var authorityInfo: AuthorityInfo?;

//    struct AuthorityInfo {
//        var userUuid: String?;
//        var type: Int?;
//        var startDate: String?;
//        var endDate: String?;
//        var repeatType: String?;
//        var dayInfo: String?;
//        var startTime: String?;
//        var endTime: String?;
//    }
}

