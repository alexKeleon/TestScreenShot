package com.deepctrl.monitor.screenshot.util;

import android.util.Log;

import com.deepctrl.monitor.screenshot.entity.DCByteBuffer;

import java.nio.ByteBuffer;

public class Command {
    private static  final byte STATE = 0x34;

    private static  final byte FRAME = 0x35;

    private static  final byte CONTROL = (byte) 0xB4;

    private static final byte[] header = {(byte)0xAA, (byte)0xBB, (byte)0xCC, (byte)0xDD};

    private static ByteBuffer sateByteBuff = ByteBuffer.allocate(13);

    private static ByteBuffer frameByteBuff = ByteBuffer.allocate(1000000);

    private static ByteBuffer commandByteBuff = ByteBuffer.allocate(1000013);


    private static DCByteBuffer genCommandData(byte command, ByteBuffer data, int buffLen) {

        commandByteBuff.clear();
        DCByteBuffer dcByteBuffer = new DCByteBuffer();
        //计算帧长
        int dataLen = 13 + buffLen;
        commandByteBuff.put(header).put(command).putInt(dataLen);
        for (int i = 0; i < buffLen; ++i) {
            commandByteBuff.put(data.get(i));
        }
        //dataLen - 4
        //计算crc32
        int crc = CRC32.genCRC32(commandByteBuff.array(), dataLen - 4);
        dcByteBuffer.setBytes(commandByteBuff.putInt(crc).array());
        dcByteBuffer.setLen(dataLen);
        return dcByteBuffer;
}

    /**
     * id + 4 + 2 + pic
     * unsigned int to int
     * 超过int + 范围的数字，只要在4个字节内（不管有符号无符号）
     * @param id
     * @param DCByteBuffer
     * @return
     */
    public static DCByteBuffer genFrame(byte[] id, DCByteBuffer DCByteBuffer) {
        frameByteBuff.clear();
        long now = System.currentTimeMillis();
        int mtime = (int)(now / 1000);
        short stime = (short)(now % 1000);
        int length = 8 + 4 + 2 + DCByteBuffer.getLen();
        ByteBuffer data = frameByteBuff
                .put(id).putInt(mtime).putShort(stime);
        //取图片的有效字节
        for (int i = 0; i < DCByteBuffer.getLen(); ++i) {
            data.put(DCByteBuffer.getBytes()[i]);
        }
        return genCommandData(FRAME, data, length);
    }

    public static DCByteBuffer genState(byte[] id, short width, short height, byte shutState) {
        sateByteBuff.clear();
        ByteBuffer data = sateByteBuff
                .put(id).putShort(width).putShort(height).put(shutState);
        return genCommandData(STATE, data, 13);
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
