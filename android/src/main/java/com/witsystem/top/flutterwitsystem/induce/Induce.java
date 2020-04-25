package com.witsystem.top.flutterwitsystem.induce;

import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;


import androidx.annotation.RequiresApi;


import com.witsystem.top.flutterwitsystem.rouse.Rouse;

import java.util.ArrayList;
import java.util.List;

/**
 * 感应开锁
 */
public final class Induce implements InduceUnlock {

    private Context context;

    private BluetoothAdapter blueAdapter;

    private static final String SERVICE_PATH = "com.witsystem.top.flutterwitsystem.induce.InduceService";

    private static final int REQUEST_CODE = 0x01;

    private BluetoothManager bluetoothManager;

    private static Induce induce;

    public static Induce instance(Context context) {
        if (induce == null) {
            synchronized (Induce.class) {
                if (induce == null) {
                    induce = new Induce(context);
                }
            }
        }
        return induce;
    }


    private Induce(Context context) {
        this.context = context;
        bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        assert bluetoothManager != null;
        blueAdapter = bluetoothManager.getAdapter();
    }

    @Override
    public boolean isReaction() {
        return true;
    }

    //是否已经开启
    @Override
    public boolean isOpenInduce() {
        return false;
    }

    //开启感应开锁
    @Override
    public boolean openInduceUnlock() {
        if (!isReaction()) return false;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return false;
        }
        List<ScanFilter> scanFilterList = new ArrayList<>();
        ScanFilter.Builder builder = new ScanFilter.Builder();
        // builder.setServiceUuid(ParcelUuid.fromString("0000fff1-0000-1000-8000-00805f9b34fb"));
        //builder.setServiceUuid(ParcelUuid.fromString("0000f1ff-0000-1000-8000-00805f9b34fb"));
        builder.setDeviceName("Slock04EE033EA8CF");//你要扫描的设备的名称，如果使用lightble这个app来模拟蓝牙可以直接设置name
        ScanFilter scanFilter = builder.build();
        scanFilterList.add(scanFilter);
        //指定蓝牙的方式，这里设置的ScanSettings.SCAN_MODE_LOW_LATENCY是比较高频率的扫描方式
        ScanSettings.Builder settingBuilder = new ScanSettings.Builder();
        // settingBuilder.setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY);
        //settingBuilder.setScanMode(ScanSettings.SCAN_MODE_BALANCED);
        settingBuilder.setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY);//高功耗
        //settingBuilder.setScanMode(ScanSettings.SCAN_MODE_LOW_POWER);
        //settingBuilder.setMatchMode(ScanSettings.MATCH_MODE_AGGRESSIVE);
        settingBuilder.setMatchMode(ScanSettings.MATCH_MODE_STICKY);
        settingBuilder.setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES);
        settingBuilder.setLegacy(true);
        ScanSettings settings = settingBuilder.build();
        //指定扫描到蓝牙后是以什么方式通知到app端，这里将以可见服务的形式进行启动
        Intent intent = new Intent(SERVICE_PATH).setPackage(context.getPackageName());
        PendingIntent callbackIntent = null;
        callbackIntent = PendingIntent.getForegroundService(
                context,
                REQUEST_CODE,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        //启动蓝牙扫描
        blueAdapter.getBluetoothLeScanner().startScan(scanFilterList, settings, callbackIntent);
        //启动服务
        context.startService(intent);
        ///启动定时器
        Rouse.instance(context).startRouse();
        Intent intent1 = new Intent(context,InduceService.class);
        context.startService(intent1);
        Log.e("定时任务", "启动感应服务" );
        return true;
    }


    // 关闭感应开锁
    public boolean stopInduceUnlock() {
        //关闭定时器
        Rouse.instance(context).stopRouse();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return false;
        }
        //关闭扫描并且停止当前服务
        context.stopService(stopScan());
        return true;
    }


    ///关闭扫描
    @RequiresApi(api = Build.VERSION_CODES.O)
    public Intent stopScan() {
        //关闭后台扫描
        Intent intent = new Intent(SERVICE_PATH)
                .setPackage(context.getPackageName());
        PendingIntent callbackIntent = PendingIntent.getForegroundService(
                context,
                REQUEST_CODE,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        blueAdapter.getBluetoothLeScanner().stopScan(callbackIntent);
        return intent;
    }


}
