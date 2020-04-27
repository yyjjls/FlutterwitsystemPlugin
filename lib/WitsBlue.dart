import 'package:flutter/cupertino.dart';
import 'package:flutter/services.dart';

class WitsBlue {
  static const platform = const MethodChannel('witsystem.top/blue');

  ///初始化感应开锁
  Future<bool> witsSdkInit(
      {@required String appId, @required String userToken}) async {
    try {
      return await platform.invokeMethod(
          'witsSdkInit', {'appId': appId, 'userToken': userToken});
    } on PlatformException catch (e) {
      print('调用初始化失败');
      return false;
    }
  }

  ///开启感应开锁
  Future<bool> openInduceUnlock() async {
    try {
      return await platform.invokeMethod('openInduceUnlock');
    } on PlatformException catch (e) {
      print('调用开启失败');
      return false;
    }
  }

  ///关闭感应开锁
  Future<bool> stopInduceUnlock() async {
    try {
      return await platform.invokeMethod('stopInduceUnlock');
    } on PlatformException catch (e) {
      print('调用关闭失败');
      return false;
    }
  }

  ///是否在运行感应开锁
  Future<bool> isRunningInduceUnlock() async {
    try {
      return await platform.invokeMethod('isRunningInduceUnlock');
    } on PlatformException catch (e) {
      print('调用关闭失败');
      return false;
    }
  }
}
