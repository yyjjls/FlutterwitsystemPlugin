import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:flutterwitsystem/flutterwitsystem.dart';
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
      platformVersion = await Flutterwitsystem.witsSdkInit;
    } on PlatformException {
      platformVersion = 'Failed to get platform version.';
    }

    if (!mounted) return;

    setState(() {
      _platformVersion ='初始化'+ platformVersion;
    });
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Center(
          child:Column(
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
            ],
          ),
        ),
      ),
    );
  }
}
