import UIKit
import Flutter
import flutterwitsystem

@UIApplicationMain
@objc class AppDelegate: FlutterAppDelegate {
    override func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {
        GeneratedPluginRegistrant.register(with: self);
        let l = launchOptions?[UIApplication.LaunchOptionsKey.bluetoothCentrals];
        print(">>>>>>>>>>>>>>>>>>>\(l)");
        debugPrint(">>>>>>>>>>>>>>>>>>>\(l)");

         // let ios = IosWitSDkInit();
        // let fruit = ios.getWitsSdk()?.witsSdkInit(appId: "smart09cdcb9ebb2c4169957f0d5423432ff2", token: "4659a0fd6c0443ac8ac946c4709b8d31-1595065244621")?.getInduceUnlock().openInduceUnlock();

//        //开启通知
//        let settings = UIUserNotificationSettings(types: [.alert, .badge, .sound],
//                categories: nil)
//        application.registerUserNotificationSettings(settings)
      //  return super.application(application, didFinishLaunchingWithOptions: launchOptions)
        return true;
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
        let ios = IosWitSDkInit();
        let fruit = ios.getWitsSdk()?.witsSdkInit(appId: "smart09cdcb9ebb2c4169957f0d5423432ff2", token: "4659a0fd6c0443ac8ac946c4709b8d31-1595065244621")?.getInduceUnlock().openInduceUnlock();
    }

}
