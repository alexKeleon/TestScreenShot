package com.deepctrl.monitor.screenshot;

import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import java.util.Date;

public class AlarmReceiver extends WakefulBroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        long elapsedTime = SystemClock.elapsedRealtime() + Constants.SHOT_TIME;
        Log.i("screenshot", "onReceive: " + new Date().
                toString() + "elapsedTime is " + elapsedTime);
        ScreenShotJobIntentService.enqueueWork(context, intent);
    }
}
