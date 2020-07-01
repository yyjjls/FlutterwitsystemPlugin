package com.witsystem.top.flutterwitsystem.add.ble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.util.Log;

import com.witsystem.top.flutterwitsystem.add.AddDevice;
import com.witsystem.top.flutterwitsystem.ble.Ble;
import com.witsystem.top.flutterwitsystem.ble.BleCode;
import com.witsystem.top.flutterwitsystem.device.DeviceManager;
import com.witsystem.top.flutterwitsystem.net.HttpsClient;
import com.witsystem.top.flutterwitsystem.tools.ByteToString;
import com.witsystem.top.flutterwitsystem.tools.NetWork;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

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

    //安全认证通过
    private static final int SECURITY_OK = 0x00;

    //安全认证失败
    private static final int SECURITY_FAIL = 0x01;

    //沒有进入设置状态
    private static final int SECURITY_NO_SETTING_STATE = 0x02;

    //网络认证的权限码
    private int checkCode = 0;

    //读取出来的设备信息
    private DeviceInfo deviceInfo;


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
        processCall(null, BleCode.SCAN_END);
    }

    @Override
    public void addDevice(String deviceId) {
        //???ID的转成mac
        if (deviceId == null) {
            return;
        }
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
        if ("Slock".contains(device.getName()) && scanRecord[5] == -15 && scanRecord[6] == -1) {
            processCall(device.getAddress(), BleCode.SCAN_ADD_DEVICE_INFO);
            scanDeviceCall(device.getName(), rssi);
        }
    }


    /**
     * 连接设备
     */
    private void connection(BluetoothDevice device) {
        stopDevice();
        processCall(device.getName(), BleCode.CONNECTING);
        List<BluetoothDevice> connectedDevices = Ble.instance(context).getBluetoothManager().getConnectedDevices(BluetoothProfile.GATT_SERVER);
        if (connectedDevices.toString().contains(device.getAddress())) {
            errorCall(device.getAddress(), "Another app of the phone is connected to the device", BleCode.OTHER_APP_CONN_DEVICE);
        } else {
            BluetoothGatt gatt = device.connectGatt(context, false, this);
            startTimer(new TimerTask() {
                @Override
                public void run() {
                    disConnection(gatt);
                    errorCall(device.getAddress(), "Connection timeout", BleCode.CONNECTION_TIMEOUT);
                }
            }, 5000);

        }
    }


    /**
     * 断开连接
     */
    private void disConnection(BluetoothGatt gatt) {
        gatt.disconnect();
        startTimer(new TimerTask() {
            @Override
            public void run() {
                gatt.close();
            }
        }, 200);
    }


    //------------------蓝牙连接的回调--------------------------------//
    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
        super.onConnectionStateChange(gatt, status, newState);
        timer.cancel();
        if (status == BluetoothGatt.GATT_SUCCESS && newState == BluetoothProfile.STATE_CONNECTED) {
            gatt.discoverServices();
            startTimer(new TimerTask() {
                public void run() {
                    timer.cancel();
                    disConnection(gatt);
                    errorCall(gatt.getDevice().getAddress(), "Found service failure ", BleCode.GET_SERVICE_FAIL);
                }
            }, 10000);
        } else {
            gatt.disconnect();
            gatt.close();
            if (status == 8) {
                errorCall(gatt.getDevice().getAddress(), "Accidentally disconnected", BleCode.UNEXPECTED_DISCONNECT);
            } else if (status == BluetoothGatt.GATT_SUCCESS && newState == BluetoothProfile.STATE_DISCONNECTED) {
                if (!Ble.instance(context).getBlueAdapter().isEnabled())
                    errorCall(gatt.getDevice().getAddress(), "Bluetooth off", BleCode.BLUE_OFF);
            } else {
                errorCall(gatt.getDevice().getAddress(), "Connection device failed", BleCode.CONNECTION_FAIL);
            }
        }
    }

    @Override
    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
        super.onServicesDiscovered(gatt, status);
        timer.cancel();
        processCall(gatt.getDevice().getAddress(), BleCode.CONNECT_SUCCESS);
        BluetoothGattService service = gatt.getService(UUID.fromString(Ble.SERVICES));
        BluetoothGattCharacteristic characteristic = service.getCharacteristic(UUID.fromString(Ble.BATTERY));
        processCall(gatt.getDevice().getAddress(), BleCode.SECURITY_CERTIFICATION_ONGOING);
        gatt.readCharacteristic(characteristic);
        startTimer(new TimerTask() {
            public void run() {
                timer.cancel();
                disConnection(gatt);
                errorCall(gatt.getDevice().getAddress(), "Read Data Timeout", BleCode.READ_OVERTIME);
            }
        }, 1500);

    }

    @Override
    public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        super.onCharacteristicRead(gatt, characteristic, status);
        timer.cancel();
        if (characteristic.getUuid().toString().equalsIgnoreCase(Ble.BATTERY)) {
            handlerFf01(gatt, characteristic, characteristic.getValue());
        } else if (characteristic.getUuid().toString().equalsIgnoreCase(Ble.KEY)) {
            deviceInfo.setKey(ByteToString.bytesToHexString(characteristic.getValue()));
            processCall(gatt.getDevice().getAddress(), BleCode.ACCESS_INFORMATION_COMPLETED);
            new Thread() {
                public void run() {
                    uploadDeviceInfo(gatt);
                }
            }.start();
        }

    }

    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        super.onCharacteristicChanged(gatt, characteristic);
        timer.cancel();
        //接受到进入设置状态的通知
        if (characteristic.getUuid().toString().equalsIgnoreCase(Ble.BATTERY)) {
            deviceInfo = analyze(characteristic.getValue());
            if (deviceInfo.isSetup()) {
                processCall(gatt.getDevice().getAddress(), BleCode.DEVICE_SET_UP);
                readKey(gatt);
            }
        }


    }

    @Override
    public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        super.onCharacteristicWrite(gatt, characteristic, status);
        timer.cancel();
        if (characteristic.getUuid().toString().equalsIgnoreCase(Ble.ADD_FINISH)) {
            processCall(gatt.getDevice().getAddress(), BleCode.ADD_SUCCESS);
            addSuccessCall(gatt.getDevice().getAddress(), BleCode.ADD_SUCCESS);
            disConnection(gatt);
        }

    }

    /**
     * 处理读取的ff01数据
     */
    private void handlerFf01(BluetoothGatt gatt, BluetoothGattCharacteristic characteristicFf01, byte[] data) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                deviceInfo = analyze(data);
                int security = security(gatt.getDevice().getAddress(), deviceInfo);
                if (security == SECURITY_FAIL) {
                    disConnection(gatt);
                } else if (security == SECURITY_OK) {//验证成功立马读取数据
                    readKey(gatt);
                } else if (security == SECURITY_NO_SETTING_STATE) {//设置监听通知
                    startTimer(new TimerTask() {
                        public void run() {
                            timer.cancel();
                            disConnection(gatt);
                            errorCall(gatt.getDevice().getAddress(), "Wait to set state timeout", BleCode.WAIT_DEVICE_SET_UP_OVERTIME);
                        }
                    }, 1000);
                    monitorNotification(gatt, characteristicFf01);
                }

            }
        }.start();

    }


    /**
     * 设置接受ff01的通知
     */
    private void monitorNotification(BluetoothGatt gatt, BluetoothGattCharacteristic characteristicFf01) {
        boolean state = gatt.setCharacteristicNotification(characteristicFf01, true);
        if (state) {
            List<BluetoothGattDescriptor> descriptors = characteristicFf01.getDescriptors();
            if (descriptors == null || descriptors.size() == 0) {
                disConnection(gatt);
                errorCall(gatt.getDevice().getAddress(), "Serial authentication failed", BleCode.SERIAL_PORT_FAIL);
                return;
            }
            for (BluetoothGattDescriptor descriptor : descriptors) {
                descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                gatt.writeDescriptor(descriptor);
            }
        } else {
            disConnection(gatt);
            errorCall(gatt.getDevice().getAddress(), "Serial authentication failed", BleCode.SERIAL_PORT_FAIL);
        }
    }


    /**
     * 读取密钥
     */
    private void readKey(BluetoothGatt gatt) {
        processCall(gatt.getDevice().getAddress(), BleCode.ACCESS_INFORMATION);
        BluetoothGattService service = gatt.getService(UUID.fromString(Ble.SERVICES));
        BluetoothGattCharacteristic characteristicKey = service.getCharacteristic(UUID.fromString(Ble.KEY));
        gatt.readCharacteristic(characteristicKey);
        startTimer(new TimerTask() {
            public void run() {
                timer.cancel();
                disConnection(gatt);
                errorCall(gatt.getDevice().getAddress(), "Read Data Timeout", BleCode.READ_OVERTIME);
            }
        }, 1500);
    }


    /**
     * 解析发ff01的数据
     */
    private DeviceInfo analyze(byte[] ff01) {
        if (ff01 == null || ff01.length < 6)
            return null;
        DeviceInfo deviceInfo = new DeviceInfo();
        deviceInfo.setNewDevice(ff01[0] == 1);
        deviceInfo.setSetup(ff01[1] == 1);
        deviceInfo.setFirmwareVersion(ByteToString.bytesToHexString(new byte[]{ff01[2], ff01[3], ff01[4]}));
        deviceInfo.setBattery(ff01[6]);
        if (ff01.length <= 10) {
            return deviceInfo;
        }
        deviceInfo.setModel(ByteToString.bytesToHexString(new byte[]{ff01[7], ff01[8], ff01[9], ff01[10]}));
        byte[] other = new byte[ff01.length - 11];
        System.arraycopy(ff01, 11, other, 0, other.length);
        deviceInfo.setOther(ByteToString.bytesToHexString(other));
        return deviceInfo;
    }


    /**
     * 进行安全认证
     */
    private int security(String mac, DeviceInfo deviceInfo) {
        if (deviceInfo == null) {
            errorCall(mac, "Inconsistent equipment information", BleCode.FAILED_SECURITY_FAIL);
            return SECURITY_FAIL;
        }

        if (!deviceInfo.isNewDevice()) {
            errorCall(mac, "Device is not new.", BleCode.NO_NEW_DEVICE);
            return SECURITY_FAIL;
        }

        if (!NetWork.isNetworkConnected(context)) {
            errorCall(mac, "Current network not available.", BleCode.NO_NETWORK);
            return SECURITY_FAIL;
        }
        //网络认证设备
        Map<String, Object> map = new HashMap<>();
        map.put("deviceId", "Slock" + mac.replaceAll(":", ""));
        map.put("appId", appId);
        map.put("token", token);
        String clientData = HttpsClient.https("/device/get_verify_device", map);
        if (clientData == null) {
            errorCall(mac, "Failed to get service.", BleCode.SERVER_EXCEPTION);
            return SECURITY_FAIL;
        }
        try {
            JSONObject jsonObject = new JSONObject(clientData);
            if (jsonObject.getInt("err") != 0) {
                errorCall(mac, "Server authentication failed.", BleCode.SERVER_VERIFY_EXCEPTION);
                return SECURITY_FAIL;
            }
            checkCode = jsonObject.getJSONArray("data").getJSONObject(0).getInt("code");
        } catch (JSONException e) {
            errorCall(mac, "Failed to get service.", BleCode.SERVER_EXCEPTION);
            return SECURITY_FAIL;
        }
        if (!deviceInfo.isSetup()) {
            processCall(mac, BleCode.NO_DEVICE_SET_UP);
            return SECURITY_NO_SETTING_STATE;
        }
        processCall(mac, BleCode.SAFETY_CERTIFICATION_COMPLETED);
        return SECURITY_OK;
    }


    /**
     * 提交设备信息到服务器
     */
    private void uploadDeviceInfo(BluetoothGatt gatt) {
        processCall(gatt.getDevice().getAddress(), BleCode.ADDITIONS_BEING_COMPLETED);
        Map<String, Object> map = new HashMap<>();
        map.put("bleDeviceId", "Slock" + gatt.getDevice().getAddress().replaceAll(":", ""));
        map.put("checkCode", String.valueOf(checkCode));
        map.put("bleDeviceKey", deviceInfo.getKey());
        map.put("bleMac", gatt.getDevice().getAddress());
        map.put("bleDeviceModel", deviceInfo.getModel());
        map.put("bleVersion", deviceInfo.getFirmwareVersion());
        map.put("bleDeviceName", deviceInfo.getName() + DeviceManager.getInstance(context, appId, token).getDevicesNumber());//设备名字默认为
        map.put("bleDeviceBattery", String.valueOf(deviceInfo.getBattery()));//设备名字默认为
        //map["bleLongitude"]=val.battery.toString();
        //map["bleLatitude"]=val.battery.toString();
        String clientData = HttpsClient.https("/device/ble/add_ble_device", map);
        if (clientData == null) {
            disConnection(gatt);
            errorCall(gatt.getDevice().getAddress(), "Failed to get service.", BleCode.SERVER_EXCEPTION);
            return;
        }
        try {
            JSONObject jsonObject = new JSONObject(clientData);
            if (jsonObject.getInt("err") != 0) {
                disConnection(gatt);
                errorCall(gatt.getDevice().getAddress(), "Server authentication failed.", BleCode.SERVER_VERIFY_EXCEPTION);
                return;
            }
            processCall(gatt.getDevice().getAddress(), BleCode.ADD_FINISH);
            sendSuccessCommand(gatt);
        } catch (JSONException e) {
            e.printStackTrace();
            disConnection(gatt);
            errorCall(gatt.getDevice().getAddress(), "Failed to get service.", BleCode.SERVER_EXCEPTION);
            return;
        }


    }

    /**
     * 发送添加成功的指令
     */
    private void sendSuccessCommand(BluetoothGatt gatt) {
        BluetoothGattService service = gatt.getService(UUID.fromString(Ble.SERVICES));
        BluetoothGattCharacteristic characteristic = service.getCharacteristic(UUID.fromString(Ble.ADD_FINISH));
        characteristic.setValue(new byte[0x03]);
        gatt.writeCharacteristic(characteristic);
        startTimer(new TimerTask() {
            public void run() {
                timer.cancel();
                //如果出现这种情况代表服务器已经保存，设备没有反应，需要删除服务器设备重试
                disConnection(gatt);
                errorCall(gatt.getDevice().getAddress(), "Waiting for equipment confirmation timeout", BleCode.CONFIRMATION_TIMEOUT);
            }
        }, 1000);
    }


    /**
     * 创建一个统一的定时器整个过程中有且只能有一个定时器存在
     *
     * @param delay
     * @param timerTask
     */
    private void startTimer(TimerTask timerTask, int delay) {
        if (timer != null)
            timer.cancel();
        timer = new Timer();
        timer.schedule(timerTask, delay);
    }


    /**
     * 回调异常
     */
    private void errorCall(String deviceId, String err, int code) {
        if (addBleDeviceCall != null)
            addBleDeviceCall.error(deviceId != null ? "Slock" + deviceId.replaceAll(":", "") : null, err, code);
    }

    /**
     * 回调进度
     */
    private void processCall(String deviceId, int code) {
        if (addBleDeviceCall != null)
            addBleDeviceCall.addProcess(deviceId != null ? "Slock" + deviceId.replaceAll(":", "") : null, code);
    }


    /**
     * 回调扫描到的设备
     */
    private void scanDeviceCall(String deviceId, int rssi) {
        if (addBleDeviceCall != null)
            addBleDeviceCall.scanDevice(deviceId, rssi);
    }


    /**
     * 添加成功的回调
     */
    private void addSuccessCall(String deviceId, int code) {
        if (addBleDeviceCall != null)
            addBleDeviceCall.addSuccess("Slock" + deviceId.replaceAll(":", ""), code);
    }

}
