package com.onlinejudge.userservice;

import com.onlinejudge.util.Provider;

import java.util.List;

public interface TimelineItemProvider extends Provider {
    List<TimelineItem> getItem(String id);
}
