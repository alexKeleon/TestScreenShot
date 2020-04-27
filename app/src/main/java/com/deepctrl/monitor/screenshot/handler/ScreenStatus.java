package com.deepctrl.monitor.screenshot.handler;

public class ScreenStatus {
    /**
     * 维持一个全局的变量
     * 0 代表屏幕正常， 1 代表关闭
     */
    public static volatile byte status = 0x00;
}
