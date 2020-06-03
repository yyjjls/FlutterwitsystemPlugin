package com.witsystem.top.flutterwitsystem.ble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;

import java.util.ArrayList;
import java.util.List;

/**
 * 蓝牙对象个数控制
 */
public class Ble {

    public static final String SERVICES = "0000fff1-0000-1000-8000-00805f9b34fb";

    public static final String TOKEN = "0000ff05-0000-1000-8000-00805f9b34fb";

    public static final String UNLOCK = "0000ff04-0000-1000-8000-00805f9b34fb";

    public static final String BATTERY = "0000ff01-0000-1000-8000-00805f9b34fb";


    private Context context;

    private BluetoothManager bluetoothManager;

    private BluetoothAdapter blueAdapter;

    //缓存扫描到的设备
    private List<BluetoothDevice> listDevice;

    private static Ble ble;


    public static Ble instance(Context context) {
        if (ble == null) {
            synchronized (Ble.class) {
                if (ble == null) {
                    ble = new Ble(context);
                }
            }
        }
        return ble;
    }


    private Ble(Context context) {
        this.context = context;
        bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        assert bluetoothManager != null;
        blueAdapter = bluetoothManager.getAdapter();
        listDevice = new ArrayList<>();
    }


    public BluetoothManager getBluetoothManager() {
        return bluetoothManager;
    }


    public BluetoothAdapter getBlueAdapter() {
        return blueAdapter;
    }


    public List<BluetoothDevice> getListDevice() {
        return listDevice;
    }

    public void setListDevice(List<BluetoothDevice> listDevice) {
        this.listDevice = listDevice;
    }
}
