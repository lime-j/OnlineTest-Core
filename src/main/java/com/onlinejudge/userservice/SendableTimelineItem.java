package com.onlinejudge.userservice;

import java.text.SimpleDateFormat;

public class SendableTimelineItem {
    private String name;
    private String description;
    private int type;
    private String uid;
    private String time;

    public SendableTimelineItem(TimelineItem t) {
        this.name = t.getName();
        this.description = t.getDescription();
        this.type = t.getType();
        this.uid = t.getUid();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        time = formatter.format(new java.util.Date(t.getTimeStamp().getTime()));
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

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
