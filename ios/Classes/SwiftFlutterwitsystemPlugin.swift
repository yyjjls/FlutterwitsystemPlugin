import Flutter
import UIKit

public class SwiftFlutterwitsystemPlugin: NSObject, FlutterPlugin, UnlockInfo {


    private var witsSdk: WitsSdk?;

    public static func register(with registrar: FlutterPluginRegistrar) {
        let channel = FlutterMethodChannel(name: "witsystem.top/blue/method", binaryMessenger: registrar.messenger())
        let instance = SwiftFlutterwitsystemPlugin()
        registrar.addMethodCallDelegate(instance, channel: channel)
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
        }

    }


    /*  》》》》》》》》》》》》》》》》开门的回调《《《《《《《《《《《《《《《《《《《《*/
    func success(deviceId: String, code: Int) {
      print("开门成功的回调\(deviceId)\(code)")
    }

    func fail(error: String, code: Int) {

    }

    func battery(deviceId: String, battery: Int) {

    }

    func devices(deviceId: String) {

    }
}
