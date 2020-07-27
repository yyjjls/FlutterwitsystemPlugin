package com.witsystem.top.flutterwitsystem.smartconfig;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.location.LocationManagerCompat;

import java.net.InetAddress;
import java.util.List;

//wifi 配置管理者
public class SmartConfigManager implements SmartConfig {

    private String appId;

    private String token;

    private Context context;

    private SmartConfigCall smartConfigCall;

    private static SmartConfig smartConfig;

    private EsptouchAsyncTask esptouchAsyncTask;


    private WifiManager mWifiManager;

    private SmartConfigManager(Context context, String appId, String token) {
        this.context = context;
        this.appId = appId;
        this.token = token;
        mWifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
    }

    public static SmartConfig getInstance(Context context, String appId, String token) {
        if (smartConfig == null) {
            smartConfig = new SmartConfigManager(context, appId, token);
        }
        return smartConfig;
    }


    @Override
    public void startSmartConfig(String ssid, String bssid, String pass) {
        StateResult stateResult = check();
        if (!stateResult.permissionGranted) {
            callFail(SmartConfigCode.PERMISSION_GRANTED, stateResult.message.toString());
            return ;
        }
        if (stateResult.locationRequirement) {
            callFail(SmartConfigCode.LOCATION_REQUIREMENT, stateResult.message.toString());
            return ;
        }
        if (!stateResult.wifiConnected) {
            callFail(SmartConfigCode.WIFI_DISCONNECT, stateResult.message.toString());
            return;
        }
        if (stateResult.is5G) {
            callFail(SmartConfigCode.WIFI_CONNECT_5G, stateResult.message.toString());
            return;
        }
        esptouchAsyncTask = new EsptouchAsyncTask(result -> {
            esptouchAsyncTask = null;
            Log.d("TAG", "执行完成结果》》》》》》》》》》》》》》》》》》" + result);
        });
        esptouchAsyncTask.execute(ssid, bssid, pass, "false", "1"); //false 代表组播，1代表配置设备就一个
    }

    @Override
    public boolean isSmartConfig() {
        return esptouchAsyncTask != null;
    }

    @Override
    public boolean stopSmartConfig() {
        if (esptouchAsyncTask == null) return false;
        boolean cancel = esptouchAsyncTask.cancel();
        if (cancel) {
            esptouchAsyncTask = null;
        }
        return cancel;
    }

    @Override
    public void addSmartConfigCallBack(SmartConfigCall smartConfigCall) {
        this.smartConfigCall = smartConfigCall;
    }


    private void callFail(int code, String error) {
        if (smartConfigCall != null) {
            smartConfigCall.smartConfigFail(code, error);
        }
    }

    private void callSuccess(IEsptouchResult esptouchResult) {
        if (smartConfigCall != null) {
            smartConfigCall.smartConfigSuccess(esptouchResult.getBssid(), esptouchResult.getInetAddress().getHostAddress(), esptouchResult.isSuc(), esptouchResult.isCancelled());
        }
    }


    //校验信息
    private StateResult check() {
        StateResult result = checkPermission();
        if (!result.permissionGranted) {
            return result;
        }
        result = checkLocation();
        result.permissionGranted = true;
        if (result.locationRequirement) {
            return result;
        }
        result = checkWifi();
        result.permissionGranted = true;
        result.locationRequirement = false;
        return result;
    }


    //校验权限
    protected StateResult checkPermission() {
        StateResult result = new StateResult();
        result.permissionGranted = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            boolean locationGranted = context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED;
            if (!locationGranted) {
                String[] splits = "APP require Location permission to get Wi-Fi information. \nClick to request permission".split("\n");
                if (splits.length != 2) {
                    throw new IllegalArgumentException("Invalid String @RES esptouch_message_permission");
                }
                SpannableStringBuilder ssb = new SpannableStringBuilder(splits[0]);
                ssb.append('\n');
                SpannableString clickMsg = new SpannableString(splits[1]);
                ForegroundColorSpan clickSpan = new ForegroundColorSpan(0xFF0022FF);
                clickMsg.setSpan(clickSpan, 0, clickMsg.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                ssb.append(clickMsg);
                result.message = ssb;
                return result;
            }
        }

        result.permissionGranted = true;
        return result;
    }

