package com.example.gueye.memoireprevention2018.modele;

import android.support.annotation.NonNull;

import com.google.firebase.firestore.Exclude;

/**
 * Created by gueye on 13/09/18.
 */

public class AlerteId {

    @Exclude
    public String alerteId;

    public <T extends AlerteId> T withId(@NonNull final String id) {
        this.alerteId = id;
        return (T) this;
    }
}
