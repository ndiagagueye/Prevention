package com.example.gueye.memoireprevention2018.modele;


/**
 * Created by gueye on 13/08/18.
 */

// MODEL CLASS

public class BlogPost extends BlogPostId{
    public String description ,user_id, image_url, image_thumb;

    double longitude, latitude;
    public String timestamp;
    public int type;
    public BlogPost(){}

    public BlogPost(int type, String description, double longitude, double latitude, String user_id, String image_url, String image_thumb, String timestamp) {
        this.type = type;
        this.description = description;
        this.longitude = longitude;
        this.latitude = latitude;
        this.user_id = user_id;
        this.image_url = image_url;
        this.image_thumb = image_thumb;
        this.timestamp = timestamp;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getImage_thumb() {
        return image_thumb;
    }

    public void setImage_thumb(String image_thumb) {
        this.image_thumb = image_thumb;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
