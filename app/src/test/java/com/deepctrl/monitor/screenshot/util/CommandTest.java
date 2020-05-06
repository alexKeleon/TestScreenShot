package com.deepctrl.monitor.screenshot.util;

import com.deepctrl.monitor.screenshot.entity.DCByteBuffer;

import org.apache.commons.codec.binary.Hex;
import org.junit.Test;

import java.nio.ByteBuffer;

import static org.junit.Assert.*;

public class CommandTest {

    @Test
    public void genFrame() {

    }

    @Test
    public void genState() {
        byte[] id = NetUtil.fetchDeviceId();
        System.out.println(id.length);
        System.out.println(Hex.encodeHexString(id));
        DCByteBuffer dcByteBuffer = Command.genState(id, (short) 960, (short) 540, (byte)1);
        ByteBuffer ret = ByteBuffer.allocate(dcByteBuffer.getLen());
        for (int i = 0; i < dcByteBuffer.getLen(); ++i) {
            ret.put(dcByteBuffer.getBytes()[i]);
        }

        System.out.println(Hex.encodeHexString(ret.array()));
        System.out.println(ByteUtil.bytesToHexStr(ret.array()));
    }
}