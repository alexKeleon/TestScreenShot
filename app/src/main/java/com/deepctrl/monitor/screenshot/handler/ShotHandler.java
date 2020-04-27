package com.deepctrl.monitor.screenshot.handler;

import android.content.ContentResolver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import com.deepctrl.monitor.screenshot.tcpclient.Connection;
import com.deepctrl.monitor.screenshot.util.Command;
import com.deepctrl.monitor.screenshot.util.ImageProcessor;
import com.deepctrl.monitor.screenshot.util.NetUtil;
import com.deepctrl.monitor.screenshot.util.SysUtil;
import com.shine.utilitylib.A64Utility;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

/**
 * 截图后的操作
 */
public class ShotHandler {
    /**
     * 事件发生
     */
    private static int EVENT_HAPPEN = 1;

    public static final String PIC_DIR_NAME = "myPhotos"; //在系统的图片文件夹下创建了一个相册文件夹，名为“myPhotos"，所有的图片都保存在该文件夹下。

    private static File mPicDir = new File(Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_PICTURES), PIC_DIR_NAME); //图片统一保存在系统的图片文件夹中

    public static void saveImageToGallery(Bitmap bitmap, ContextWrapper contextWrapper) {
        OutputStream out = null;
        try {
            mPicDir.mkdirs();
            String fileName = "screen_shot" + System.currentTimeMillis();
            String savedImageURL = MediaStore.Images.Media.insertImage(contextWrapper.getContentResolver(), bitmap, fileName, "xxx");
            Log.i("screenshot", "saveImageToGallery image url is :" + savedImageURL);
            Uri uri = Uri.parse(savedImageURL);
            if (uri != null) {
                contextWrapper.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
            }
            //设置屏幕亮度到最暗
//            if (Settings.System.canWrite(MainActivity.this)) {
//                Settings.System.putInt(getApplicationContext().getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, 0);
//            }
            //熄灭屏幕
            Log.i("screenshot", "saveImageToGallery: EVENT_HAPPEN is: " + EVENT_HAPPEN);
            if (EVENT_HAPPEN > 0) {
//                ShellUtils.execCommand("input keyevent 26", false);
//                Log.i("screenshot", "saveImageToGallery: screen off keyevent 26 ");
                new A64Utility().CloseScreen();
                Log.i("screenshot", "saveImageToGallery: close screen ");
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


    public static void sendImage2AICenter(Bitmap bitmap, Context context) {
        byte[] pngBytes = ImageProcessor.compress2png(bitmap);
        byte[] frame = Command.genFrame(NetUtil.fetchDeviceId(), pngBytes);
        short width = (short) SysUtil.getWindowWidth(context);
        short height = (short) SysUtil.getWindowHeight(context);
        byte[] state = Command.genState(NetUtil.fetchDeviceId(), width, height, ScreenStatus.status);
        Connection.getINSTANCE().sendData(frame);
        Connection.getINSTANCE().sendData(state);

    }
}
