package com.example.gueye.memoireprevention2018.modele;

import android.support.annotation.NonNull;

import com.google.firebase.firestore.Exclude;

/**
 * Created by gueye on 18/09/18.
 */

public class NotificationId {

    @Exclude
    public String notificationId;

    public <T extends NotificationId> T withId(@NonNull final String id) {
        this.notificationId = id;
        return (T) this;
    }
}
