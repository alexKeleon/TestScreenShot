package com.deepctrl.monitor.screenshot;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.app.JobIntentService;
import android.util.Log;

import java.util.Date;

public class ScreenShotJobIntentService extends JobIntentService {

    static final int JOB_ID = 1000;

    public static void enqueueWork(Context context, Intent work) {
        enqueueWork(context, ScreenShotJobIntentService.class, JOB_ID, work);
    }

//    @Override
//    public void onCreate() {
//        super.onCreate();
//        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
//        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
//                "ScreenShotApp:myWakeLock");
//        wakeLock.acquire();
//    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                Log.i("screenshot", "截图: " + new Date().
                        toString());
                try {
                    if (ScreenShotApp.getInstance().getScreenShotHelper() != null) {
                        //todo 监听Intent.ACTION_SCREEN_OFF ，如果锁屏了，就不截屏了
                        ScreenShotApp.getInstance().getScreenShotHelper().doScreenShot();
                    } else {
                        Log.i("screenshot", "instance not ready....");
                    }
                } catch (Exception e) {
                    Log.e("screenshot error", "run: shot", e);
                }
                Log.i("screenshot", "Completed service " + new Date().
                        toString());
            }
        }).start();
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int five = 1000; // 1s
        long triggerAtTime = SystemClock.elapsedRealtime() + five;
        Log.i("screenshot", "next shot time is" + triggerAtTime);
        Intent i = new Intent(this, AlarmReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);

        manager.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
    }
}
