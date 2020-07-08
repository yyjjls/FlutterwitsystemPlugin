//
// Created by yyjjls on 2020/7/8.
//

import Foundation

//dart  Event 通信
class FlutterwitsystemEventPlugin: NSObject, FlutterStreamHandler {

    private static let bleEvent: String? = PluginConfig.CHANNEL + "/event/ble";
    private static let unlockEvent: String? = PluginConfig.CHANNEL + "/event/unlock";
    private static let addBleEvent: String? = PluginConfig.CHANNEL + "/event/addBleDevice";
    private static let serialPortEvent: String? = PluginConfig.CHANNEL + "/event/serialPort";

    public var bleEventSink: FlutterEventSink?;
    public var unlockBleEventSink: FlutterEventSink?;
    public var addBleEventSink: FlutterEventSink?;
    public var serialPortEventSink: FlutterEventSink?;


    public func initBleEvent(registrar: FlutterPluginRegistrar) {
        let bleEventChannel = FlutterEventChannel(name: FlutterwitsystemEventPlugin.bleEvent!, binaryMessenger: registrar.messenger())
        bleEventChannel.setStreamHandler(self);
    }


    func onListen(withArguments arguments: Any?, eventSink events: @escaping FlutterEventSink) -> FlutterError? {

        print("初始化的时候\(serialPortEventSink.unsafelyUnwrapped)");
        return nil;
    }

    func onCancel(withArguments arguments: Any?) -> FlutterError? {
        return nil;
    }


/**
 * 发送蓝牙事件
 */
//    public func sendBleEvent(Object data) {
//        if (bleEventSink != null) {
//            bleEventSink.success(data);
//        } else {
//            Log.e(">>>", "没有监听者");
//        }
//    }

/**
 * 发送开门事件
 */
//    public func sendUnlockBleEvent(Object data) {
//        if (unlockBleEventSink != null) {
//            unlockBleEventSink.success(data);
//        } else {
//            Log.e(">>>", "没有监听者");
//        }
//    }
//
///**
// * 发送添加设备事件
// */
//    public func sendAddBleEvent(Object data) {
//        if (addBleEventSink != null) {
//            addBleEventSink.success(data);
//        } else {
//            Log.e(">>>", "没有监听者");
//        }
//    }
//
///**
// * 发送串口事件
// */
//    public func sendSerialPortEvent(Object data) {
//        if (serialPortEventSink != null) {
//            serialPortEventSink.success(data);
//        } else {
//            Log.e(">>>", "没有监听者");
//        }
//    }


}
