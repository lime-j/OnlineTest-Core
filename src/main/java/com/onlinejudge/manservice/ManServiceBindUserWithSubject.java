package com.onlinejudge.manservice;

import com.onlinejudge.util.BooleanEvent;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Objects;

import static com.onlinejudge.util.DatabaseUtil.closeConnection;
import static com.onlinejudge.util.DatabaseUtil.prepareStatement;

@Log4j2
public class ManServiceBindUserWithSubject implements BooleanEvent {
    private final String subject;
    private final String userID;

    public ManServiceBindUserWithSubject(@NotNull String subject, @NotNull String userID) {
        this.subject = subject;
        this.userID = userID;
    }

    @Override
    public void beforeGo() {
        // do nothing
    }

    @Override
    public void afterGo() {
        // do nothing
    }

    @Override
    public boolean go() {
        // 将教师执教科目插入到数据库
        // requestType: addTeacherSubject
        PreparedStatement staInsertTea = null;
        try {
            staInsertTea = prepareStatement("insert into teasubject (subject, teacherid) values(?, ?)");
            staInsertTea.setString(1, subject);
            staInsertTea.setString(2, userID);
            staInsertTea.executeUpdate();
        } catch (SQLException e) {
            log.debug(e.getMessage(), e);
            return false;
        } finally {
            try {
                Objects.requireNonNull(staInsertTea).close();
            } catch (SQLException e) {
                log.debug(e.getMessage(), e);
            }
            closeConnection();
        }
        return true;
    }
}
