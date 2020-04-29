package com.deepctrl.monitor.screenshot.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import java.io.ByteArrayOutputStream;

public class ImageProcessor {
    /**
     * 原图应该是1080x1920
     * 960x540
     * @return
     */
    private static final int WIDTH = 960;

    private static final int HEIGHT = 540;

    public static byte[] compress2png(Bitmap bitmap) {
        float scaleWidth = (float)WIDTH / (float) bitmap.getWidth();
        float scaleHeight = (float)HEIGHT / (float) bitmap.getHeight();
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap smallBm = Bitmap.createBitmap(bitmap,0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        smallBm.compress(Bitmap.CompressFormat.PNG, 100, out);
        return out.toByteArray();
    }

    public static int calculateInSampleSize(BitmapFactory.Options options) {
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
