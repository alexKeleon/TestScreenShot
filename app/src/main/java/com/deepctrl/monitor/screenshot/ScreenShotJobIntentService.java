package com.deepctrl.monitor.screenshot;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.app.JobIntentService;
import android.util.Log;

import com.deepctrl.monitor.screenshot.tcpclient.TcpConnector;

import java.util.Date;

public class ScreenShotJobIntentService extends JobIntentService {

    static final int JOB_ID = 1000;

    public static void enqueueWork(Context context, Intent work) {
        enqueueWork(context, ScreenShotJobIntentService.class, JOB_ID, work);
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {

        Log.i("screenshot", "截图: " + new Date().
                toString());
        try {
            if (ScreenShotApp.getInstance().getScreenShotHelper() != null &&
                    TcpConnector.getINSTANCE().isConnected()) {
                ScreenShotApp.getInstance().getScreenShotHelper().doScreenShot();
            } else {
                Log.i("screenshot", "instance not ready....");
            }
        } catch (Exception e) {
            Log.e("screenshot error", "run: shot", e);
        }
        Log.i("screenshot", "Completed service " + new Date().
                toString());

        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        long triggerAtTime = SystemClock.elapsedRealtime() + Constants.SHOT_TIME;
        Log.i("screenshot", "next shot time is" + triggerAtTime);
        Intent i = new Intent(this, AlarmReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);

        manager.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
    }
}
