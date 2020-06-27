package com.witsystem.top.flutterwitsystem.add.ble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.util.Log;

import com.witsystem.top.flutterwitsystem.add.AddDevice;
import com.witsystem.top.flutterwitsystem.ble.Ble;
import com.witsystem.top.flutterwitsystem.ble.BleCode;
import com.witsystem.top.flutterwitsystem.tools.ByteToString;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 添加蓝牙设备
 */
public class AddBleDevice extends BluetoothGattCallback implements AddDevice, BluetoothAdapter.LeScanCallback {

    private AddBleDeviceCall addBleDeviceCall;

    private Context context;

    private String appId;

    private String token;

    private static AddDevice addDevice;

    private Timer timer;

    private AddBleDevice(Context context, String appId, String token) {
        this.context = context;
        this.appId = appId;
        this.token = token;
    }

    public static AddDevice instance(Context context, String appId, String token) {
        if (addDevice == null) {
            addDevice = new AddBleDevice(context, appId, token);
        }
        return addDevice;
    }

    @Override
    public void addCall(AddBleDeviceCall addBleDeviceCall) {
        this.addBleDeviceCall = addBleDeviceCall;
    }


    @Override
    public void scanDevice() {
        scan();
    }

    @Override
    public void stopDevice() {
        Ble.instance(context).getBlueAdapter().stopLeScan(this);
    }

    @Override
    public void addDevice(String deviceId) {
        //???ID的转成mac
        BluetoothDevice remoteDevice = Ble.instance(context).getBlueAdapter().getRemoteDevice(deviceId);
        connection(remoteDevice);
    }


    private void scan() {
        BluetoothAdapter blueAdapter = Ble.instance(context).getBlueAdapter();
        if (!blueAdapter.isEnabled()) {
            errorCall(null, "Bluetooth not on", BleCode.DEVICE_BLUE_OFF);
            return;
        }
        boolean startLeScan = blueAdapter.startLeScan(this);
        if (!startLeScan) {
            errorCall(null, "Bluetooth not on", BleCode.BLE_SCAN_FAIL);
            stopDevice();
            return;
        }
        processCall(null, BleCode.SCANNING);
    }

    //------------------蓝牙扫描的回调--------------------------------//
    @Override
    public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
        Log.d("扫描到的设备", device.getName() + "::" + ByteToString.bytesToHexString(scanRecord));


        processCall(device.getName(), BleCode.SCAN_ADD_DEVICE_INFO);
        scanDeviceCall(device.getName(), rssi);
    }


    /**
     * 连接设备
     */
    private void connection(BluetoothDevice device) {
        stopDevice();
        List<BluetoothDevice> connectedDevices = Ble.instance(context).getBluetoothManager().getConnectedDevices(BluetoothProfile.GATT_SERVER);
        if (connectedDevices.toString().contains(device.getAddress())) {
            errorCall(device.getName(), "Another app of the phone is connected to the device", BleCode.OTHER_APP_CONN_DEVICE);
        } else {
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    errorCall(device.getName(), "Connection timeout", BleCode.CONNECTION_TIMEOUT);
                    timer.cancel();
                }
            }, 5000);
            BluetoothGatt gatt = device.connectGatt(context, false, this);

        }
    }


    /**
     * 断开连接
     */
    private void disConnection(BluetoothGatt gatt) {
        gatt.disconnect();
        gatt.close();
    }


    //------------------蓝牙连接的回调--------------------------------//
    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
        super.onConnectionStateChange(gatt, status, newState);
        timer.cancel();
        if (status == BluetoothGatt.GATT_SUCCESS && newState == BluetoothProfile.STATE_CONNECTED) {
            gatt.discoverServices();
        } else {
            disConnection(gatt);
            if (status == 8) {
                errorCall(gatt.getDevice().getName(), "Accidentally disconnected", BleCode.UNEXPECTED_DISCONNECT);
            } else if (status == BluetoothGatt.GATT_SUCCESS && newState == BluetoothProfile.STATE_DISCONNECTED) {
                errorCall(gatt.getDevice().getName(), "Bluetooth off", BleCode.BLUE_OFF);
            } else {
                errorCall(gatt.getDevice().getName(), "Connection device failed", BleCode.CONNECTION_FAIL);
            }
        }
    }

    @Override
    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
        super.onServicesDiscovered(gatt, status);


    }


    /**
     * 回调异常
     */
    private void errorCall(String deviceId, String err, int code) {
        if (addBleDeviceCall != null)
            addBleDeviceCall.error(deviceId, err, code);
    }

    /**
     * 回调进度
     */
    private void processCall(String deviceId, int code) {
        if (addBleDeviceCall != null)
            addBleDeviceCall.addProcess(deviceId, code);
    }


    /**
     * 回调扫描到的设备
     */
    private void scanDeviceCall(String deviceId, int rssi) {
        if (addBleDeviceCall != null)
            addBleDeviceCall.scanDevice(deviceId, rssi);
    }

}
