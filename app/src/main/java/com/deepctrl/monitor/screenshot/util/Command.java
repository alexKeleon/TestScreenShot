package com.deepctrl.monitor.screenshot.util;

import android.content.Intent;

import com.deepctrl.monitor.screenshot.util.CRC32;

import java.nio.ByteBuffer;

public class Command {
    public static  byte STATE = 0x33;

    public static  byte FRAME = 0x34;

    public static  byte CONTROL = (byte) 0xB4;

    public static byte[] header = {(byte)0xAA, (byte)0xBB, (byte)0xCC, (byte)0xDD};

    private static byte[] genCommandData(byte command, byte[] data) {
        //计算帧长
        int dataLen = 13 + data.length;
        byte[] bytes = ByteBuffer.allocate(dataLen - 4)
                .put(header).put(command).putInt(dataLen).put(data).array();
        //计算crc32
        int crc = CRC32.genCRC32(bytes);
        byte[] crcBytes = ByteBuffer.allocate(dataLen)
                .put(header).put(command).putInt(dataLen).put(data).array();

        return crcBytes;
    }

    /**
     * id + 4 + 2 + pic
     * unsigned int to int
     * 超过int + 范围的数字，只要在4个字节内（不管有符号无符号）
     * @param id
     * @param pic
     * @return
     */
    public static byte[] genFrame(byte[] id, byte[] pic) {
        long now = System.currentTimeMillis();
        int mtime = (int)(now / 1000);
        short stime = (short)(now % 1000);
        int length = 8 + 4 + 2 + pic.length;
        byte[] data = ByteBuffer.allocate(length)
                .put(id).putInt(mtime).putShort(stime).put(pic).array();
        return genCommandData(FRAME, data);
    }

    public static byte [] genState(byte[] id, short width, short height, byte shutState) {
        byte[] data = ByteBuffer.allocate(13)
                .put(id).putShort(width).putShort(height).put(shutState).array();
        return genCommandData(STATE, data);
    }

    public static int analysisControl(byte[] data) {
        return 0;
    }
}
