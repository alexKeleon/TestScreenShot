package com.shine.utilitylib;

/**
 * 关机和开关屏命令
 * A64 设备 需要在/system/lib64 有 相关库
 */
public class A64Utility {

    static {
        System.loadLibrary("A64Utility");
    }

    public native int OpenScreen();
    public native int CloseScreen();
    public native int Shutdown();

}
