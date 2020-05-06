package com.deepctrl.monitor.screenshot.entity;

import java.io.ByteArrayOutputStream;

public class DCByteArrayOutputStream extends ByteArrayOutputStream {
    public void copyDataToMem(byte[] dist) {
        if (dist.length < count) {
            throw new IllegalArgumentException("Negative dist array size: "
                    + dist.length);
        }
        for (int i = 0; i < count; ++i) {
            dist[i] = buf[i];
        }
    }
}
