package com.deepctrl.monitor.screenshot.tcpclient;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.deepctrl.monitor.screenshot.util.ByteUtil;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.Arrays;

public class TcpConnector {
    private static TcpConnector INSTANCE = new TcpConnector();

    private static final int MS_SIGNAL = 100;

    private String ip;

    private int port;

    private Socket socket;

    private DataArriveListener listener;

    private Thread connectThread;

    private Handler aiMessageHandler = new AIMessageHandler();

    private static class AIMessageHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MS_SIGNAL:
                    DataArriveListener listener = TcpConnector.getINSTANCE().getListener();
                    if (listener != null) {
                        listener.onReceiveData(msg.getData().getByteArray("data"));
                    }
                    break;
            }
        }
    }

    public static TcpConnector getINSTANCE() {
        return INSTANCE;
    }


    private TcpConnector() {

    }

    public interface DataArriveListener {
        void onReceiveData(byte[] data);
    }

    public void setOnDataArriveListener(DataArriveListener listener) {
        this.listener = listener;
    }

    public void createConnection(String ip, final int port) {
        if (connectThread != null) {
            return;
        }
        INSTANCE.setIp(ip);
        INSTANCE.setPort(port);
        connectThread = new Thread(new Runnable() {
            @Override
            public void run() {
                INSTANCE.connect();
            }
        });
        connectThread.start();
    }

    /**
     * 建立连接后，监听read buffer
     */
    private void connect() {
        try {
            if (socket != null && socket.isConnected()) {
                Log.i("screenshot-tcp", "connect: is connected");
                return;
            }
            if (TextUtils.isEmpty(ip) || port == 0) {
                Log.e("screenshot-tcp", "connect: ip port not init");
                return;
            }
            socket = new Socket(ip, port);

            InputStream inputStream = socket.getInputStream();
            byte[] buffer = new byte[1024];
            int len = -1;
            //同步阻塞读
            while ((len = inputStream.read(buffer)) != -1) {
                byte[] data = Arrays.copyOf(buffer, len);
                Message message = new Message();
                message.what = MS_SIGNAL;
                Bundle bundle = new Bundle();
                bundle.putByteArray("data", data);
                message.setData(bundle);
                aiMessageHandler.sendMessage(message);
                Log.i("screenshot-tcp", "data receive: " +  ByteUtil.bytesToHexStr(data));
            }
        } catch (Exception e) {
            Log.e("screenshot-tcp", "connect or read: error", e);
        }

    }

    public boolean isConnected() {
        if (socket != null) {
            return socket.isConnected();
        }
        return false;
    }

    public void close() {

        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            Log.e("screenshot-tcp", "close: error", e);
        }
    }


    /**
     * 发送数据
     * @param data
     */
    public void send(byte[] data) {
        try {
            if (socket.isConnected()) {
                socket.getOutputStream().write(data);
                Log.i("screenshot-tcp", " data is" + ByteUtil.bytesToHexStr(data));
            }
        } catch (IOException e) {
            Log.e("screenshot-tcp", "sendData: ", e);
        }
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

   public DataArriveListener getListener() {
        return listener;
   }


}
