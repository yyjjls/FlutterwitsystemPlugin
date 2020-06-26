import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:flutterwitsystem/WitsBlue.dart';

void main() => runApp(MyApp());

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _platformVersion = 'Unknown';

  @override
  void initState() {
    super.initState();

    initPlatformState();
  }

  Future<void> initPlatformState() async {
    String platformVersion;
    try {
      platformVersion =
          '${await WitsBlue().witsSdkInit(appId: 'smart09cdcb9ebb2c4169957f0d5423432ff2', userToken: '4947b63e0ee746218518700a9918c7d0-1593092816884')}';
    } on PlatformException {
      platformVersion = '初始化失败.';
    }
    if (!mounted) return;
    _platformVersion =
        '是否已经运行:' + (await WitsBlue().isRunningInduceUnlock()).toString();
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
                    bool b = await WitsBlue().openInduceUnlock();
                    print('开启返回值：${b}');
                    _platformVersion = '开启成功$b';
                    setState(() {});
                  }),
              FlatButton(
                child: Text('关闭'),
                onPressed: () async {
                  bool b = await WitsBlue().stopInduceUnlock();
                  print('关闭返回值：${b}');
                  _platformVersion = '已关闭$b';
                  setState(() {});
                },
              ),
              FlatButton(
                child: Text('开锁'),
                onPressed: () async {
                  bool b = await WitsBlue().unlock();
                  print('关闭返回值：${b}');
                  _platformVersion = '已关闭$b';
                  setState(() {});
                },
              ),
              FlatButton(
                child: Text('串口发送数据'),
                onPressed: () async {
                  bool b = await WitsBlue().serialPortSendData(
                      deviceId: 'Slock04EE033EA882', data: 'ef55010313020019');
                  print('关闭返回值：${b}');
                  _platformVersion = '已关闭$b';
                  setState(() {});
                },
              ),
              FlatButton(
                child: Text('扫描添加设备'),
                onPressed: () async {
                  bool b = await WitsBlue().scanDevice();
                  print('关闭返回值：${b}');
                  _platformVersion = '已关闭$b';
                  setState(() {});
                },
              ),
              FlatButton(
                child: Text('停止添加设备扫描'),
                onPressed: () async {
                  bool b = await WitsBlue().stopDevice();
                  print('关闭返回值：${b}');
                  _platformVersion = '已关闭$b';
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
