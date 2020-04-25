package com.witsystem.top.flutterwitsystem.rouse;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.witsystem.top.flutterwitsystem.induce.Induce;



//定时器
public class RouseService extends Service {

    private NotificationManager notificationManager;
    private NotificationCompat.Builder builder;
    private Notification notification;
    private final static int notifyId = 0x023;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Notification("定时任务");
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("定时任务", "定时任务启动服务" + startId);
        Induce.instance(getApplication()).openInduceUnlock();
        return START_STICKY;

    }


    public void Notification(String aMessage) {
        String wit = "witsystem";
        String channelID = "1";
        String channelName = "witsystem";
        if (builder == null) {
            notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            builder = new NotificationCompat.Builder(this, wit);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(channelID, channelName, NotificationManager.IMPORTANCE_HIGH);
                channel.enableLights(false);
                channel.enableVibration(false);
                channel.setVibrationPattern(new long[]{0});
                channel.setSound(null, null);
                builder.setChannelId(channelID);
                notificationManager.createNotificationChannel(channel);
            } else {
                builder.setVibrate(new long[]{0L});
            }
            builder.setDefaults(NotificationCompat.FLAG_ONLY_ALERT_ONCE);
            builder.setSmallIcon(android.R.mipmap.sym_def_app_icon)
                    .setContentTitle(aMessage)
                    .setContentText("定时任务..");

            notification = builder.build();
            notification.flags = Notification.FLAG_ONGOING_EVENT;
            //notificationManager.notify(notifyId, notification);
            startForeground(notifyId, notification);
        }

    }

}
