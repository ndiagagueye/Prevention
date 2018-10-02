package com.example.gueye.memoireprevention2018.modele;


/**
 * Created by gueye on 13/09/18.
 */


public class Users extends UserId {
    public String image, name, telephone,token_id, user_id, status;
    public Users() {}

    public Users(String image, String name, String telephone, String token_id,String user_id,String status) {
        this.image = image;
        this.name = name;
        this.telephone = telephone;
        this.token_id = token_id ;
        this.user_id = user_id;
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getToken_id() {
        return token_id;
    }

    public void setToken_id(String token_id) {
        this.token_id = token_id;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}