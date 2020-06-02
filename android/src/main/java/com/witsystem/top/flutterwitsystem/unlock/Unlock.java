package com.witsystem.top.flutterwitsystem.unlock;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.util.Log;

import com.witsystem.top.flutterwitsystem.ble.Ble;
import com.witsystem.top.flutterwitsystem.device.DeviceInfo;
import com.witsystem.top.flutterwitsystem.device.DeviceManager;
import com.witsystem.top.flutterwitsystem.tools.AesEncryption;

import java.util.UUID;


/**
 * 开锁
 */

public class Unlock extends BluetoothGattCallback implements BleUnlock, BluetoothAdapter.LeScanCallback {

    private static Unlock unlock;

    private Context context;

    private UnlockInfo unlockInfo;

    private Unlock(Context context) {
        this.context = context;
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

    Long time;

    @Override
    public boolean unlock() {
        time = System.currentTimeMillis();
        return scan();
    }

    @Override
    public boolean unlock(String deviceId) {
        time = System.currentTimeMillis();
        DeviceInfo deviceInfo = DeviceManager.getInstance(context, null, null).getDevice(deviceId);
        if (deviceInfo == null) {
            return false;
        }
        connection(Ble.instance(context).getBlueAdapter().getRemoteDevice(deviceInfo.getBleMac()));
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
        stopScan();
        connection(device);
    }

    /**
     * 连接设备
     *
     * @param device
     */
    private void connection(BluetoothDevice device) {
        BluetoothGatt gatt = device.connectGatt(context, false, this);
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

        gatt.discoverServices();
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
            Log.e("开门", "onCharacteristicWrite: 开门成功" + (System.currentTimeMillis() - time));
            disConnection(gatt);
            gatt.close();
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
