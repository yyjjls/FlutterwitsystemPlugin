package com.witsystem.top.flutterwitsystem.location;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * 定位
 */
public class AppLocation {
    private static String provider;
    private static Location location;

    /**
     * 启动定位
     */
    public static Location startLocation(Context context) {
        //获取定位服务
        //定位都要通过LocationManager这个类实现
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        //获取当前可用的位置控制器
        List<String> list = locationManager.getProviders(true);

//        if (list.contains(LocationManager.GPS_PROVIDER)) {
//            //是否为GPS位置控制器
//            provider = LocationManager.GPS_PROVIDER;
//        } else if (list.contains(LocationManager.NETWORK_PROVIDER)) {
//            //是否为网络位置控制器
//            provider = LocationManager.NETWORK_PROVIDER;
//        } else {
//            return;
//        }
        if (list.contains(LocationManager.NETWORK_PROVIDER)) {
            //是否为网络位置控制器
            provider = LocationManager.NETWORK_PROVIDER;
        }
        if (provider == null) {
            return null;
        }
        location = locationManager.getLastKnownLocation(provider);
        if (location != null) {
//            String string = "纬度为：" + location.getLatitude() + ",经度为："
//                    + location.getLongitude();
//            Log.e("定位", string);
//            Log.e("定位", getLocationAddress(context, location));
        }
        return location;
    }


    public static Location getLocation() {
        return location;
    }

    /**
     * 关闭定位
     */
    public void closeLocation() {
        //locationManager.removeUpdates(locationListener);
    }


    /**
     * 将经纬度转换成中文地址
     */
    public static String getLocationAddress(Context context, Location location) {
        String add = "";
        Geocoder geoCoder = new Geocoder(context, Locale.SIMPLIFIED_CHINESE);
        try {
            List<Address> addresses = geoCoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            Address address = addresses.get(0);
            // Log.i("定位", "getLocationAddress: " + address.toString());
            // Log.i("定位", "getLocationAddress: " + address.getLocality()+address.getThoroughfare());
            int maxLine = address.getMaxAddressLineIndex();
            if (maxLine >= 2) {
                add = address.getAddressLine(1) + address.getAddressLine(2);
            } else {
                add = address.getAddressLine(1);
            }
            add = address.getLocality() + address.getThoroughfare() + add;
        } catch (IOException e) {
            add = "";
            e.printStackTrace();
        }
        return add;
    }


}
