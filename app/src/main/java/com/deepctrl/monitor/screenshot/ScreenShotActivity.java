package com.deepctrl.monitor.screenshot;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Window;

import java.io.File;
import java.util.Date;

public class ScreenShotActivity extends Activity {
    private static int REQUEST_MEDIA_PROJECTION = 0;

    public static final String PIC_DIR_NAME = "myPhotos"; //在系统的图片文件夹下创建了一个相册文件夹，名为“myPhotos"，所有的图片都保存在该文件夹下。

    private File mPicDir = new File(Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_PICTURES), PIC_DIR_NAME); //图片统一保存在系统的图片文件夹中

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //setTheme(android.R.style.Theme_Dialog);//这个在这里设置 之后导致 的问题是 背景很黑
        super.onCreate(savedInstanceState);

        //如下代码 只是想 启动一个透明的Activity 而上一个activity又不被pause
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        getWindow().setDimAmount(0f);

        requestScreenShot();
    }

    public void requestScreenShot() {
        MediaProjectionManager mediaProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        startActivityForResult(mediaProjectionManager.createScreenCaptureIntent(), REQUEST_MEDIA_PROJECTION);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_MEDIA_PROJECTION) {
            if (resultCode == RESULT_OK && data != null) {
                ScreenShotHelper screenShotHelper = new ScreenShotHelper(ScreenShotActivity.this, resultCode, data, new ScreenShotHelper.OnScreenShotListener() {
                    @Override
                    public void onFinish(Bitmap bitmap) {
                        Log.d("sceenshot onFinish", "sceenshot save: " + new Date().
                                toString());
                        saveImageToGallery(bitmap);
                        finish();

                    }
                });
                screenShotHelper.startScreenShot();
            }
        }
    }

    public void saveImageToGallery(Bitmap bitmap) {
        try {
            mPicDir.mkdirs();
            String fileName = "screen_shot" + System.currentTimeMillis();
            String savedImageURL = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, fileName, "xxx");
            System.out.println(savedImageURL);
            Uri uri = Uri.parse(savedImageURL);
            if (uri != null) {
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

