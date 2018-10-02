package com.example.gueye.memoireprevention2018.modele;

import android.support.annotation.NonNull;

import com.google.firebase.firestore.Exclude;

public class UserId {

    @Exclude
    public String usersId;

    public <T extends UserId> T withId(@NonNull final String id) {
        this.usersId = id;
        return (T) this;
    }
}
