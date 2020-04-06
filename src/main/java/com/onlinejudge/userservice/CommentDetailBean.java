package com.onlinejudge.userservice;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class CommentDetailBean {
    public final String userID;
    public final String commentID;
    public final String content;
    public final String createDate;
    public final String nickName;
    public final List<ReplyDetailBean> replyList;

    public CommentDetailBean(@NotNull String commentID,
                             @NotNull String userID,
                             @NotNull String text,
                             @NotNull Timestamp time,
                             @NotNull String nickName,
                             @NotNull List<ReplyDetailBean> replyList) {
        this.commentID = commentID;
        this.userID = userID;
        this.content = text;
        this.createDate = new SimpleDateFormat("yy-MM-dd hh:mm").format(time.toLocalDateTime());
        this.nickName = nickName;
        this.replyList = replyList;
    }
}
