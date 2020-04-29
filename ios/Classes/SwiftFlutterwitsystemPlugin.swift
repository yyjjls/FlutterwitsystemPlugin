import Flutter
import UIKit

public class SwiftFlutterwitsystemPlugin: NSObject, FlutterPlugin {
  public static func register(with registrar: FlutterPluginRegistrar) {
    let channel = FlutterMethodChannel(name: "witsystem.top/blue", binaryMessenger: registrar.messenger())
    let instance = SwiftFlutterwitsystemPlugin()
    registrar.addMethodCallDelegate(instance, channel: channel)
  }

  public func handle(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
          if (call.method.equals("witsSdkInit")) {
              //初始化SDK成功返回true
              result.success(false);
          } else if (call.method.equals("openInduceUnlock")) {
              //开启感应开锁
              result.success(false);
          } else if (call.method.equals("stopInduceUnlock")) {
              result.success(false);
          } else if (call.method.equals("isRunningInduceUnlock")) {
              result.success(false);
          }

  }
}
