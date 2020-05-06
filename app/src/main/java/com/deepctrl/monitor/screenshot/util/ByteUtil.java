package com.deepctrl.monitor.screenshot.util;

public class ByteUtil {
    public static String bytesToHexStr(byte[] bytes, int len) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for(int i = 0; i < len; ++i)
            sb.append(String.format("%02x", bytes[i]));
        return sb.toString();
    }

    public static String bytesToHexStr(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for(byte b : bytes)
            sb.append(String.format("%02x", b));
        return sb.toString();
    }
}
