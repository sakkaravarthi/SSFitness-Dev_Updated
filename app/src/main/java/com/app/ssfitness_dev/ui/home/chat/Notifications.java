package com.app.ssfitness_dev.ui.home.chat;

public class Notifications {
    private String userId;
    private String from;
    private String reqTYpe;
    private String reqId;

    public Notifications(){

    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getReqTYpe() {
        return reqTYpe;
    }

    public void setReqTYpe(String reqTYpe) {
        this.reqTYpe = reqTYpe;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Notifications(String from,String reqTYpe) {
        this.from = from;
        this.reqTYpe=reqTYpe;
    }

    public Notifications(String from, String reqTYpe, String reqId) {
        this.from = from;
        this.reqTYpe = reqTYpe;
        this.reqId = reqId;
    }

    public String getReqId() {
        return reqId;
    }

    public void setReqId(String reqId) {
        this.reqId = reqId;
    }
}
