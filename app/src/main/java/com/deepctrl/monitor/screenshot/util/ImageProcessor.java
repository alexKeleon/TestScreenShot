package com.deepctrl.monitor.screenshot.util;

import android.graphics.Bitmap;
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
        float scaleHeight = (float) HEIGHT / (float) bitmap.getHeight();
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap smallBm = Bitmap.createBitmap(bitmap,0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        smallBm.compress(Bitmap.CompressFormat.PNG, 100, out);
        return out.toByteArray();
    }
}
