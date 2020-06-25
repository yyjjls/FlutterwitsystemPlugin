package com.witsystem.top.flutterwitsystem.serialport;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.util.Log;

import com.witsystem.top.flutterwitsystem.ble.Ble;
import com.witsystem.top.flutterwitsystem.ble.BleCode;
import com.witsystem.top.flutterwitsystem.device.DeviceInfo;
import com.witsystem.top.flutterwitsystem.device.DeviceManager;
import com.witsystem.top.flutterwitsystem.tools.AesEncryption;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

/**
 * 开启串口只要获得了对象开启串口就成功
 */
public class OpenSerialPort extends BluetoothGattCallback implements SerialPort {

    private static SerialPort serialPort;

    private SerialPortListen serialPortListen;

    private Context context;

    private Timer timer;

    private Map<String, BluetoothGatt> gattMap;

    private String data;

    private OpenSerialPort(Context context) {
        this.context = context;
        gattMap = new HashMap<>();
    }

    public static SerialPort instance(Context context) {
        if (serialPort == null) {
            serialPort = new OpenSerialPort(context);
        }
        return serialPort;
    }

    @Override
    public void addCall(SerialPortListen serialPortListen) {
        this.serialPortListen = serialPortListen;
    }

    @Override
    public boolean sendData(String deviceId, String data) {
        if (data == null) {
            return false;
        }
        boolean deviceInfoOrBleState = isDeviceInfoOrBleState();
        if (!deviceInfoOrBleState) {
            return false;
        }
        this.data = data;
        DeviceInfo deviceInfo = DeviceManager.getInstance(context, null, null).getDevice(deviceId);
        if (deviceInfo == null) {
            failCall(deviceId, "Failed to obtain device information", BleCode.GET_DEVICE_INFO_FAIL);
            return false;
        }
        connection(Ble.instance(context).getBlueAdapter().getRemoteDevice(deviceInfo.getBleMac()));
        return true;
    }


    @Override
    public void closeSerialPort() {
        Set<String> strings = gattMap.keySet();
        for (String string : strings) {
            disConnection(Objects.requireNonNull(gattMap.get(string)));
        }
        gattMap.clear();
    }

    /**
     * 判断蓝牙获得设备的信息状态
     */
    private boolean isDeviceInfoOrBleState() {
        if (!Ble.instance(context).getBlueAdapter().isEnabled()) {
            failCall(null, "Bluetooth not on", BleCode.DEVICE_BLUE_OFF);
            return false;
        }
        if (DeviceManager.getInstance(context, null, null).getDevicesNumber() == 0) {
            failCall(null, "No equipment currently available", BleCode.NO_DEVICE);
            return false;
        }
        return true;
    }

