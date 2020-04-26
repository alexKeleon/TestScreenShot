package com.deepctrl.monitor.screenshot;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import java.util.Date;

public class AlarmReceiver extends WakefulBroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("screenshot", "onReceive: " + new Date().
                toString());
        ScreenShotJobIntentService.enqueueWork(context, intent);
    }
}
