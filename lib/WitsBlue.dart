import 'package:flutter/cupertino.dart';
import 'package:flutter/services.dart';

class WitsBlue {
  static const methodChannel = const MethodChannel('witsystem.top/blue/method');

  ///蓝牙事件
  static const EventChannel bleEvent =
      const EventChannel('witsystem.top/blue/event/ble');

  ///开门事件
  static const EventChannel unlockEvent =
      const EventChannel('witsystem.top/blue/event/unlock');

  ///添加设备事件
  static const EventChannel addBleEvent =
      const EventChannel('witsystem.top/blue/event/addBleDevice');

  ///串口事件
  static const EventChannel serialPortEvent =
      const EventChannel('witsystem.top/blue/event/serialPort');

  //wifi配置
  static const EventChannel smartConfigEvent =
      const EventChannel('witsystem.top/blue/event/smartConfig');

  WitsBlue._internal();

  static WitsBlue witsBlue;

  static WitsBlue getInstance() {
    if (witsBlue == null) {
      witsBlue = WitsBlue._internal();
    }
    return witsBlue;
  }

  ///初始化感应开锁
  Future<WitsBlue> witsSdkInit(
      {@required String appId, @required String userToken}) async {
    return await methodChannel.invokeMethod(
            'witsSdkInit', {'appId': appId, 'userToken': userToken})
        ? witsBlue
        : null;
  }

  ///获得设备信息
  Future<String> getDeviceInfo() async {
    return await methodChannel.invokeMethod('getDeviceInfo');
  }

  ///开启感应开锁
  Future<bool> openInduceUnlock() async {
    return await methodChannel.invokeMethod('openInduceUnlock');
  }

  ///关闭感应开锁
  Future<bool> stopInduceUnlock() async {
    return await methodChannel.invokeMethod('stopInduceUnlock');
  }

  ///是否在运行感应开锁
  Future<bool> isRunningInduceUnlock() async {
    return await methodChannel.invokeMethod('isRunningInduceUnlock');
  }

  ///直接开门
  Future<bool> unlock() async {
    return await methodChannel.invokeMethod('unlock');
  }

  ///开启指定设备
  Future<bool> unlockDevice(deviceId) async {
    return await methodChannel.invokeMethod('unlock', {"deviceId": deviceId});
  }

  ///串口发送数据
  Future<bool> serialPortSendData(
      {@required String deviceId, @required String data}) async {
    return await methodChannel.invokeMethod(
        'serialPortSendData', {'deviceId': deviceId, 'data': data});
  }

  ///关闭串口
  Future<bool> closeSerialPort() async {
    return await methodChannel.invokeMethod('closeSerialPort');
  }

  ///添加设备扫描附近设备
  Future<bool> scanDevice() async {
    return await methodChannel.invokeMethod('scanDevice');
  }

  ///添加设备停止扫描附近设备
  Future<bool> stopDevice() async {
    return await methodChannel.invokeMethod('stopDevice');
  }

  ///添加设备，传入指定设备的设备ID
  Future<bool> addDevice(deviceId) async {
    return await methodChannel
        .invokeMethod('addDevice', {'deviceId': deviceId});
  }

  ///取消添加设备
  Future<bool> cancelAdd() async {
    return await methodChannel.invokeMethod('cancelAdd');
  }

  ///smartConfig开始配置
  Future<bool> startSmartConfig(ssid, bssid, pass, deviceName) async {
    return await methodChannel.invokeMethod('startSmartConfig',
        {'ssid': ssid, 'bssid': bssid, "pass": pass, "deviceName": deviceName});
  }

  ///smartConfig停止配置
  Future<bool> stopSmartConfig() async {
    return await methodChannel.invokeMethod('stopSmartConfig');
  }

  ///smartConfig当前是否在配置中
  Future<String> isSmartConfig() async {
    return await methodChannel.invokeMethod('isSmartConfig');
  }
}
