package com.witsystem.top.flutterwitsystem;


import io.flutter.Log;
import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.PluginRegistry;


public class FlutterwitsystemEventPlugin {

    private static final String bleEvent = PluginConfig.CHANNEL + "/event/ble";
    private static final String unlockEvent = PluginConfig.CHANNEL + "/event/unlock";
    private static final String addBleEvent = PluginConfig.CHANNEL + "/event/addBleDevice";
    private static final String serialPortEvent = PluginConfig.CHANNEL + "/event/serialPort";


    public EventChannel.EventSink bleEventSink;
    public EventChannel.EventSink unlockBleEventSink;
    public EventChannel.EventSink addBleEventSink;
    public EventChannel.EventSink serialPortEventSink;

    private static FlutterwitsystemEventPlugin flutterwitsystemEventPlugin;

    public void onAttachedToEngine(FlutterPlugin.FlutterPluginBinding flutterPluginBinding) {
        EventChannel BleEventChannel = new EventChannel(flutterPluginBinding.getBinaryMessenger(), bleEvent);
        BleEventChannel.setStreamHandler(new EventChannel.StreamHandler() {
            @Override
            public void onListen(Object o, EventChannel.EventSink eventSink) {
                android.util.Log.e("开门", "onListen: 注册监听开门" + eventSink);
                bleEventSink = eventSink;
            }

            @Override
            public void onCancel(Object o) {
                bleEventSink = null;
            }
        });
        EventChannel unlockEventChannel = new EventChannel(flutterPluginBinding.getBinaryMessenger(), unlockEvent);
        unlockEventChannel.setStreamHandler(new EventChannel.StreamHandler() {
            @Override
            public void onListen(Object o, EventChannel.EventSink eventSink) {
                unlockBleEventSink = eventSink;
            }

            @Override
            public void onCancel(Object o) {
                unlockBleEventSink = null;
            }
        });
        EventChannel addBleEventChannel = new EventChannel(flutterPluginBinding.getBinaryMessenger(), addBleEvent);
        addBleEventChannel.setStreamHandler(new EventChannel.StreamHandler() {
            @Override
            public void onListen(Object o, EventChannel.EventSink eventSink) {
                addBleEventSink = eventSink;
            }

            @Override
            public void onCancel(Object o) {
                addBleEventSink = null;
            }
        });
        EventChannel serialPortEventChannel = new EventChannel(flutterPluginBinding.getBinaryMessenger(), serialPortEvent);
        serialPortEventChannel.setStreamHandler(new EventChannel.StreamHandler() {
            @Override
            public void onListen(Object o, EventChannel.EventSink eventSink) {
                serialPortEventSink = eventSink;
            }

            @Override
            public void onCancel(Object o) {
                serialPortEventSink = null;
            }
        });


    }

    public void registerWith(PluginRegistry.Registrar registrar) {
        EventChannel BleEventChannel = new EventChannel(registrar.messenger(), bleEvent);
        BleEventChannel.setStreamHandler(new EventChannel.StreamHandler() {
            @Override
            public void onListen(Object o, EventChannel.EventSink eventSink) {
                bleEventSink = eventSink;
            }

            @Override
            public void onCancel(Object o) {
                bleEventSink = null;
            }
        });

        EventChannel unlockEventChannel = new EventChannel(registrar.messenger(), unlockEvent);
        unlockEventChannel.setStreamHandler(new EventChannel.StreamHandler() {
            @Override
            public void onListen(Object o, EventChannel.EventSink eventSink) {
                unlockBleEventSink = eventSink;
            }

            @Override
            public void onCancel(Object o) {
                unlockBleEventSink = null;
            }
        });
        EventChannel addBleEventChannel = new EventChannel(registrar.messenger(), addBleEvent);
        addBleEventChannel.setStreamHandler(new EventChannel.StreamHandler() {
            @Override
            public void onListen(Object o, EventChannel.EventSink eventSink) {
                addBleEventSink = eventSink;
            }

            @Override
            public void onCancel(Object o) {
                addBleEventSink = null;
            }
        });

        EventChannel serialPortEventChannel = new EventChannel(registrar.messenger(), serialPortEvent);
        serialPortEventChannel.setStreamHandler(new EventChannel.StreamHandler() {
            @Override
            public void onListen(Object o, EventChannel.EventSink eventSink) {
                serialPortEventSink = eventSink;
            }

            @Override
            public void onCancel(Object o) {
                serialPortEventSink = null;
            }
        });
    }


    public static FlutterwitsystemEventPlugin create() {
        if (flutterwitsystemEventPlugin == null) {
            flutterwitsystemEventPlugin = new FlutterwitsystemEventPlugin();
        }
        return flutterwitsystemEventPlugin;
    }

    /**
     * 发送蓝牙事件
     */
    public void sendBleEvent(Object data) {
        if (bleEventSink != null) {
            bleEventSink.success(data);
        } else {
            Log.e(">>>", "没有监听者");
        }
    }

    /**
     * 发送开门事件
     */
    public void sendUnlockBleEvent(Object data) {
        if (unlockBleEventSink != null) {
            unlockBleEventSink.success(data);
        } else {
            Log.e(">>>", "没有监听者");
        }
    }

    /**
     * 发送添加设备事件
     */
    public void sendAddBleEvent(Object data) {
        if (addBleEventSink != null) {
            addBleEventSink.success(data);
        } else {
            Log.e(">>>", "没有监听者");
        }
    }

    /**
     * 发送串口事件
     */
    public void sendSerialPortEvent(Object data) {
        if (serialPortEventSink != null) {
            serialPortEventSink.success(data);
        } else {
            Log.e(">>>", "没有监听者");
        }
    }
}