    /**
     * 连接设备
     */
    private void connection(BluetoothDevice device) {
        List<BluetoothDevice> connectedDevices = Ble.instance(context).getBluetoothManager().getConnectedDevices(BluetoothProfile.GATT_SERVER);
        if (connectedDevices.toString().contains(device.getAddress())) {
            if (gattMap.get(device.getAddress()) == null) {
                failCall(device.getName(), "Another app of the phone is connected to the device", BleCode.OTHER_APP_CONN_DEVICE);
            } else {
                writeData(Objects.requireNonNull(gattMap.get(device.getAddress())));
            }
        } else {
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    failCall(device.getName(), "Connection timeout", BleCode.CONNECTION_TIMEOUT);
                    timer.cancel();
                }
            }, 5000);
            device.connectGatt(context, false, this);
        }
    }

    /**
     * 断开连接
     */
    private void disConnection(BluetoothGatt gatt) {
        gattMap.remove(gatt.getDevice().getAddress());
        gatt.disconnect();
        gatt.close();
    }

    /**********************连接设备的回调************************/

    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
        super.onConnectionStateChange(gatt, status, newState);
        timer.cancel();
        if (status == BluetoothGatt.GATT_SUCCESS && newState == BluetoothProfile.STATE_CONNECTED) {
            gatt.discoverServices();
            gattMap.put(gatt.getDevice().getAddress(), gatt);
            timer = new Timer();
            timer.schedule(new TimerTask() {
                public void run() {
                    failCall(gatt.getDevice().getAddress(), "Certification  timeout", BleCode.SERIAL_PORT_AUTH_OVERTIME);
                    timer.cancel();
                    disConnection(gatt);
                }
            }, 6000);
        } else {
            disConnection(gatt);
            if (status == 8) {
                failCall(gatt.getDevice().getName(), "Accidentally disconnected", BleCode.UNEXPECTED_DISCONNECT);
            } else if (status == BluetoothGatt.GATT_SUCCESS && newState == BluetoothProfile.STATE_DISCONNECTED) {
                failCall(gatt.getDevice().getName(), "Bluetooth off", BleCode.BLUE_OFF);
            } else {
                failCall(gatt.getDevice().getName(), "Connection device failed", BleCode.CONNECTION_FAIL);
            }
            gattMap.remove(gatt.getDevice().getAddress());
            timer.cancel();
        }
    }

    @Override
    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
        super.onServicesDiscovered(gatt, status);
        BluetoothGattService service = gatt.getService(UUID.fromString(Ble.SERVICES));
        BluetoothGattCharacteristic characteristic = service.getCharacteristic(UUID.fromString(Ble.TOKEN));
        gatt.readCharacteristic(characteristic);
    }

    @Override
    public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        super.onCharacteristicRead(gatt, characteristic, status);
        if (characteristic.getUuid().toString().equals(Ble.TOKEN)) {
            DeviceInfo device = DeviceManager.getInstance(context, null, null).getMacDevice(gatt.getDevice().getAddress());
            byte[] encrypt = AesEncryption.encrypt(characteristic.getValue(), device.getBleDeviceKey());
            BluetoothGattService service = gatt.getService(UUID.fromString(Ble.SERVICES));
            BluetoothGattCharacteristic characteristicUnlock = service.getCharacteristic(UUID.fromString(Ble.UNLOCK));
            characteristicUnlock.setValue(AesEncryption.authenticationData(encrypt));
            gatt.writeCharacteristic(characteristicUnlock);
        }
    }

    @Override
    public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        super.onCharacteristicWrite(gatt, characteristic, status);
        if (characteristic.getUuid().toString().equals(Ble.SERIAL_PORT_WRITE)) {
            timer.cancel();
            successCall(gatt.getDevice().getAddress(), BleCode.SERIAL_PORT_SEND_DATA_SUCCESS);
            //Log.e("串口", "数据发送成功");
        } else if (characteristic.getUuid().toString().equals(Ble.UNLOCK)) {
            //开启启动串口通知监听
            BluetoothGattService service = gatt.getService(UUID.fromString(Ble.SERVICES));
            BluetoothGattCharacteristic serialPortRead = service.getCharacteristic(UUID.fromString(Ble.SERIAL_PORT_READ));
            boolean state = gatt.setCharacteristicNotification(serialPortRead, true);
            timer.cancel();
            if (state) {
                // Log.e("串口", "认证成功");
                successCall(gatt.getDevice().getAddress(), BleCode.SERIAL_PORT_SUCCESS);
                timer = new Timer();
                timer.schedule(new TimerTask() {
                    public void run() {
                        timer.cancel();
                        writeData(gatt);
                    }
                }, 300);

            } else {
                disConnection(gatt);
                failCall(gatt.getDevice().getAddress(), "Serial authentication failed", BleCode.SERIAL_PORT_FAIL);
            }
        }

    }

    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        super.onCharacteristicChanged(gatt, characteristic);
        acceptedDataCall(gatt.getDevice().getAddress(), characteristic.getValue());
    }

    //写入数据
    private void writeData(BluetoothGatt gatt) {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                failCall(gatt.getDevice().getAddress(), "Data send timeout", BleCode.SERIAL_PORT_SEND_DATA_OVERTIME);
                timer.cancel();
                disConnection(gatt);
            }
        }, 1000);
        BluetoothGattService service = gatt.getService(UUID.fromString(Ble.SERVICES));
        BluetoothGattCharacteristic serialPortWrite = service.getCharacteristic(UUID.fromString(Ble.SERIAL_PORT_WRITE));
        serialPortWrite.setValue(AesEncryption.parseHexStringToBytes(data));
        gatt.writeCharacteristic(serialPortWrite);
        Log.d("发送的数据", data);
        data = null;
    }


    private void failCall(String deviceId, String err, int code) {
        if (serialPortListen == null)
            return;
        serialPortListen.serialPortFail((deviceId != null && deviceId.contains(":")) ? "Slock" + deviceId.replaceAll(":", "") : deviceId, err, code);
    }


    private void successCall(String deviceId, int code) {
        if (serialPortListen == null)
            return;
        serialPortListen.serialPortSuccess(deviceId.contains(":") ? "Slock" + deviceId.replaceAll(":", "") : deviceId, code);
    }


    private void acceptedDataCall(String deviceId, byte[] data) {
        if (serialPortListen == null)
            return;
        serialPortListen.acceptedData(deviceId.contains(":") ? "Slock" + deviceId.replaceAll(":", "") : deviceId, data);
    }
}
