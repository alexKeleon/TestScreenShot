package com.example.testscreenshot;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.PowerManager;
import android.os.SystemClock;

public class ScreenShotApp extends Application {
    private ScreenShotHelper screenShotHelper;

    private static ScreenShotApp instance;

    private PowerManager.WakeLock wakeLock;

    public ScreenShotHelper getScreenShotHelper() {
        return screenShotHelper;
    }

    public void setScreenShotHelper(ScreenShotHelper screenShotHelper) {
        this.screenShotHelper = screenShotHelper;
    }

    public static ScreenShotApp getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

}
