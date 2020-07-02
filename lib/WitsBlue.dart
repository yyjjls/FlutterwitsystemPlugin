import 'package:flutter/cupertino.dart';
import 'package:flutter/services.dart';

class WitsBlue {
  static const methodChannel = const MethodChannel('witsystem.top/blue/method');

  //蓝牙事件
  static const EventChannel bleEvent =
      const EventChannel('witsystem.top/blue/event/ble');

  //开门事件
  static const EventChannel unlockEvent =
      const EventChannel('witsystem.top/blue/event/unlock');

  //添加设备事件
  static const EventChannel addBleEvent =
      const EventChannel('witsystem.top/blue/event/addBleDevice');

  //串口事件
  static const EventChannel serialPortEvent =
      const EventChannel('witsystem.top/blue/event/serialPort');

  dsd() {
    addBleEvent.receiveBroadcastStream().listen((data) {
      print('调用初始获得$data');
    }, onError: (err) {
      print('调用初unlockEvent始化失败$err');
    });
  }

  ///初始化感应开锁
  Future<bool> witsSdkInit(
      {@required String appId, @required String userToken}) async {
    try {
      return await methodChannel.invokeMethod(
          'witsSdkInit', {'appId': appId, 'userToken': userToken});
    } on PlatformException catch (e) {
      print('调用初始化失败$e');
      return false;
    }
  }

  ///获得设备信息
  Future<bool> getDeviceInfo() async {
    try {
      return await methodChannel.invokeMethod('getDeviceInfo');
    } on PlatformException catch (e) {
      print('调用初始化失败$e');
      return false;
    }
  }

  ///开启感应开锁
  Future<bool> openInduceUnlock() async {
    try {
      return await methodChannel.invokeMethod('openInduceUnlock');
    } on PlatformException catch (e) {
      print('调用开启失败$e');
      return false;
    }
  }

  ///关闭感应开锁
  Future<bool> stopInduceUnlock() async {
    try {
      return await methodChannel.invokeMethod('stopInduceUnlock');
    } on PlatformException catch (e) {
      print('调用关闭失败$e');
      return false;
    }
  }

  ///是否在运行感应开锁
  Future<bool> isRunningInduceUnlock() async {
    try {
      return await methodChannel.invokeMethod('isRunningInduceUnlock');
    } on PlatformException catch (e) {
      print('调用关闭失败$e');
      return false;
    }
  }

  ///直接开门
  Future<bool> unlock() async {
    try {
      return await methodChannel.invokeMethod('unlock');
    } on PlatformException catch (e) {
      print('调用关闭失败$e');
      return false;
    }
  }

  ///串口发送数据
  Future<bool> serialPortSendData(
      {@required String deviceId, @required String data}) async {
    try {
      return await methodChannel.invokeMethod(
          'serialPortSendData', {'deviceId': deviceId, 'data': data});
    } on PlatformException catch (e) {
      print('调用初始化失败$e');
      return false;
    }
  }

  ///关闭串口
  Future<bool> closeSerialPort() async {
    try {
      return await methodChannel.invokeMethod('closeSerialPort');
    } on PlatformException catch (e) {
      print('调用初始化失败$e');
      return false;
    }
  }

  ///添加设备扫描附近设备
  Future<bool> scanDevice() async {
    try {
      return await methodChannel.invokeMethod('scanDevice');
    } on PlatformException catch (e) {
      print('调用关闭失败$e');
      return false;
    }
  }

  ///添加设备停止扫描附近设备
  Future<bool> stopDevice() async {
    try {
      return await methodChannel.invokeMethod('stopDevice');
    } on PlatformException catch (e) {
      print('调用关闭失败$e');
      return false;
    }
  }

  ///添加设备，传入指定设备的设备ID
  Future<bool> addDevice(deviceId) async {
    try {
      return await methodChannel
          .invokeMethod('addDevice', {'deviceId': deviceId});
    } on PlatformException catch (e) {
      print('调用关闭失败$e');
      return false;
    }
  }

  ///取消添加设备
  Future<bool> cancelAdd() async {
    try {
      return await methodChannel.invokeMethod('cancelAdd');
    } on PlatformException catch (e) {
      print('调用关闭失败$e');
      return false;
    }
  }
}
