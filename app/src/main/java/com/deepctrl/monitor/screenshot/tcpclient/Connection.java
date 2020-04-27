package com.deepctrl.monitor.screenshot.tcpclient;

import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.net.Socket;

public class Connection {
    private static Connection INSTANCE = new Connection();

    private String ip;

    private int port;

    private Socket socket;

    public static Connection getINSTANCE() {
        return INSTANCE;
    }

    public static Runnable network =  new Runnable() {
        @Override
        public void run() {
            Connection.getINSTANCE().setIp("192.168.1.33");
            Connection.getINSTANCE().setPort(3000);
            Connection.getINSTANCE().connect();
        }
    };

    private Connection() {

    }

    public Connection connect() {
        try {
            if (this.socket != null && this.socket.isConnected()) {
                Log.i("screenshot-tcp", "connect: is connected");
                return this;
            }
            if (TextUtils.isEmpty(ip) || port == 0) {
                Log.e("screenshot-tcp", "connect: ip port not init");
                return null;
            }
            this.socket = new Socket(ip, port);
        } catch (IOException e) {
            Log.e("screenshot-tcp", "connect: error", e);
        }
        return this;
    }

    public void close() {

        try {
            this.socket.close();
        } catch (IOException e) {
            Log.e("screenshot-tcp", "connect: error", e);
        }
    }

    public void sendData(byte[] data) {
        try {
            this.socket.getOutputStream().write(data);
            this.socket.getOutputStream().flush();
        } catch (IOException e) {
            Log.e("screenshot-tcp", "sendData: ", e);
        }
    }

    public int receiveData(byte[] data) {
        try {
            return this.socket.getInputStream().read(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
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

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }
}
