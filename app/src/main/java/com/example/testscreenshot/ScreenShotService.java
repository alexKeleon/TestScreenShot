package com.example.testscreenshot;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.Date;

public class ScreenShotService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                Log.d("screenshot", "截图: " + new Date().
//                        toString());
//                ScreenShotApp.getInstance().getScreenShotHelper().doScreenShot();
////                Intent i = new Intent("testsceenshot.shotter");
////                // 这个不是必需的
////                i.addCategory(Intent.CATEGORY_DEFAULT);
////                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
////                startActivity(i);
//            }
//        }).start();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d("screenshot", "截图: " + new Date().
                        toString());
                try {
                    ScreenShotApp.getInstance().getScreenShotHelper().doScreenShot();
                } catch (Exception e) {
                    Log.e("screenshot error", "run: shot", e);
                }
            }
        }, 5);
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int five = 2000; // 这是2s
        long triggerAtTime = SystemClock.elapsedRealtime() + five;
        Intent i = new Intent(this, AlarmReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
        return super.onStartCommand(intent, flags, startId);
    }

}
