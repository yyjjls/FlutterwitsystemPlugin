package com.witsystem.top.flutterwitsystem;


import io.flutter.Log;
import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.PluginRegistry;


public class FlutterwitsystemEventPlugin {

    private static final String bleEvent = PluginConfig.CHANNEL + "/event/ble";
    private static final String unlockEvent = PluginConfig.CHANNEL + "/event/unlock";
    private static final String addBleEvent = PluginConfig.CHANNEL + "/event/addBleDevice";
    private static final String serialPortEvent = PluginConfig.CHANNEL + "/event/serialPort";
    private static final String smartConfigEvent = PluginConfig.CHANNEL + "/event/smartConfig";


    public EventStreamHandler bleEventSink;
    public EventStreamHandler unlockBleEventSink;
    public EventStreamHandler addBleEventSink;
    public EventStreamHandler serialPortEventSink;
    public EventStreamHandler smartConfigEventSink;


    private static FlutterwitsystemEventPlugin flutterwitsystemEventPlugin;

    public void onAttachedToEngine(BinaryMessenger messenger) {
        reg(messenger);
    }

    public void registerWith(BinaryMessenger messenger) {
        reg(messenger);
    }


    private void reg(BinaryMessenger messenger) {
        EventChannel BleEventChannel = new EventChannel(messenger, bleEvent);
        BleEventChannel.setStreamHandler(bleEventSink);

        EventChannel unlockEventChannel = new EventChannel(messenger, unlockEvent);
        unlockEventChannel.setStreamHandler(unlockBleEventSink);

        EventChannel addBleEventChannel = new EventChannel(messenger, addBleEvent);
        addBleEventChannel.setStreamHandler(addBleEventSink);

        EventChannel serialPortEventChannel = new EventChannel(messenger, serialPortEvent);
        serialPortEventChannel.setStreamHandler(serialPortEventSink);

        EventChannel smartConfigEventChannel = new EventChannel(messenger, smartConfigEvent);
        smartConfigEventChannel.setStreamHandler(smartConfigEventSink);
    }


    private FlutterwitsystemEventPlugin() {
        bleEventSink = new EventStreamHandler();
        unlockBleEventSink = new EventStreamHandler();
        addBleEventSink = new EventStreamHandler();
        serialPortEventSink = new EventStreamHandler();
        smartConfigEventSink = new EventStreamHandler();

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
        bleEventSink.sendEvent(data);
    }

    /**
     * 发送开门事件
     */
    public void sendUnlockBleEvent(Object data) {
        unlockBleEventSink.sendEvent(data);
    }

    /**
     * 发送添加设备事件
     */
    public void sendAddBleEvent(Object data) {
        addBleEventSink.sendEvent(data);
    }

    /**
     * 发送smartconfig事件
     */
    public void sendSmartConfigEvent(Object data) {
        smartConfigEventSink.sendEvent(data);
    }

    /**
     * 发送串口事件
     */
    public void sendSerialPortEvent(Object data) {
        serialPortEventSink.sendEvent(data);
    }


    class EventStreamHandler implements EventChannel.StreamHandler {

        private EventChannel.EventSink eventSink;

        @Override
        public void onListen(Object o, EventChannel.EventSink eventSink) {
            this.eventSink = eventSink;
        }

        @Override
        public void onCancel(Object o) {
            eventSink = null;
        }

        /**
         * 发送事件
         */
        public void sendEvent(Object data) {
            if (eventSink != null) {
                eventSink.success(data);
            } else {
                Log.e(">>>", "没有监听者");
            }
        }
    }

}


