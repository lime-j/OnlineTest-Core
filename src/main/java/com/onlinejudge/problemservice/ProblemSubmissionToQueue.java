package com.onlinejudge.problemservice;

import com.onlinejudge.util.BooleanEvent;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

import static com.onlinejudge.util.DatabaseUtil.*;

public class ProblemSubmissionToQueue extends BooleanEvent {
    private final static String QUEUE_NAME = "test";
    private final static String USER_NAME = "judger";
    private final static String PASSWORD = "ruanjiangongcheng";
    private Submission CurrSubmission;
    private static Logger logger = LoggerFactory.getLogger(ProblemSubmissionToQueue.class);

    public ProblemSubmissionToQueue(Submission CurrSubmission) {
        // type subUpdate
        this.CurrSubmission = CurrSubmission;
    }

    public boolean go() {
        if (this.CurrSubmission.getSubID().isEmpty()) {
            this.CurrSubmission.setSubID((UUID.randomUUID().toString()));
        }
        if (!this.CurrSubmission.updateSubmission()) {
            // 将提交放入数据库中
            return false;
        }
        logger.info("Submission to Queue - \n\t" + this.CurrSubmission.getSubID() + " is added into database");
        try {
            java.sql.Connection conn = getConnection();
            PreparedStatement sta = null;
            sta = prepareStatement("select * from problem where pid=?");
            sta.setString(1, this.CurrSubmission.getSubProb());
            logger.debug("[problemservice]: find Problem of Submission: SQL:" + sta.toString());
            var probResult = sta.executeQuery();
            while (probResult.next()) {
                if (probResult.getInt("ptype") == 5) {
                    closeQuery(probResult, sta, conn);
                    return true;
                }
            }
            closeQuery(probResult, sta, conn);

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
            logger.error(String.format("problemservice: add Submission to Queue - \n\t%s is pushed into message queue %s", this.CurrSubmission.getSubID(), QUEUE_NAME));
            channel.close();
            connection.close();
        } catch (TimeoutException | IOException | SQLException e) {
            logger.error(e.getMessage(),e);
            return false;
        }
        return true;
    }
}