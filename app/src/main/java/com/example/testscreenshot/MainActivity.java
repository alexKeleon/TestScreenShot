package com.example.testscreenshot;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private static int REQUEST_MEDIA_PROJECTION = 0;

    private Button mBtn;
    private ImageView mImageView;

    public static final String PIC_DIR_NAME = "myPhotos"; //在系统的图片文件夹下创建了一个相册文件夹，名为“myPhotos"，所有的图片都保存在该文件夹下。

    private File mPicDir = new File(Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_PICTURES), PIC_DIR_NAME); //图片统一保存在系统的图片文件夹中

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBtn = findViewById(R.id.btn);
        mImageView = findViewById(R.id.iv);

        mBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try2StartScreenShot();
            }
        });
    }

    private void try2StartScreenShot() {
        MediaProjectionManager mediaProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        startActivityForResult(mediaProjectionManager.createScreenCaptureIntent(), REQUEST_MEDIA_PROJECTION);
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
//                Intent intent = new Intent(this, ScreenShotService.class);
//                startService(intent);
            }
        }
    }

    public void saveImageToGallery(Bitmap bitmap) {
        OutputStream out = null;
        try {
            mPicDir.mkdirs();
            String fileName = "screen_shot" + System.currentTimeMillis();
            String savedImageURL = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, fileName, "xxx");
            System.out.println(savedImageURL);
            Uri uri = Uri.parse(savedImageURL);
            if (uri != null) {
//                out = getContentResolver().openOutputStream(uri);
//                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
            }

        } catch (Exception e) {
            e.printStackTrace();
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

}
