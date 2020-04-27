package com.deepctrl.monitor.screenshot.util;

import org.apache.commons.codec.binary.Hex;
import org.junit.Test;

import static org.junit.Assert.*;

public class CRC32Test {

    @Test
    public void genCRC32() {
        String rdn = "101:XNBSC7:XN604";
        try {
            byte[] bytes = rdn.getBytes("US-ASCII");
            System.out.println("para is " + Hex.encodeHexString(bytes));
            int result = CRC32.genCRC32(bytes);
            System.out.println("result is " + Integer.toHexString(result));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testAny() {
        int i = 0x77073096;
        System.out.println(Integer.toHexString(i));
    }
}