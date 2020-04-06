package com.onlinejudge.userservice;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
public class ReplyDetailBean {
    private String nickName;
    private String userID;
    private String content;
    private String createDate;
    private String commentID;

    public ReplyDetailBean(@NotNull String nickName,
                           @NotNull String userID,
                           @NotNull String content,
                           @NotNull Timestamp createDate,
                           @NotNull String commentID) {
        this.nickName = nickName;
        this.userID = userID;
        this.content = content;
        this.commentID = commentID;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        this.createDate = formatter.format((new Date(createDate.getTime())));
    }
}
