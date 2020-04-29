import Flutter
import UIKit

public class SwiftFlutterwitsystemPlugin: NSObject, FlutterPlugin {
  public static func register(with registrar: FlutterPluginRegistrar) {
    let channel = FlutterMethodChannel(name: "witsystem.top/blue", binaryMessenger: registrar.messenger())
    let instance = SwiftFlutterwitsystemPlugin()
    registrar.addMethodCallDelegate(instance, channel: channel)
  }

  public func handle(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
          if (call.method=="witsSdkInit") {
              //初始化SDK成功返回true
              result(false);
          } else if (call.method=="openInduceUnlock") {
              //开启感应开锁
              result(false);
          } else if (call.method=="stopInduceUnlock") {
              result(false);
          } else if (call.method=="isRunningInduceUnlock") {
              result(false);
          }

  }
}
