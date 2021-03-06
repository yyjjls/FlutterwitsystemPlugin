import 'dart:convert';

import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutterwitsystem/WitsBlue.dart';

void main() => runApp(MyApp());

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _platformVersion = 'Unknown';
  WitsBlue witsBlue;

  @override
  void initState() {
    super.initState();

    initPlatformState();
  }

  Future<void> initPlatformState() async {
    witsBlue = await WitsBlue.getInstance().witsSdkInit(
        appId: 'smart09cdcb9ebb2c4169957f0d5423432ff2',
        userToken: '33d3ab3f00e84971b04cf7826ce47bd8-1596127315689');
    if (!mounted) return;
    _platformVersion =
        '是否已经运行:' + (await witsBlue.isRunningInduceUnlock()).toString();
    setState(() {});
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Center(
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: <Widget>[
              Text('$_platformVersion'),
              FlatButton(
                  child: Text('开启'),
                  onPressed: () async {
                    bool b = await witsBlue.openInduceUnlock();
                    print('开启返回值：${b}');
                    _platformVersion = '开启成功$b';
                    setState(() {});
                  }),
              FlatButton(
                child: Text('关闭'),
                onPressed: () async {
                  bool b = await witsBlue.stopInduceUnlock();
                  print('关闭返回值：${b}');
                  _platformVersion = '已关闭$b';
                  setState(() {});
                },
              ),
              FlatButton(
                child: Text('开锁'),
                onPressed: () async {
                  // bool b = await witsBlue.unlockDevice("Slock04EE033EABA4");
                  bool b = await witsBlue.unlock();
                  print('关闭返回值：${b}');
                  _platformVersion = '已关闭$b';
                  setState(() {});
                },
              ),
              FlatButton(
                child: Text('串口发送数据'),
                onPressed: () async {
                  bool b = await witsBlue.serialPortSendData(
                      deviceId: 'Slock04EE033EABD7', data: 'ef55010313020019');
                  print('关闭返回值：${b}');
                  _platformVersion = '已关闭$b';
                  setState(() {});
                },
              ),
              FlatButton(
                child: Text('扫描添加设备'),
                onPressed: () async {
                  bool b = await witsBlue.scanDevice();
                  print('关闭返回值：${b}');
                  _platformVersion = '已关闭$b';
                  setState(() {});
                },
              ),
              FlatButton(
                child: Text('停止添加设备扫描'),
                onPressed: () async {
                  bool b = await witsBlue.stopDevice();
                  print('关闭返回值：${b}');
                  _platformVersion = '已关闭$b';
                  setState(() {});
                },
              ),
              FlatButton(
                child: Text('连接添加设备'),
                onPressed: () async {
                  bool b = await witsBlue.addDevice("Slock04EE033EAD07");
                  print('关闭返回值：${b}');
                  _platformVersion = '已关闭$b';
                  setState(() {});
                },
              ),
              FlatButton(
                child: Text('开启smartConfig'),
                onPressed: () async {
                  Map info = json.decode(await WitsBlue.getInstance().getWifiInfo());

                  bool b = await witsBlue.startSmartConfig(
                      info["ssid"], info["bssid"], "12345678", "测试时");
                  print('关闭返回值：${b}');
                  _platformVersion = '已关闭$b';
                  setState(() {});
                },
              ),
              FlatButton(
                child: Text('关闭smartConfig'),
                onPressed: () async {
                  bool b = await witsBlue.stopSmartConfig();
                  print('关闭返回值：${b}');
                  _platformVersion = '已关闭$b';
                  setState(() {});
                },
              ),
              FlatButton(
                child: Text('获取信息smartConfig'),
                onPressed: () async {
                  print('获取信息smartConfig：${await witsBlue.getWifiInfo()}');
                  setState(() {});
                },
              ),
            ],
          ),
        ),
      ),
    );
  }
}
