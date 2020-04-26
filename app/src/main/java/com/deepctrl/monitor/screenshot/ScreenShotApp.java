package com.deepctrl.monitor.screenshot;

import android.app.Application;
import android.os.PowerManager;

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
