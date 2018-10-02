package com.example.gueye.memoireprevention2018.modele;

import android.support.annotation.NonNull;

import com.google.firebase.firestore.Exclude;

/**
 * Created by gueye on 14/08/18.
 */

public class CommentsId {

    @Exclude
    public String commentId;

    public <T extends CommentsId> T withId(@NonNull final String id) {
        this.commentId = id;
        return (T) this;
    }
}
