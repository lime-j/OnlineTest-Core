package com.onlinejudge.daemonservice;

import com.alibaba.fastjson.JSON;
import com.onlinejudge.loginservice.WrongPasswordException;
import com.onlinejudge.userservice.TokenWrongException;
import com.onlinejudge.userservice.UserServiceCheckToken;
import com.onlinejudge.util.Handler;
import com.onlinejudge.util.HandlerFactory;
import org.jetbrains.annotations.Contract;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class DaemonServiceRunnable implements Runnable {
    private Socket cl;

    @Contract(pure = true)
    DaemonServiceRunnable(Socket sc) {
        this.cl = sc;
    }

    private static Logger logger = LoggerFactory.getLogger(DaemonServiceRunnable.class);

    public void run() {
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(this.cl.getInputStream()));
            String recv = bufferedReader.readLine();
            logger.info(recv);
            Handler handler = null;
            var jsonObject = JSON.parseObject(recv);
            String requestType = jsonObject.getString("requestType");
            String userToken = jsonObject.getString("userToken");
            if (requestType == null || userToken == null) throw new InvalidRequestException();
            String userID = jsonObject.getString("userID");
            if (!requestType.equals("login") && !requestType.equals("sendMain") && !requestType.equals("changePassword")) {
                UserServiceCheckToken.checkToken(userID, userToken);
            }
            handler = HandlerFactory.getHandler(requestType, jsonObject);
            assert handler != null;
            logger.info(handler.result);
            this.cl.getOutputStream().write(handler.result.getBytes(StandardCharsets.UTF_8));
            Thread.sleep(50);
            this.cl.close();
        } catch (WrongPasswordException e) {
            try {
                logger.warn("Wrong Passwd or ID");
                this.cl.getOutputStream().write("{\"uName\":\"-1\",\"uSex\":-1,\"uType\":-1,\"uuid\":\"-1\"}".getBytes(StandardCharsets.UTF_8));
            } catch (IOException ex) {
                logger.error(e.getMessage(), e);
            }
        } catch (TokenWrongException wa) {
            logger.warn("The token is wrong.");
            try {
                this.cl.getOutputStream().write("{\"status\":-2}".getBytes(StandardCharsets.UTF_8));
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        } catch (Exception ee) {
            logger.error("Oops.Something is wrong.", ee);
            try {
                this.cl.getOutputStream().write("{\"status\":-1}".getBytes(StandardCharsets.UTF_8));
            } catch (IOException e) {
                logger.error("IOException", e);
            }
        } finally {
            try {
                this.cl.close();
            } catch (IOException e) {
                logger.error("IOException", e);
            }
        }
    }
}
