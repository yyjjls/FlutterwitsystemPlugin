package com.witsystem.top.flutterwitsystem.unlock;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
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

        time=System.currentTimeMillis();
        //scan();
        unlock("");
        return false;

    }
//04:EE:03:3E:A8:CF
    @Override
    public boolean unlock(String deviceId) {
        connection(Ble.instance(context).getBlueAdapter().getRemoteDevice("04:EE:03:3E:A8:CF"));
        return false;
    }


    /**
     * 开始扫描
     */
    private void scan() {
        Ble.instance(context).getBlueAdapter().startLeScan(this);
    }

    /**
     * 停止扫描
     */
    private void stopScan() {
        Ble.instance(context).getBlueAdapter().stopLeScan(this);
    }


    @Override
    public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
        Log.e("开门", "onCharacteristicWrite: 扫描到的设备"+device.getName() );
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
        Log.e("开门", "onCharacteristicWrite: 开始连接" +(System.currentTimeMillis()-time));
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
            Log.e("开门", "onCharacteristicWrite: 结束连接" +(gatt.getDevice().getAddress()));
            Log.e("开门", "onCharacteristicWrite: 结束连接" +(device));
            byte[] encrypt = AesEncryption.encrypt(characteristic.getValue(), device.getBleDeviceKey());
            BluetoothGattService service = gatt.getService(UUID.fromString(Ble.SERVICES));
            BluetoothGattCharacteristic characteristicUnlock = service.getCharacteristic(UUID.fromString(Ble.UNLOCK));
            characteristicUnlock.setValue(encrypt);
            gatt.writeCharacteristic(characteristicUnlock);
        }
    }

    @Override
    public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        super.onCharacteristicWrite(gatt, characteristic, status);
        if (characteristic.getUuid().toString().equals(Ble.UNLOCK)) {
            Log.e("开门", "onCharacteristicWrite: 开门成功" +(System.currentTimeMillis()-time));
            disConnection(gatt);
            gatt.close();
        }
    }

    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        super.onCharacteristicChanged(gatt, characteristic);
    }


    /** 》》》》》》》》》》》》》》》》》》》》》》》   连接成功的回调 》》》》》》》》》》》》》》》》》》》》》》》》 */
}
