package com.onlinejudge.userservice;


import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.sql.Timestamp;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
public class TimelineItem implements Comparable<TimelineItem> {
    private String name;
    private String description;
    private int type;
    private String uid;
    private Timestamp time;

    @Override
    public int compareTo(@NotNull TimelineItem o) {
        return this.time.compareTo(o.time);
    }

}
