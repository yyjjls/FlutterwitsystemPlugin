package com.witsystem.top.flutterwitsystem.smartconfig;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.util.List;

//wifi 配置管理者
public class SmartConfigManager implements SmartConfig {

    private String appId;

    private String token;

    private Context context;

    private static SmartConfig smartConfig;

    private EsptouchAsyncTask esptouchAsyncTask;

    private SmartConfigManager(Context context, String appId, String token) {
        this.context = context;
        this.appId = appId;
        this.token = token;
    }

    public static SmartConfig getInstance(Context context, String appId, String token) {
        if (smartConfig == null) {
            smartConfig = new SmartConfigManager(context, appId, token);
        }
        return smartConfig;
    }


    @Override
    public void startSmartConfig(String ssid, String bssid, String pass) {
        esptouchAsyncTask = new EsptouchAsyncTask(result -> {
            esptouchAsyncTask = null;
            Log.d("TAG", "执行完成结果》》》》》》》》》》》》》》》》》》");
        });
        esptouchAsyncTask.execute(ssid, bssid, pass, "YES", "0");

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

    }

    public interface TaskListener {
        void onFinished(List<IEsptouchResult> result);
    }

    private class EsptouchAsyncTask extends AsyncTask<String, Void, List<IEsptouchResult>> {
        private final TaskListener taskListener;
        private EsptouchTask mEsptouchTask;

        public EsptouchAsyncTask(TaskListener listener) {
            this.taskListener = listener;
        }

        private final Object mLock = new Object();

        @Override
        protected void onPreExecute() {
            Log.d("TAG", "开始执行》》》》》》》》》》》》》》》》》》");
        }

        @Override
        protected List<IEsptouchResult> doInBackground(String... params) {
            int taskResultCount = -1;
            synchronized (mLock) {
                String apSsid = params[0];
                String apBssid = params[1];
                String apPassword = params[2];
                // String isSsidHiddenStr = params[3];
                String taskResultCountStr = params[4];
               /* boolean isSsidHidden = false;
                if (isSsidHiddenStr.equals("YES")) {
                    isSsidHidden = true;
                }*/
                taskResultCount = Integer.parseInt(taskResultCountStr);
                mEsptouchTask = new EsptouchTask(apSsid, apBssid, apPassword, context);
                mEsptouchTask.setPackageBroadcast(true);
                //mEsptouchTask.setEsptouchListener(myListener);
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
        protected void onCancelled() {
            super.onCancelled();
            Log.d("TAG", "关闭task");
            // mEsptouchTask.interrupt();
        }

        public boolean cancel() {
            mEsptouchTask.interrupt();
            return this.cancel(true);
        }

    }
}
