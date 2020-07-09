//
// Created by yyjjls on 2020/7/9.
//

import Foundation

//开锁返回对象
struct FlutterUnlock : Codable{
    public var event: String?;
    public var deviceId: String?;
    public var code: Int?;
    public var error: String?;
    public var battery: Int?;

}
