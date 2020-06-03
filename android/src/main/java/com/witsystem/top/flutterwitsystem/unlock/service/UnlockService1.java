package com.witsystem.top.flutterwitsystem.unlock.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.witsystem.top.flutterwitsystem.unlock.Unlock;

public class UnlockService1 extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Unlock.instance(getApplication()).unlock();
        return super.onStartCommand(intent, flags, startId);
    }




}
