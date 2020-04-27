package com.witsystem.top.flutterwitsystem.unlock;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.witsystem.top.flutterwitsystem.device.DeviceInfo;
import com.witsystem.top.flutterwitsystem.device.DeviceManager;
import com.witsystem.top.flutterwitsystem.induce.Induce;
import com.witsystem.top.flutterwitsystem.tools.AesEncryption;
import com.witsystem.top.flutterwitsystem.tools.ByteToString;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

/**
 * 距离开锁判断设备的距离
 */
public class DistanceUnlock {

    //开门信号
    private int unlockRssi = -55;

    private static final String SERVICES = "0000fff1-0000-1000-8000-00805f9b34fb";

    /**
     * 读取token 的UUID
     */
    private static final String TOKEN = "0000ff05-0000-1000-8000-00805f9b34fb";
    /**
     * 开锁特征
     */
    private static final String UNLOCK = "0000ff04-0000-1000-8000-00805f9b34fb";

    private static DistanceUnlock distanceUnlock;

    private Context context;

    private BluetoothGatt gatt;

    private Timer timer;

    private TimerTask timerTask;

    private long unlockTime;

    private List<Integer> rssiList = new ArrayList<>(5);

    public static DistanceUnlock instance(Context context) {
        if (distanceUnlock == null) {
            synchronized (Induce.class) {
                if (distanceUnlock == null) {
                    distanceUnlock = new DistanceUnlock();
                    distanceUnlock.context = context;

                }
            }
        }
        return distanceUnlock;
    }


    ///连接设备进行距离判断
    public void connectDeviceRanging(ScanResult scanResult) {
        synchronized (DistanceUnlock.class) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                return;
            }
            if (scanResult.getRssi() < (unlockRssi - 20)) {
                return;
            }
            //如果不为空代表已经有连接
            if (gatt != null) {
                return;
            }
            DeviceInfo device = DeviceManager.getInstance(context, null, null).getDevice(scanResult.getDevice().getName());
            if (device == null) {
                return;
            }
            gatt = scanResult.getDevice().connectGatt(context, false, new BluetoothGattCallback() {
                @Override
                public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                    super.onConnectionStateChange(gatt, status, newState);
                    Log.e("启动服务", "连接状态：" + status + ":::" + newState);
                    if (newState == BluetoothProfile.STATE_CONNECTED) {
                        gatt.discoverServices();
                    } else {
                        close();
                    }
                }

                @Override
                public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                    super.onServicesDiscovered(gatt, status);
                    // sendUnlockInfo(gatt);
                    readRssi(gatt);
                }

                @Override
                public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                    super.onCharacteristicRead(gatt, characteristic, status);
                    if (characteristic.getUuid().toString().toLowerCase().equals(TOKEN.toLowerCase())) {
                        readToken(gatt, characteristic.getValue());
                    }
                }

                @Override
                public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                    super.onCharacteristicWrite(gatt, characteristic, status);
                }

                @Override
                public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
                    super.onCharacteristicChanged(gatt, characteristic);
                }

                @Override
                public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
                    super.onReadRemoteRssi(gatt, rssi, status);
                    handlerReadRssiValue(gatt, rssi);
                }
            });
        }

    }

    //不断读取信号
    private void readRssi(final BluetoothGatt gatt) {
        if (timerTask != null) return;
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                gatt.readRemoteRssi();
            }
        };
        timer.schedule(timerTask, 0, 100);
    }

    //停止读取
    private void stopReadRssi() {
        if (timerTask != null) {
            timerTask.cancel();
        }
        if (timer != null) {
            timer.cancel();
        }
        timerTask = null;
        timer = null;
    }


    /**
     * 通知取5次的平均值信号值来判断是否断开
     *
     * @param rssi
     */
    private void handlerReadRssiValue(BluetoothGatt gatt, int rssi) {
        rssiList.add(rssi);
        if (rssiList.size() < 5) {
            return;
        } else {
            rssiList.remove(0);
        }
        ///获得5次的总值判断平局值
        int sum = 0;
        for (Integer integer : rssiList) {
            sum += integer;
        }
        sum = sum / rssiList.size();
        if (sum < (unlockRssi - 20)) {
            Log.e("启动服务", "》》》》》》》》》》》》断开设备》》》》》》》》》》》》》》》》》》》》》》》》》》》》");
            close();
        } else if (sum > unlockRssi) {
            //Log.e("启动服务", "开门成功》》》》》》》》》》》》》》》》》》》》》》》》》》》》");
            if (gatt.getServices().size() == 0) {
                gatt.discoverServices();
            } else {
                sendUnlockInfo(gatt);
            }
        }

    }

    /**
     * 发送开锁信息
     */
    private void sendUnlockInfo(BluetoothGatt gatt) {
        if (System.currentTimeMillis() - unlockTime < 2000) {
            return;
        }
        unlockTime = System.currentTimeMillis();
        BluetoothGattService service = gatt.getService(UUID.fromString(SERVICES));
        BluetoothGattCharacteristic token = service.getCharacteristic(UUID.fromString(TOKEN));
        gatt.readCharacteristic(token);

    }

    /**
     * 读取到的token
     */
    private void readToken(BluetoothGatt gatt, byte[] value) {
        if (value == null || value.length == 0) return;
        writeOpenValue(gatt, value);
    }


    /**
     * 写入开锁值的方法
     */
    private void writeOpenValue(BluetoothGatt gatt, byte[] value) {
        String devicesKey = DeviceManager.getInstance(context, null, null).getDevice(gatt.getDevice().getName()).getBleDeviceKey();
        byte[] encrypt = AesEncryption.encrypt(value, devicesKey);
        String encryptText = ByteToString.bytesToHexString(encrypt);
        Log.i("加密好的数据", encryptText);
        byte[] openLockData = getOpenLockData(ByteToString.str2HexByte(encryptText));
        BluetoothGattService service = gatt.getService(UUID.fromString(SERVICES));
        BluetoothGattCharacteristic unlock = service.getCharacteristic(UUID.fromString(UNLOCK));
        unlock.setValue(openLockData);
        gatt.writeCharacteristic(unlock);
        Log.e("启动服务", "开门成功》》》》》》》》》》》》》》》》》》》》》》》》》》》》");
    }


    /**
     * 增加开锁码
     */

    private byte[] getOpenLockData(byte[] by) {
        byte[] openLock = new byte[by.length + 1];
        openLock[0] = 0x01;
        for (int i = 0; i < by.length; i++) {
            openLock[i + 1] = by[i];
        }
        return openLock;

    }

    //进行资源回收
    public void close() {
        stopReadRssi();
        if (gatt != null) {
            gatt.disconnect();
            gatt.close();
        }
        gatt = null;
        rssiList.clear();


    }
}
