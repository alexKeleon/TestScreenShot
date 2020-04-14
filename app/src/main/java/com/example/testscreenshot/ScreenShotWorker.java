package com.example.testscreenshot;

import android.content.Context;
import android.util.Log;


import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.util.Date;

public class ScreenShotWorker extends Worker {
    public ScreenShotWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.i("screenshot", "截图: " + new Date().
                toString());
        try {
            if (ScreenShotApp.getInstance().getScreenShotHelper() != null) {
                ScreenShotApp.getInstance().getScreenShotHelper().doScreenShot();
            } else {
                Log.i("screenshot", "instance not ready....");
            }
        } catch (Exception e) {
            Log.e("screenshot error", "run: shot", e);
        }
        return Result.success();
    }
}
