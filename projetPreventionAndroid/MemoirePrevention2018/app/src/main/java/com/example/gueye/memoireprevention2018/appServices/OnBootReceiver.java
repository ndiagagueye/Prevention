package com.example.gueye.memoireprevention2018.appServices;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.example.gueye.memoireprevention2018.listeners.ShakeListener;


public class OnBootReceiver extends BroadcastReceiver {

    ShakeListener shakeListener ;
    @Override
    public void onReceive(final Context context, Intent intent) {

        shakeListener = new ShakeListener() {
            @Override
            public void onShake() {

                Toast.makeText(context, "On a remuer le portable ", Toast.LENGTH_SHORT).show();

            }
        };

        shakeListener.init(context);

    }
}