    //校验定位
    protected StateResult checkLocation() {
        StateResult result = new StateResult();
        result.locationRequirement = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            LocationManager manager = context.getSystemService(LocationManager.class);
            boolean enable = manager != null && LocationManagerCompat.isLocationEnabled(manager);
            if (!enable) {
                result.message = "Please turn on GPS to get Wi-Fi information.";
                return result;
            }
        }

        result.locationRequirement = false;
        return result;
    }

    //校验wifi信息
    protected StateResult checkWifi() {
        StateResult result = new StateResult();
        result.wifiConnected = false;
        WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
        boolean connected = NetUtils.isWifiConnected(mWifiManager);
        if (!connected) {
            result.message = "Please connect Wi-Fi first.";
            return result;
        }
        String ssid = NetUtils.getSsidString(wifiInfo);
        int ipValue = wifiInfo.getIpAddress();
        if (ipValue != 0) {
            result.address = NetUtils.getAddress(wifiInfo.getIpAddress());
        } else {
            result.address = NetUtils.getIPv4Address();
            if (result.address == null) {
                result.address = NetUtils.getIPv6Address();
            }
        }

        result.wifiConnected = true;
        result.message = "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            result.is5G = NetUtils.is5G(wifiInfo.getFrequency());
        } else {
            result.message = "Above android5.0 support.";
            return result;
        }
        if (result.is5G) {
            result.message = "Current Wi-Fi connection is 5G, but the device just support 2.4G.";
        }
        result.ssid = ssid;
        result.ssidBytes = NetUtils.getRawSsidBytesOrElse(wifiInfo, ssid.getBytes());
        result.bssid = wifiInfo.getBSSID();
        return result;
    }


    protected static class StateResult {
        public CharSequence message = null;

        public boolean permissionGranted = false;
        public boolean locationRequirement = false;
        public boolean wifiConnected = false;
        public boolean is5G = false;
        public InetAddress address = null;
        public String ssid = null;
        public byte[] ssidBytes = null;
        public String bssid = null;
    }


    public interface TaskListener {
        void onFinished(List<IEsptouchResult> result);
    }

    private class EsptouchAsyncTask extends AsyncTask<String, IEsptouchResult, List<IEsptouchResult>> {
        private final TaskListener taskListener;
        private EsptouchTask mEsptouchTask;

        public EsptouchAsyncTask(TaskListener listener) {
            this.taskListener = listener;
        }

        private final Object mLock = new Object();

        @Override
        protected List<IEsptouchResult> doInBackground(String... params) {
            int taskResultCount = 1;
            synchronized (mLock) {
                String apSsid = params[0];
                String apBssid = params[1];
                String apPassword = params[2];
                String isSsidHiddenStr = params[3];
                String taskResultCountStr = params[4];
                if (!isSsidHiddenStr.equals("true") && !isSsidHiddenStr.equals("false")) {
                    isSsidHiddenStr = "false";
                }
                taskResultCount = Integer.parseInt(taskResultCountStr);
                mEsptouchTask = new EsptouchTask(apSsid, apBssid, apPassword, context);
                mEsptouchTask.setPackageBroadcast(Boolean.parseBoolean(isSsidHiddenStr));
                mEsptouchTask.setEsptouchListener(this::publishProgress);
            }
            return mEsptouchTask.executeForResults(taskResultCount);
        }

        @Override
        protected void onPostExecute(List<IEsptouchResult> result) {
            IEsptouchResult firstResult = result.get(0);
            if (!firstResult.isCancelled()) {
                if (this.taskListener != null) {
                    this.taskListener.onFinished(result);
                }
            }
        }

        @Override
        protected void onProgressUpdate(IEsptouchResult... values) {
            if (context != null) {
                IEsptouchResult result = values[0];
                Log.i("返回结果", "EspTouchResult: " + result);
                String text = result.getBssid() + " is connected to the wifi";
                Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        public boolean cancel() {
            mEsptouchTask.interrupt();
            return this.cancel(true);
        }

    }
}
