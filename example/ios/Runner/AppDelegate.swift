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
//    let ios = IosWitSDkInit();
//    let fruit = ios.getWitsSdk();
   // print(">>>>>>>>>>>22>>>>>>>>\(fruit)");
  }

  override func applicationWillTerminate(_ application: UIApplication) {
    super.applicationWillTerminate(application)
    print(">>>>>>>>>>>33>>>>>>>>");
    let ios = IosWitSDkInit();
    let fruit = ios.getWitsSdk()?.witsSdkInit(appId: "smart09cdcb9ebb2c4169957f0d5423432ff2", token: "4659a0fd6c0443ac8ac946c4709b8d31-1595065244621")?.getInduceUnlock().openInduceUnlock();
    print(">>>>>>>>>>>33>>>>>>>>\(fruit )");
    // WitsSdkInit().getBleUnlock();
   // WitsSdkInit
   // flutterwitsystem.WitsSdkInit
  }
}
