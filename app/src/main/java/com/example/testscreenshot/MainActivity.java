package com.example.testscreenshot;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.testscreenshot.util.ShellUtils;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;

public class MainActivity extends AppCompatActivity {
    /**
     * 事件发生
     */
    private static int EVENT_HAPPEN = 1;

    private static int REQUEST_MEDIA_PROJECTION = 0;

    private static int REQUEST_MANAGE_SETTINGS = 1;

    public static final String PIC_DIR_NAME = "myPhotos"; //在系统的图片文件夹下创建了一个相册文件夹，名为“myPhotos"，所有的图片都保存在该文件夹下。

    private File mPicDir = new File(Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_PICTURES), PIC_DIR_NAME); //图片统一保存在系统的图片文件夹中

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestWriteMedia();
        try2StartScreenShot();
        requestWriteSettings();
    }

    private void requestWriteMedia() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
        }
    }

    private void try2StartScreenShot() {
        MediaProjectionManager mediaProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        startActivityForResult(mediaProjectionManager.createScreenCaptureIntent(), REQUEST_MEDIA_PROJECTION);
    }

    private void requestWriteSettings() {
        if (!Settings.System.canWrite(getApplicationContext())) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
            intent.setData(Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, REQUEST_MANAGE_SETTINGS);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_MEDIA_PROJECTION) {
            if (resultCode == RESULT_OK && data != null) {
                ScreenShotHelper screenShotHelper = new ScreenShotHelper(MainActivity.this, resultCode, data, new ScreenShotHelper.OnScreenShotListener() {
                    @Override
                    public void onFinish(Bitmap bitmap) {
                        saveImageToGallery(bitmap);
                    }
                });
                ScreenShotApp.getInstance().setScreenShotHelper(screenShotHelper);
                ScreenShotJobIntentService.enqueueWork(getApplicationContext(), new Intent());
            }
        }
        if (requestCode == REQUEST_MANAGE_SETTINGS) {
            if (resultCode == RESULT_OK && data != null) {
                if (Settings.System.canWrite(MainActivity.this)) {
                    Log.i("screen bright", "onActivityResult: yes");
                    Toast.makeText(MainActivity.this, "同意修改系统配置", Toast.LENGTH_LONG).show();
                } else {
                    Log.i("screen bright", "onActivityResult: no");
                }
            }
        }
    }

    public void saveImageToGallery(Bitmap bitmap) {
        OutputStream out = null;
        try {
            mPicDir.mkdirs();
            String fileName = "screen_shot" + System.currentTimeMillis();
            String savedImageURL = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, fileName, "xxx");
            Log.i("screenshot", "saveImageToGallery image url is :" + savedImageURL);
            Uri uri = Uri.parse(savedImageURL);
            if (uri != null) {
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
            }
            //设置屏幕亮度到最暗
//            if (Settings.System.canWrite(MainActivity.this)) {
//                Settings.System.putInt(getApplicationContext().getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, 0);
//            }
            //熄灭屏幕
//            DevicePolicyManager policyManager = (DevicePolicyManager) getApplicationContext()
//                    .getSystemService(Context.DEVICE_POLICY_SERVICE);
//            ComponentName adminReceiver = new ComponentName(MainActivity.this, ScreenOffAdminReceiver.class);
//            if (policyManager.isAdminActive(adminReceiver)) {
//                policyManager.lockNow();
//            }
            //goToSleep(getApplicationContext());
            if (EVENT_HAPPEN > 0) {
                ShellUtils.execCommand("input keyevent 26", false);
                Log.i("screenshot", "saveImageToGallery: screen off keyevent 26 ");
                EVENT_HAPPEN = 0;
            }

        } catch (Exception e) {
            Log.e("screenshot", "saveImageToGallery: ", e);
        } finally {
            if (out != null) {
                try {
                    out.flush();
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     *   关闭屏幕 ，其实是使系统休眠
     *
     */
    public static void goToSleep(Context context) {
        PowerManager powerManager= (PowerManager)context.getSystemService(Context.POWER_SERVICE);
        try {
            powerManager.getClass().getMethod("goToSleep", new Class[]{long.class}).invoke(powerManager, SystemClock.uptimeMillis());
        } catch (Exception e) {
            Log.e("screenshot", "goToSleep: ", e);
        }
    }



    /**
     * 唤醒屏幕
     * @param context
     */
    public static void wakeUp(Context context) {
        PowerManager powerManager= (PowerManager)context.getSystemService(Context.POWER_SERVICE);
        try {
            powerManager.getClass().getMethod("wakeUp", new Class[]{long.class}).invoke(powerManager, SystemClock.uptimeMillis());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

}
