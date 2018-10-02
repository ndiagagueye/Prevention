package com.example.gueye.memoireprevention2018.modele;

import java.util.Date;

public class Chat {

    private String from;
    private String to;
    private String message;
    private Date  timeMsgSend;

    public Chat(String from, String to, String message) {
        this.from = from;
        this.to = to;
        this.message = message;

    }

    public Chat(String from, String to, String message, Date timeMsgSend) {
        this.from = from;
        this.to = to;
        this.message = message;
        this.timeMsgSend = timeMsgSend;
    }

    public Chat() {
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getTimeMsgSend() {
        return timeMsgSend;
    }

    public void setTimeMsgSend(Date timeMsgSend) {
        this.timeMsgSend = timeMsgSend;
    }
}
