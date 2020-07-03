import Flutter
import UIKit

public class SwiftFlutterwitsystemPlugin: NSObject, FlutterPlugin {
  private var witsSdk:WitsSdk?;
    
    public static func register(with registrar: FlutterPluginRegistrar) {
    let channel = FlutterMethodChannel(name: "witsystem.top/blue/method", binaryMessenger: registrar.messenger())
    let instance = SwiftFlutterwitsystemPlugin()
    registrar.addMethodCallDelegate(instance, channel: channel)
  }
 
    
    
    
  public func handle(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
          if (call.method=="witsSdkInit") {
              //初始化SDK成功返回true
            witsSdk=WitsSdkInit.getInstance.witsSdkInit();
              result(witsSdk != nil);
          } else if (call.method=="openInduceUnlock") {
              //开启感应开锁
              result(witsSdk?.getInduceUnlock().openInduceUnlock());
          } else if (call.method=="stopInduceUnlock") {
              result(witsSdk?.getInduceUnlock().stopInduceUnlock());
          } else if (call.method=="isRunningInduceUnlock") {
              result(false);
          }else if (call.method=="unlock") {
            result(Unlock.getInstance.unlock());
           }

  }
}
