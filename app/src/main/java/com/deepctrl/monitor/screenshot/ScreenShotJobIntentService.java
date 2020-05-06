package com.deepctrl.monitor.screenshot;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.app.JobIntentService;
import android.util.Log;

import com.deepctrl.monitor.screenshot.tcpclient.TcpConnector;

import java.util.Date;

import static java.lang.Thread.sleep;

public class ScreenShotJobIntentService extends JobIntentService {

    static final int JOB_ID = 1000;

    private ComponentName component;

    public static void enqueueWork(Context context, Intent work) {
        enqueueWork(context, ScreenShotJobIntentService.class, JOB_ID, work);
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
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
                    try {
                        sleep(300);
                    } catch (Exception e) {
                        Log.e("screenshot error", "sleep: ", e);
                    }
                }
            }
        }).start();


//        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
//        //long triggerAtTime = SystemClock.elapsedRealtime() + Constants.SHOT_TIME;
//        long triggerAtTime = System.currentTimeMillis() +  Constants.SHOT_TIME;
//        Log.i("screenshot", "next shot time is" + triggerAtTime);
//        Intent i = new Intent(this, AlarmReceiver.class);
//        PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);
//        //AlarmManager.AlarmClockInfo alarmClockInfo =manager.setAlarmClock(, pi);
//        manager.setExact(AlarmManager.RTC_WAKEUP, triggerAtTime, pi);
//        //manager.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
    }
}
