package com.onlinejudge.userservice;


import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.sql.Timestamp;

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

    public boolean equals(TimelineItem o) {
        if (o == null) return false;
        return this.time.equals(o.time);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }

    public Timestamp getTimeStamp() {
        return this.time;
    }
}
