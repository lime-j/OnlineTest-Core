package com.onlinejudge.ExamService;

public class ExamServiceListedStudent {
    private String sID;
    private String uName;

    public ExamServiceListedStudent(String sID, String uName) {
        this.sID = sID;
        this.uName = uName;
    }

    public String getuName() {
        return this.uName;
    }

    public void setuName(String uName) {
        this.uName = uName;
    }

    public String getsID() {
        return this.sID;
    }

    public void setsID(String sID) {
        this.sID = sID;
    }
}
