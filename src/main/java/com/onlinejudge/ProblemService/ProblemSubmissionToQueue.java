package com.onlinejudge.ProblemService;

import com.onlinejudge.util.BooleanEvent;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

import static com.onlinejudge.DaemonService.DaemonServiceMain.debugPrint;
import static com.onlinejudge.util.DatabaseUtil.*;

public class ProblemSubmissionToQueue extends BooleanEvent {
    private final static String QUEUE_NAME = "test";
    private final static String USER_NAME = "judger";
    private final static String PASSWORD = "ruanjiangongcheng";
    private Submission CurrSubmission;

    public ProblemSubmissionToQueue(Submission CurrSubmission) {
        // type subUpdate
        this.CurrSubmission = CurrSubmission;
    }

    public boolean go() {
        String cmd;
        if (this.CurrSubmission.getSubID().isEmpty()) {
            this.CurrSubmission.setSubID((UUID.randomUUID().toString()));
        }
        if (!this.CurrSubmission.updateSubmission()) {
            // 将提交放入数据库中
            return false;
        }
        System.out.println("ProblemService: Submission to Queue - \n\t" + this.CurrSubmission.getSubID() + " is added into database");
        try {
            java.sql.Connection conn = getConnection();
            PreparedStatement sta = null;
            sta = prepareStatement("select * from problem where pid=?");
            sta.setString(1, this.CurrSubmission.getSubProb());
            debugPrint("[ProblemService]: find Problem of Submission: SQL:" + sta.toString());
            var ProbResult = sta.executeQuery();
            while (ProbResult.next()) {
                if (ProbResult.getInt("ptype") == 5) {
                    closeQuery(ProbResult, sta, conn);
                    return true;
                }
            }
            closeQuery(ProbResult, sta, conn);

            // 将提交放入RabbitMQ（router为‘test’）中
            ConnectionFactory factory = new ConnectionFactory();
            factory.setUsername(USER_NAME);
            factory.setPassword(PASSWORD);
            factory.setHost("localhost");
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            String message = this.CurrSubmission.getSubID();
            channel.basicPublish("", QUEUE_NAME, null, message.getBytes(StandardCharsets.UTF_8));
            System.out.println(String.format("ProblemService: add Submission to Queue - \n\t%s is pushed into message queue %s", this.CurrSubmission.getSubID(), QUEUE_NAME));
            channel.close();
            connection.close();
        } catch (TimeoutException | IOException | SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}