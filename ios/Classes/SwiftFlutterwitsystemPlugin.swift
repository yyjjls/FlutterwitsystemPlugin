import Flutter
import UIKit

public class SwiftFlutterwitsystemPlugin: NSObject, FlutterPlugin, UnlockInfo, SerialPortListen {


    private var witsSdk: WitsSdk?;
    private var eventPlugin: FlutterwitsystemEventPlugin?;
    private let encoder = JSONEncoder();

    public static func register(with registrar: FlutterPluginRegistrar) {
        let channel = FlutterMethodChannel(name: "witsystem.top/blue/method", binaryMessenger: registrar.messenger());
        let instance = SwiftFlutterwitsystemPlugin();
        registrar.addMethodCallDelegate(instance, channel: channel);
        instance.eventPlugin = FlutterwitsystemEventPlugin();
        instance.eventPlugin!.register(registrar: registrar);
        instance.encoder.outputFormatting = .prettyPrinted // 输出格式
    }

    public func handle(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
        if (call.method == "witsSdkInit") {
            //初始化SDK成功返回true
            let args: NSDictionary = call.arguments as! NSDictionary
            let appId: String = args["appId"] as! String;
            let token: String = args["userToken"] as! String;
            witsSdk = WitsSdkInit.getInstance().witsSdkInit(appId: appId, token: token);
            result(witsSdk != nil);
        } else if (call.method == "openInduceUnlock") {
            //开启感应开锁
            result(witsSdk?.getInduceUnlock().openInduceUnlock());
        } else if (call.method == "stopInduceUnlock") {
            result(witsSdk?.getInduceUnlock().stopInduceUnlock());
        } else if (call.method == "isRunningInduceUnlock") {
            result(false);
        } else if (call.method == "unlock") {
            witsSdk?.getBleUnlock().addCallBack(unlockInfo: self);
            result(witsSdk?.getBleUnlock().unlock());
        } else if (call.method == "serialPortSendData") {
            //Log.e("初始化", "调用串口发送数据" + call.argument("deviceId"));
            witsSdk?.getSerialPort().addCall(serialPortListen: self);
            let args: NSDictionary = call.arguments as! NSDictionary
            result(witsSdk?.getSerialPort().sendData(deviceId: args["deviceId"] as! String, data: args["data"] as! String));
        } else if (call.method == "closeSerialPort") {
            witsSdk!.getSerialPort().closeSerialPort();
            result(true);
        }

    }


    /*  》》》》》》》》》》》》》》》》开门的回调《《《《《《《《《《《《《《《《《《《《*/
    func success(deviceId: String, code: Int) {
        print("开门成功的回调\(deviceId)\(code)")
        var flutterUnlock = FlutterUnlock();
        flutterUnlock.event = "success";
        flutterUnlock.code = code;
        flutterUnlock.deviceId = deviceId;
        eventPlugin?.sendUnlockBleEvent(data: String(data: try! encoder.encode(flutterUnlock), encoding: .utf8)!)

    }

    func fail(error: String, code: Int) {
        var flutterUnlock = FlutterUnlock();
        flutterUnlock.event = "fail";
        flutterUnlock.code = code;
        flutterUnlock.error = error;
        eventPlugin?.sendUnlockBleEvent(data: String(data: try! encoder.encode(flutterUnlock), encoding: .utf8)!)

    }

    func battery(deviceId: String, battery: Int) {
        var flutterUnlock = FlutterUnlock();
        flutterUnlock.event = "battery";
        flutterUnlock.battery = battery;
        flutterUnlock.deviceId = deviceId;
        eventPlugin?.sendUnlockBleEvent(data: String(data: try! encoder.encode(flutterUnlock), encoding: .utf8)!)

    }

    func devices(deviceId: String) {
        var flutterUnlock = FlutterUnlock();
        flutterUnlock.event = "devices";
        flutterUnlock.deviceId = deviceId;
        eventPlugin?.sendUnlockBleEvent(data: String(data: try! encoder.encode(flutterUnlock), encoding: .utf8)!)

    }


    /*  》》》》》》》》》》》》》》》》串口的回调《《《《《《《《《《《《《《《《《《《《*/
    func serialPortFail(deviceId: String, error: String, code: Int) {
        var flutterSerialPort = FlutterSerialPort();
        flutterSerialPort.event = "serialPortFail";
        flutterSerialPort.deviceId = deviceId;
        flutterSerialPort.code = code;
        flutterSerialPort.error = error;
        eventPlugin?.sendSerialPortEvent(data: String(data: try! encoder.encode(flutterSerialPort), encoding: .utf8)!)

    }

    func serialPortSuccess(deviceId: String, code: Int) {
        var flutterSerialPort = FlutterSerialPort();
        flutterSerialPort.event = "serialPortSuccess";
        flutterSerialPort.deviceId = deviceId;
        flutterSerialPort.code = code;
        eventPlugin?.sendSerialPortEvent(data: String(data: try! encoder.encode(flutterSerialPort), encoding: .utf8)!)
    }

    func acceptedData(deviceId: String, data: Data) {
        var flutterSerialPort = FlutterSerialPort();
        flutterSerialPort.event = "acceptedData";
        flutterSerialPort.deviceId = deviceId;
        flutterSerialPort.data = data.toHexString();
        eventPlugin?.sendSerialPortEvent(data: String(data: try! encoder.encode(flutterSerialPort), encoding: .utf8)!)
    }

}
