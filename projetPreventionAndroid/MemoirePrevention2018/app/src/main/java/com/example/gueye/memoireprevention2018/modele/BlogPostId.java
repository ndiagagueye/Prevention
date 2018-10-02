package com.example.gueye.memoireprevention2018.modele;

import android.support.annotation.NonNull;

import com.google.firebase.firestore.Exclude;

/**
 * Created by gueye on 14/08/18.
 */

public class BlogPostId {
    @Exclude
    public String blogPostId;

    public <T extends BlogPostId> T withId(@NonNull final String id) {
        this.blogPostId = id;
        return (T) this;
    }

}
