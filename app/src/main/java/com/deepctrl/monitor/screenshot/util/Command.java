package com.deepctrl.monitor.screenshot.util;

import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class Command {
    private static  final byte STATE = 0x34;

    private static  final byte FRAME = 0x35;

    private static  final byte CONTROL = (byte) 0xB4;

    private static final byte[] header = {(byte)0xAA, (byte)0xBB, (byte)0xCC, (byte)0xDD};


    /**
     * 这个值可能需要调整
     */
    //private static byte[] pngBytes = new byte[1000000];

    //private static

    private static byte[] genCommandData(byte command, byte[] data) {
        //计算帧长
        int dataLen = 13 + data.length;
        byte[] bytes = ByteBuffer.allocate(dataLen - 4)
                .put(header).put(command).putInt(dataLen).put(data).array();
        //计算crc32
        int crc = CRC32.genCRC32(bytes);
        return ByteBuffer.allocate(dataLen)
                .put(header).put(command).putInt(dataLen).put(data).putInt(crc).array();
    }

    /**
     * id + 4 + 2 + pic
     * unsigned int to int
     * 超过int + 范围的数字，只要在4个字节内（不管有符号无符号）
     * @param id
     * @param pngOut
     * @return
     */
    public static byte[] genFrame(byte[] id, byte[] pngBytes) {
//        int picLen = pngOut.size();
//        Arrays.fill(pngBytes, (byte)0);
//        pngOut.write(pngBytes, 0, picLen);
        long now = System.currentTimeMillis();
        int mtime = (int)(now / 1000);
        short stime = (short)(now % 1000);
        int length = 8 + 4 + 2 + pngBytes.length;
        byte[] data = ByteBuffer.allocate(length)
                .put(id).putInt(mtime).putShort(stime).put(pngBytes).array();
        return genCommandData(FRAME, data);
    }

    public static byte [] genState(byte[] id, short width, short height, byte shutState) {
        byte[] data = ByteBuffer.allocate(13)
                .put(id).putShort(width).putShort(height).put(shutState).array();
        return genCommandData(STATE, data);
    }

    /**
     * 解析客户端接收的control指令
     * @param data
     * @return 0-开屏，1-关屏
     */
    public static byte analysisRevData(byte[] data) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(data);
        int header = byteBuffer.getInt(0);
        if (header != (int)0xAABBCCDD) {
            Log.i("screen-shot tcp", "analysisRevData: header有问题");
            return -1;
        }
        if (byteBuffer.get(4) != CONTROL) {
            Log.i("screen-shot tcp", "analysisRevData: 非法指令" + byteBuffer.get(4));
            return -1;
        }

        byte state = byteBuffer.get(9);
        Log.i("screen-shot tcp", "analysisRevData: control is" + state);
        return state;
    }
}
