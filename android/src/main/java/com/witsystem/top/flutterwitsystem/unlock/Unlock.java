package com.witsystem.top.flutterwitsystem.unlock;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
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
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;


/**
 * 开锁
 */

public class Unlock extends BluetoothGattCallback implements BleUnlock, BluetoothAdapter.LeScanCallback {

    private static final String TAG = "开锁";

    private static Unlock unlock;

    private Context context;

    private UnlockInfo unlockInfo;

    private Timer timer;

    private Map<String, BluetoothGatt> gattMap;

    private Unlock(Context context) {
        this.context = context;
        gattMap = new HashMap<>();
    }

    public static Unlock instance(Context context) {
        if (unlock == null) {
            synchronized (Unlock.class) {
                if (unlock == null) {
                    unlock = new Unlock(context);
                }
            }
        }
        return unlock;
    }


    @Override
    public boolean unlock() {
        if (!isDeviceInfoOrBleState()) {
            return false;
        }
        return scan();
    }

    @Override
    public boolean unlock(String deviceId) {
        if (!isDeviceInfoOrBleState()) {
            return false;
        }
        DeviceInfo deviceInfo = DeviceManager.getInstance(context, null, null).getDevice(deviceId);
        if (deviceInfo == null) {
            failCall("Failed to obtain device information", BleCode.GET_DEVICE_INFO_FAIL);
            return false;
        }
        connection(Ble.instance(context).getBlueAdapter().getRemoteDevice(deviceInfo.getBleMac()));
        return true;
    }

    /**
     * 判断蓝牙获得设备的信息状态
     */
    private boolean isDeviceInfoOrBleState() {
        if (!Ble.instance(context).getBlueAdapter().isEnabled()) {
            failCall("Bluetooth not on", BleCode.DEVICE_BLUE_OFF);
            return false;
        }
        if (DeviceManager.getInstance(context, null, null).getDevicesNumber() == 0) {
            failCall("No equipment currently available", BleCode.NO_DEVICE);
            return false;
        }
        return true;
    }


    @Override
    public void addCallBack(UnlockInfo unlockInfo) {
        this.unlockInfo = unlockInfo;
    }

    /**
     * 开始扫描
     */
    private boolean scan() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                stopScan();
                failCall("Scan Timeout", BleCode.SCAN_OUT_TIME);
                timer.cancel();
            }
        }, 10000);//延时1s执行
        return Ble.instance(context).getBlueAdapter().startLeScan(this);
    }

    /**
     * 停止扫描
     */
    private void stopScan() {
        Ble.instance(context).getBlueAdapter().stopLeScan(this);
    }


    @Override
    public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
        if (device.getName() == null || device.getName().equals("")) {
            return;
        }
        DeviceInfo deviceInfo = DeviceManager.getInstance(context, null, null).getDevice(device.getName());
        if (deviceInfo == null) {
            return;
        }
        timer.cancel();
        stopScan();
        connection(device);
    }

    /**
     * 连接设备
     *
     * @param device
     */
    private void connection(BluetoothDevice device) {
        List<BluetoothDevice> connectedDevices = Ble.instance(context).getBluetoothManager().getConnectedDevices(BluetoothProfile.GATT_SERVER);
        if (connectedDevices.toString().contains(device.getAddress())) {
            if (gattMap.get(device.getAddress()) == null) {
                failCall("Another app of the phone is connected to the device", BleCode.OTHER_APP_CONN_DEVICE);
            } else {
                Objects.requireNonNull(gattMap.get(device.getAddress())).discoverServices();
            }
        } else {
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    stopScan();
                    failCall("Connection timeout", BleCode.CONNECTION_TIMEOUT);
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
        gatt.disconnect();
    }

    /**
     * 》》》》》》》》》》》》》》》》》》》》》》》   连接成功的回调 》》》》》》》》》》》》》》》》》》》》》》》》
     */
    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
        super.onConnectionStateChange(gatt, status, newState);
        timer.cancel();
        if (status == BluetoothGatt.GATT_SUCCESS) {
            gatt.discoverServices();
            gattMap.put(gatt.getDevice().getAddress(), gatt);
        } else {
            disConnection(gatt);
            gatt.close();
            failCall(status == 8 ? "Accidentally disconnected" : "Connection device failed", status == 8 ? BleCode.UNEXPECTED_DISCONNECT : BleCode.CONNECTION_FAIL);
            gattMap.remove(gatt.getDevice().getAddress());
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
            byte[] openLock = new byte[encrypt.length + 1];
            openLock[0] = 0x01;
            System.arraycopy(encrypt, 0, openLock, 1, encrypt.length);
            characteristicUnlock.setValue(openLock);
            gatt.writeCharacteristic(characteristicUnlock);
        }
    }

    @Override
    public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        super.onCharacteristicWrite(gatt, characteristic, status);
        if (characteristic.getUuid().toString().equals(Ble.UNLOCK)) {
            disConnection(gatt);
            gatt.close();
            gattMap.remove(gatt.getDevice().getAddress());
            successCall(gatt.getDevice().getName(), BleCode.UNLOCK_SUCCESS);
        }
    }

    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        super.onCharacteristicChanged(gatt, characteristic);
    }


    /**
     * 》》》》》》》》》》》》》》》》》》》》》》》   连接成功的回调 》》》》》》》》》》》》》》》》》》》》》》》》
     */


    /**
     * 失败的回调
     *
     * @param error
     * @param code
     */
    private void failCall(String error, int code) {
        if (unlockInfo != null)
            unlockInfo.fail(error, code);
    }


    /**
     * 成功的回调
     *
     * @param deviceId
     * @param code
     */
    private void successCall(String deviceId, int code) {
        if (unlockInfo != null)
            unlockInfo.success(deviceId, code);
    }


    /**
     * 电量的回调
     *
     * @param deviceId
     * @param battery
     */
    private void batteryCall(String deviceId, int battery) {
        if (unlockInfo != null)
            unlockInfo.battery(deviceId, battery);
    }


}
