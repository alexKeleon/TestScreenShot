package com.deepctrl.monitor.screenshot.util;

import org.apache.commons.codec.binary.Hex;
import org.junit.Test;

import static org.junit.Assert.*;

public class CommandTest {

    @Test
    public void genFrame() {
        byte[] id = NetUtil.fetchDeviceId();
        byte[] bytes = Command.genState(id, (short) 960, (short) 540, (byte)1);
        System.out.println(Hex.encodeHexString(bytes));
    }

    @Test
    public void genState() {
    }
}