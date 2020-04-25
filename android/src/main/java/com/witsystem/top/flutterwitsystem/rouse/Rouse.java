package com.witsystem.top.flutterwitsystem.rouse;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;


/**
 * 定时唤醒
 */
public class Rouse {

    private static Rouse rouse;
    private Context context;
    private AlarmManager alarmManager;
    private static final String ACTION="com.witsystem.top.flutterwitsystem.rouse.RouseService";

    public static Rouse instance(Context context) {
        if (rouse == null) {
            synchronized (Rouse.class) {
                if (rouse == null) {
                    rouse = new Rouse();
                    rouse.context = context;
                    rouse.alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                }
            }
        }
        return rouse;
    }


    ///启动定时
    public boolean startRouse() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return false;
        }
        Intent alarmIntent = new Intent(context, RouseService.class).setAction(ACTION);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, alarmIntent, 0);
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 30000, pendingIntent);
        Log.e("定时任务", "开启定时器" );
        return true;
    }


    //停止
    public boolean stopRouse() {
        Intent alarmIntent = new Intent(context, RouseService.class).setAction(ACTION);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, alarmIntent, 0);
        alarmManager.cancel(pendingIntent);
        context.stopService(alarmIntent);
        return true;
    }

}
