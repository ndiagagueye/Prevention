package com.example.gueye.memoireprevention2018.modele;

/**
 * Created by gueye on 18/09/18.
 */

public class Notifications extends NotificationId{

    String from, description, image, date;
    int type ;

    public Notifications() {

    }

    public String getFromNotif() {
        return from;
    }

    public void setFromNotif(String from) {
        this.from = from;
    }

    public int getTypeNotif() {
        return type;
    }

    public void getTypeNotif(int type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTime() {
        return date;
    }

    public void setTime(String timestamp) {
        this.date = timestamp;
    }

    public Notifications(String from, int type, String image, String description, String timestamp) {
        this.from = from;
        this.type = type;
        this.description = description ;
        this.date = date ;
        this.image = image  ;
    }
}