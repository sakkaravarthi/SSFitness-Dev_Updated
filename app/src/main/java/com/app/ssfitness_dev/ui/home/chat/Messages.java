package com.app.ssfitness_dev.ui.home.chat;

public class Messages {

    private String message, type, from;
    private long time;
    private boolean seen;

    public Messages(String from) {
        this.from = from;
    }

    public String getFrom() {
        return from;
    }

    public Messages() {
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getMessage() {
        return message;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public void setMessage(String message) {
        this.message = message;
    }



    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Messages(String message, boolean seen, long time, String type) {
        this.message = message;
        this.seen = seen;
        this.time = time;
        this.type = type;
    }
}
