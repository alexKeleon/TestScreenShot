package com.deepctrl.monitor.screenshot.util;

import org.apache.commons.codec.binary.Hex;
import org.junit.Test;

import static org.junit.Assert.*;

public class NetUtilTest {

    @Test
    public void fetchDeviceId() {
        byte[] bytes = NetUtil.fetchDeviceId();
        System.out.println(Hex.encodeHexString(bytes));
    }
}