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
import com.witsystem.top.flutterwitsystem.net.HttpsClient;
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

    private String appId;

    private String userToken;


    private Unlock(Context context, String appId, String userToken) {
        this.context = context;
        this.appId = appId;
        this.userToken = userToken;
        gattMap = new HashMap<>();
    }

    public static Unlock instance(Context context, String appId, String userToken) {
        if (unlock == null) {
            synchronized (Unlock.class) {
                if (unlock == null) {
                    unlock = new Unlock(context, appId, userToken);
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
            failCall(deviceId, "Failed to obtain device information", BleCode.GET_DEVICE_INFO_FAIL);
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
            failCall(null, "Bluetooth not on", BleCode.DEVICE_BLUE_OFF);
            return false;
        }
        if (DeviceManager.getInstance(context, null, null).getDevicesNumber() == 0) {
            failCall(null, "No equipment currently available", BleCode.NO_DEVICE);
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
                failCall(null, "Scan Timeout", BleCode.SCAN_OUT_TIME);
                timer.cancel();
            }
        }, 10000);
        // return Ble.instance(context).getBlueAdapter().startLeScan(new UUID[]{UUID.fromString(Ble.SERVICES)},this);
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
                failCall(device.getName(), "Another app of the phone is connected to the device", BleCode.OTHER_APP_CONN_DEVICE);
            } else {
                Objects.requireNonNull(gattMap.get(device.getAddress())).discoverServices();
            }
        } else {
            BluetoothGatt gatt = device.connectGatt(context, false, this);
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    stopScan();
                    disConnection(gatt);
                    failCall(device.getName(), "Connection timeout", BleCode.CONNECTION_TIMEOUT);
                    timer.cancel();
                }
            }, 5000);

        }
    }

    /**
     * 断开连接
     */
    private void disConnection(BluetoothGatt gatt) {
        gatt.disconnect();
        timer.cancel();
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                timer.cancel();
                gatt.close();
            }
        }, 200);

    }

    /**
     * 》》》》》》》》》》》》》》》》》》》》》》》   连接成功的回调 》》》》》》》》》》》》》》》》》》》》》》》》
     */
    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
        super.onConnectionStateChange(gatt, status, newState);
        timer.cancel();
        //Log.e("状态", "状态" + status + "::" + newState);
        if (status == BluetoothGatt.GATT_SUCCESS && newState == BluetoothProfile.STATE_CONNECTED) {
            gatt.discoverServices();
            gattMap.put(gatt.getDevice().getAddress(), gatt);
        } else {
            gatt.close();
            disConnection(gatt);
            if (status == 8) {
                failCall(gatt.getDevice().getName(), "Accidentally disconnected", BleCode.UNEXPECTED_DISCONNECT);
            } else if (status == BluetoothGatt.GATT_SUCCESS && newState == BluetoothProfile.STATE_DISCONNECTED) {
                if (!Ble.instance(context).getBlueAdapter().isEnabled())
                    failCall(gatt.getDevice().getName(), "Bluetooth off", BleCode.BLUE_OFF);
            } else {
                failCall(gatt.getDevice().getName(), "Connection device failed", BleCode.CONNECTION_FAIL);
            }
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
            characteristicUnlock.setValue(AesEncryption.getOpenLockData(encrypt));
            gatt.writeCharacteristic(characteristicUnlock);
        } else if (characteristic.getUuid().toString().equals(Ble.BATTERY)) {
            byte[] value = characteristic.getValue();
            if (value != null && value.length >= 7)
                batteryCall(gatt.getDevice().getName(), value[6]);
        }
    }

    @Override
    public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        super.onCharacteristicWrite(gatt, characteristic, status);
        if (!characteristic.getUuid().toString().equals(Ble.UNLOCK)) {
            return;
        }
        timer.cancel();
        successCall(gatt.getDevice().getName(), BleCode.UNLOCK_SUCCESS);
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                gatt.disconnect();
                gattMap.remove(gatt.getDevice().getAddress());
                timer.cancel();
            }
        }, 900);

        BluetoothGattService service = gatt.getService(UUID.fromString(Ble.SERVICES));
        BluetoothGattCharacteristic batteryCharacteristic = service.getCharacteristic(UUID.fromString(Ble.BATTERY));
        gatt.readCharacteristic(batteryCharacteristic);
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
    private void failCall(String deviceId, String error, int code) {
        if (deviceId != null)
            uploadRecord(code == BleCode.CONNECTION_TIMEOUT ? 2 : 1, deviceId, -1);
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
        uploadRecord(0, deviceId, battery);
    }


    private void uploadRecord(int state, String deviceId, int battery) {
        Map<String, Object> map = new HashMap();
        map.put("token", userToken);
        map.put("state", state);
        map.put("deviceId", deviceId);
        if (battery >= 0)
            map.put("battery", battery);
        new Thread() {
            @Override
            public void run() {
                super.run();
                String https = HttpsClient.https("/device/upload_record", map);
                Log.e(TAG, "run: 上传记录返回" + https);
            }
        }.start();

    }


}
