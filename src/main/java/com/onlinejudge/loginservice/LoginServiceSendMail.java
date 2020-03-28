package com.onlinejudge.loginservice;

import com.onlinejudge.util.BooleanEvent;
import com.onlinejudge.util.DatabaseUtil;
import com.onlinejudge.util.InternalException;
import com.sendgrid.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.TestOnly;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.Objects;

public class LoginServiceSendMail extends BooleanEvent {
    private static final Logger logger = LoggerFactory.getLogger(LoginServiceSendMail.class);

    private static final String SG_API_KEY = "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";
    private static final String MAIL_FROM = "noreply@cfmirror.tech";
    private final String email;

    public LoginServiceSendMail(String email) {
        this.email = email;
    }

    @NotNull
    private static String genCode() {
        long l = System.currentTimeMillis();
        return Long.toString(l % 1000000);
    }

    @TestOnly
    public static void main(String[] args) throws InternalException {
        var tmp = new LoginServiceSendMail("solidhtwoo@qq.com");
        tmp.go();
    }

    public boolean go() throws InternalException {

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet res = null;
        String queryID = null;
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement("select uid from userinfo where uid = ?");
            stmt.setString(1, email);
            res = stmt.executeQuery();

            while (res.next()) {
                queryID = res.getString("uid");
            }
            if (queryID == null) {
                DatabaseUtil.closeQuery(res, stmt, conn);
                throw new InternalException("user not found");
            }
        } catch (SQLException e) {
            assert stmt != null;
            logger.warn("sqlerror! {}", stmt.toString());
        } finally {
            try {
                DatabaseUtil.closeQuery(res, stmt, conn);
            } catch (SQLException e) {
                logger.error(e.getMessage(), e);
            }
        }
        if (!Objects.requireNonNull(queryID).equals(email)) throw new InternalException("sun rises from west, please check.");


        Jedis jedis;
        Email from = new Email(MAIL_FROM);
        String subject = "重设密码";
        Email to = new Email(email);
        String code = genCode();
        String mailContent = "[Landingbridge] 您好，您的验证码是" + code + "，如果这不是您发的，请联系solidhtwoo@qq.com";

        // connect to redis

        jedis = new Jedis("localhost");
        logger.debug("Connected to redis");
        jedis.set(String.format("@%s", email), code);
        logger.debug("Binded {} with {}", String.format("r%s", email), code);
        jedis.disconnect();

        Content content = new Content("text/plain", MessageFormat.format(mailContent, code));
        Mail mail = new Mail(from, subject, to, content);
        SendGrid sg = new SendGrid(SG_API_KEY);
        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);
            logger.info(response.getBody());
            logger.info(response.getHeaders().toString());
        } catch (IOException ex) {
            logger.error(ex.getMessage(), ex);
            throw new InternalException("Internal error, IOException");
        }
        return true;
    }
}
