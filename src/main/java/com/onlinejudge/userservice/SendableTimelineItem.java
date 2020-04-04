package com.onlinejudge.userservice;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Date;

@Getter
@Setter
public class SendableTimelineItem {
    private String itemName;
    private String itemDescription;
    private int itemType;
    private String uid;
    private String itemTime;

    public SendableTimelineItem(@NotNull TimelineItem t) {
        this.itemName = t.getName();
        this.itemDescription = t.getDescription();
        this.itemType = t.getType();
        this.uid = t.getUid();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        itemTime = formatter.format((new Date(t.getTime().getTime())));
    }

}
