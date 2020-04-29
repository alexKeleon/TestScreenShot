package com.deepctrl.monitor.screenshot.util;

public class ByteUtil {
    public static String bytesToHexStr(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for(byte b: bytes)
            sb.append(String.format("%02x", b));
        return sb.toString();
    }
}
