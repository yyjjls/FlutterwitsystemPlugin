import UIKit
import Flutter
import flutterwitsystem
@UIApplicationMain
@objc class AppDelegate: FlutterAppDelegate {
  override func application( _ application: UIApplication,didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {
    GeneratedPluginRegistrant.register(with: self)
    print(">>>>>>>>>>>>>>>>>>>");
    return super.application(application, didFinishLaunchingWithOptions: launchOptions)
  }

  override func applicationDidEnterBackground(_ application: UIApplication) {
    super.applicationDidEnterBackground(application)
    print(">>>>>>>>>11>>>>>>>>>>");
  }

  override func applicationWillEnterForeground(_ application: UIApplication) {
    super.applicationWillEnterForeground(application)
    print(">>>>>>>>>>>22>>>>>>>>");
  }

  override func applicationWillTerminate(_ application: UIApplication) {
    super.applicationWillTerminate(application)
    print(">>>>>>>>>>>33>>>>>>>>");
   // WitsSdkInit。;
   // flutterwitsystem.WitsSdkInit
    
  }
}
