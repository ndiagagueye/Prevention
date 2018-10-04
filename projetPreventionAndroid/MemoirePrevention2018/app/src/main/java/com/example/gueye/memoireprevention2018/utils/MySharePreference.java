package com.example.gueye.memoireprevention2018.utils;


import android.content.Context;
import android.content.SharedPreferences;

import com.example.gueye.memoireprevention2018.modele.UserId;

import java.util.prefs.Preferences;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by gueye on 03/10/18.
 */

public class MySharePreference {

    public  Context context;
    public SharedPreferences pref;

    public MySharePreference(Context context ,SharedPreferences pref){
        this.context = context;
        this.pref = pref;
    }
}
