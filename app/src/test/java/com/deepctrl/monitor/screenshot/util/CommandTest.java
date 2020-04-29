package com.deepctrl.monitor.screenshot.util;

import org.apache.commons.codec.binary.Hex;
import org.junit.Test;

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
        byte[] bytes = Command.genState(id, (short) 960, (short) 540, (byte)1);
        System.out.println(Hex.encodeHexString(bytes));
        System.out.println(ByteUtil.bytesToHexStr(bytes));
    }
}