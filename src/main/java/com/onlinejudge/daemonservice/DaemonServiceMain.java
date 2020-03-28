package com.onlinejudge.daemonservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class DaemonServiceMain {

    // daemonservice 的功能是
    // 监听2331端口,
    // 解析用户发来的 JSON 并根据请求类型调用其他的service模块
    private static final int PORT = 2331;
    private static final Logger logger = LoggerFactory.getLogger(DaemonServiceMain.class);
    public static void main(String[] args) {
        try {
            ServerSocket loginServSocket = new ServerSocket(PORT);
            logger.info("Server is up, listening {}",PORT);
            while (true) {
                Socket sc = loginServSocket.accept();
                logger.info("Session started with {}", sc.getInetAddress());
                try {
                    Thread td = new Thread(new DaemonServiceRunnable(sc));
                    td.start();
                } catch (Exception e) {
                    logger.error("Something went wrong",e);
                    break;
                } finally {
                    logger.info("finished task");
                }
            }
        } catch (IOException ee) {
            logger.error("IOException", ee);
        }
    }
}
