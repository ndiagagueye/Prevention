package com.example.gueye.memoireprevention2018.ModeleService;

/**
 * Created by gueye on 15/09/18.
 */

public class Sender {


    public Notification notification ;
    public String to ;

    public Sender(){}

    public Sender(String to, Notification notification ) {
        this.notification = notification;
        this.to = to;
    }

    public Notification getNotification() {
        return notification;
    }

    public void setNotification(Notification notification) {
        this.notification = notification;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }
}
