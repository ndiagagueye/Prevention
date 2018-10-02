package com.example.gueye.memoireprevention2018.appServices;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.example.gueye.memoireprevention2018.activities.AlerteListenerActivity;
import com.example.gueye.memoireprevention2018.listeners.ShakeListener;
import com.example.gueye.memoireprevention2018.utils.NotificationShow;
import com.example.gueye.memoireprevention2018.utils.SendEmergencyAlert;


public class ShakeService extends Service {

    public static final String TAG = "ShakeService";
    
    private ShakeListener shakeListener;

   SendEmergencyAlert sendEmergencyAlert;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        sendEmergencyAlert = new SendEmergencyAlert(getApplicationContext());

        Toast.makeText(this, " service démaré ", Toast.LENGTH_SHORT).show();
        shakeListener = new ShakeListener() {
            @Override
            public void onShake() {


               sendEmergencyAlert.verifyServices(getApplicationContext());

            }
        };

        shakeListener.init(getApplicationContext());

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        sendEmergencyAlert = new SendEmergencyAlert(getApplicationContext());


        return START_STICKY;
    }



}
