package com.deepctrl.monitor.screenshot.handler;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import com.deepctrl.monitor.screenshot.Constants;
import com.deepctrl.monitor.screenshot.Screen;
import com.deepctrl.monitor.screenshot.tcpclient.TcpConnector;
import com.deepctrl.monitor.screenshot.util.ByteUtil;
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
        byte[] state = Command.genState(NetUtil.fetchDeviceId(), width, height, Screen.status);
        TcpConnector.getINSTANCE().send(state);
        TcpConnector.getINSTANCE().send(frame);
        Log.i("screenshot-tcp", "frame size is: " + frame.length);

    }

    public static void processAIControl(byte[] data) {
        byte toggle = Command.analysisRevData(data);
        if (toggle == Constants.SCREEN_OPEN) {
            if (Screen.status == Constants.SCREEN_CLOSE) {
                new A64Utility().OpenScreen();
            }
        } else if (toggle == Constants.SCREEN_CLOSE) {
            if (Screen.status == Constants.SCREEN_OPEN) {
                //关闭屏幕
                new A64Utility().CloseScreen();
            }
        }
    }
}
