import 'dart:async';

import 'package:flutter/services.dart';

class Flutterwitsystem {
  static const MethodChannel _channel = const MethodChannel('witsystem.top/blue');

  static Future<String> get witsSdkInit async {
    final bool version = await _channel.invokeMethod('witsSdkInit');
    return version.toString();
  }
  static Future<String> get platformVersion async {
    final bool version = await _channel.invokeMethod('openInduceUnlock');
    return version.toString();
  }
}
