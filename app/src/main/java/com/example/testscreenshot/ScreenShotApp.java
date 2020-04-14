package com.example.testscreenshot;

import android.app.Application;

import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.concurrent.TimeUnit;

public class ScreenShotApp extends Application {
    private ScreenShotHelper screenShotHelper;

    private static ScreenShotApp instance;

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
        PeriodicWorkRequest.Builder screenshotWorkBuilder = new PeriodicWorkRequest.Builder(ScreenShotWorker.class, 3,
                TimeUnit.SECONDS);
        PeriodicWorkRequest screenShotWork = screenshotWorkBuilder.build();
        WorkManager.getInstance(this).enqueue(screenShotWork);
    }
}
