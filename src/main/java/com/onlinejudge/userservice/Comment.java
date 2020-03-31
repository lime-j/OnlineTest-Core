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
public class Comment {
    public final String userID;
    public final String text;
    public final String time;

    public Comment(@NotNull String userID, @NotNull String text, @NotNull Timestamp time) {
        this.userID = userID;
        this.text = text;
        this.time = new SimpleDateFormat("yy-MM-dd hh:mm").format(time.toLocalDateTime());
    }
}
