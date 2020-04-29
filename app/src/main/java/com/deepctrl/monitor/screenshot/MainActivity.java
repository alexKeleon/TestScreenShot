package com.deepctrl.monitor.screenshot;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.deepctrl.monitor.screenshot.handler.ShotHandler;
import com.deepctrl.monitor.screenshot.tcpclient.TcpConnector;
import com.shine.utilitylib.A64Utility;

public class MainActivity extends AppCompatActivity {

    private static int REQUEST_MEDIA_PROJECTION = 0;

    private static int REQUEST_MANAGE_SETTINGS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new A64Utility().OpenScreen();
        //todo 弹窗体填写ip和端口号的弹窗
        TcpConnector.getINSTANCE().createConnection("192.168.1.33", 3000);
        TcpConnector.getINSTANCE().setOnDataArriveListener(new TcpConnector.DataArriveListener() {
            @Override
            public void onReceiveData(byte[] data) {
                ShotHandler.processAIControl(data);
            }
        });
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
                //注册截图后的处理函数
                ScreenShotHelper screenShotHelper = new ScreenShotHelper(MainActivity.this, resultCode, data, new ScreenShotHelper.OnScreenShotListener() {
                    @Override
                    public void onFinish(Bitmap bitmap) {
                        //ShotHandler.saveImageToGallery(bitmap, MainActivity.this);
                        ShotHandler.sendImage2AICenter(bitmap, MainActivity.this);
                    }
                });
                ScreenShotApp.getInstance().setScreenShotHelper(screenShotHelper);
                //启动job
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

}
