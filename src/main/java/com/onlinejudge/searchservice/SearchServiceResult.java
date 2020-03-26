package com.onlinejudge.searchservice;

public class SearchServiceResult {
    // SearchServiceResult 类用于生成JSON String, 约束了返回信息的类型
    private String description;
    private String id;
    private String title;
    private String examSubject;

    public SearchServiceResult(String description, String id, String title) {
        this.description = description;
        this.id = id;
        this.title = title;
    }

    public SearchServiceResult(String description, String id, String title, String examSubject) {
        this.description = description;
        this.id = id;
        this.title = title;
        this.examSubject = examSubject;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getExamSubject() {
        return this.examSubject;
    }

    public void setExamSubject(String examSubject) {
        this.examSubject = examSubject;
    }
}
