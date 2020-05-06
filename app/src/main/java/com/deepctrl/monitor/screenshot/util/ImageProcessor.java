package com.deepctrl.monitor.screenshot.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;

import com.deepctrl.monitor.screenshot.entity.DCByteBuffer;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;

public class ImageProcessor {

    private static ImageProcessor INSTANCE = new ImageProcessor();

    /**
     * 原图神州视翰是1080x1920
     * 960x540
     * @return
     */
    private static final int WIDTH = 960;

    private static final int HEIGHT = 540;

    private ByteArrayOutputStream out = new ByteArrayOutputStream();

    private Bitmap smallBm;

    /**
     * 图片png对应的byte array
     */
    private static byte[] pngBytes = new byte[1000000];

    private ImageProcessor () {

    }

    public static ImageProcessor getINSTANCE() {
        return INSTANCE;
    }

    public DCByteBuffer compress2png(Bitmap bitmap) {

        float scaleWidth = (float)WIDTH / (float) bitmap.getWidth();
        float scaleHeight = (float)HEIGHT / (float) bitmap.getHeight();
        if (smallBm == null) {
            smallBm = Bitmap.createBitmap(WIDTH, HEIGHT, Bitmap.Config.ARGB_8888);
        }
        Canvas canvas = new Canvas(smallBm);
        Matrix scaleMatrix = new Matrix();
        scaleMatrix.postScale(scaleWidth, scaleHeight);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bitmap, 0, 0, null);
        out.reset();
        smallBm.compress(Bitmap.CompressFormat.PNG, 100, out);
        int picLen = out.size();
        Arrays.fill(pngBytes, (byte)0);
        out.write(pngBytes, 0, picLen);
        DCByteBuffer DCByteBuffer = new DCByteBuffer();
        DCByteBuffer.setLen(picLen);
        DCByteBuffer.setBytes(pngBytes);
        return DCByteBuffer;
    }

    public int calculateInSampleSize(BitmapFactory.Options options) {
        // 源图片的高度和宽度
        final int height = options.outHeight;
        final int width = options.outWidth;
        int reqHeight = HEIGHT;
        int reqWidth = WIDTH;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            // 计算出实际宽高和目标宽高的比率
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            // 选择宽和高中最小的比率作为inSampleSize的值，这样可以保证最终图片的宽和高
            // 一定都会大于等于目标的宽和高。
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }
}
