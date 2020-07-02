package com.witsystem.top.flutterwitsystem.tools;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * 网络工具
 */
public class NetWork {

    /**
     * 判断当前网络是否可用，如果wifi连接到不可用网络也会返回true
     *
     * @param context
     * @return
     */

    public static boolean isNetworkConnected(Context context) {
        if (context == null)
            return false;
        ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (mConnectivityManager == null)
            return false;
        NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
        return mNetworkInfo != null && mNetworkInfo.isAvailable();
    }
}
