package com.onlinejudge.userservice;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

@Getter
@Setter
@AllArgsConstructor
public class ReplyDetailBean {
    private String nickName;
    private String userID;
    private String content;
//    private String status;
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
       // this.status = status;
        this.commentID = commentID;
        this.createDate = new SimpleDateFormat(
                "yy-MM-dd hh:mm").format(createDate.toLocalDateTime());
    }
}
