package com.onlinejudge.DaemonService;

import com.onlinejudge.util.DatabaseUtil;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class DaemonServiceMain {

    // DaemonService 的功能是
    // 监听2331端口,
    // 解析用户发来的 JSON 并根据请求类型调用其他的service模块
    static final String DBG = "[DBG]:";
    private static final int port = 2331;

    public static void debugPrint(String str) {
        System.out.println(DBG + ", " + Thread.currentThread().toString() + " ," + str);
    }

    public static DatabaseUtil databaseUtil = new DatabaseUtil();
    public static void main(String[] args) throws ClassNotFoundException {
        try {
            ServerSocket LoginServSocket = new ServerSocket(port);
            debugPrint("Server is up, listening " + port);
            while (true) {
                Socket sc = LoginServSocket.accept();
                debugPrint("Session started with " + sc.getInetAddress());
                try {
                    Thread td = new Thread(new DaemonServiceRunnable(sc));
                    td.start();
                } catch (Exception e) {
                    System.out.println(DBG + "Something went wrong.");
                    e.printStackTrace();
                    break;
                }
            }
        } catch (IOException ee) {
            ee.printStackTrace();
        }
    }
}
