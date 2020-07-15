import Foundation

///解析的设备信息
public struct DeviceBasicInfo: Decodable {
    var bleDeviceId: String?;
    var bleDeviceBattery: Int?;
    var bleDeviceName: String?;
    var bleLineState: Bool?;
    var authInfo: AuthInfo?;

    struct AuthInfo: Decodable {
        var type: Int?;
        var startDate: Int?;
        var endDate: Int?;
        var repeatType: String?;
        var dayInfo: String?;
        var startTime: Int?;
        var endTime: Int?;
    }

}
