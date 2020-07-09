//
// Created by yyjjls on 2020/7/8.
//

import Foundation

//dart  Event 通信
 class FlutterwitsystemEventPlugin {

    private static let bleEvent: String? = PluginConfig.CHANNEL + "/event/ble";
    private static let unlockEvent: String? = PluginConfig.CHANNEL + "/event/unlock";
    private static let addBleEvent: String? = PluginConfig.CHANNEL + "/event/addBleDevice";
    private static let serialPortEvent: String? = PluginConfig.CHANNEL + "/event/serialPort";

    public var bleEventSink: BleEventSink?;
    public var unlockBleEventSink: BleEventSink?;
    public var addBleEventSink: BleEventSink?;
    public var serialPortEventSink: BleEventSink?;

    public func register(registrar: FlutterPluginRegistrar) {
        let bleEventChannel = FlutterEventChannel(name: FlutterwitsystemEventPlugin.bleEvent!, binaryMessenger: registrar.messenger())
        bleEventSink = BleEventSink();
        bleEventChannel.setStreamHandler(bleEventSink);

        let unlockEventChannel = FlutterEventChannel(name: FlutterwitsystemEventPlugin.unlockEvent!, binaryMessenger: registrar.messenger())
        unlockBleEventSink = BleEventSink();
        unlockEventChannel.setStreamHandler(unlockBleEventSink);

        let addBleEventChannel = FlutterEventChannel(name: FlutterwitsystemEventPlugin.addBleEvent!, binaryMessenger: registrar.messenger())
        addBleEventSink = BleEventSink();
        addBleEventChannel.setStreamHandler(addBleEventSink);

        let serialPortEventChannel = FlutterEventChannel(name: FlutterwitsystemEventPlugin.serialPortEvent!, binaryMessenger: registrar.messenger())
        serialPortEventSink = BleEventSink();
        serialPortEventChannel.setStreamHandler(serialPortEventSink);
    }

/**
* 发送蓝牙事件
*/
    public func sendBleEvent(data: Any) {
        if (bleEventSink!.eventSink != nil) {
            bleEventSink!.eventSink!(data);
        } else {
            print("没有监听者>>>");
        }
    }

/**
 * 发送开门事件*/
    public func sendUnlockBleEvent(data: Any) {
        if (unlockBleEventSink!.eventSink != nil) {
            unlockBleEventSink!.eventSink!(data);
        } else {
            print("没有监听者>>>");
        }
    }

//*
// * 发送添加设备事件

    public func sendAddBleEvent(data: Any) {
        if (addBleEventSink!.eventSink != nil) {
            addBleEventSink!.eventSink!(data);
        } else {
            print("没有监听者>>>");
        }
    }

//*
// * 发送串口事件
//
    public func sendSerialPortEvent(data: Any) {
        if (serialPortEventSink!.eventSink != nil) {
            serialPortEventSink!.eventSink!(data);
        } else {
            print("没有监听者>>>");
        }
    }


}

class BleEventSink: NSObject, FlutterStreamHandler {
    public var eventSink: FlutterEventSink?;

    func onListen(withArguments arguments: Any?, eventSink events: @escaping FlutterEventSink) -> FlutterError? {
        eventSink = events;
        return nil;
    }

    func onCancel(withArguments arguments: Any?) -> FlutterError? {
        eventSink = nil;
        return nil;
    }

}


