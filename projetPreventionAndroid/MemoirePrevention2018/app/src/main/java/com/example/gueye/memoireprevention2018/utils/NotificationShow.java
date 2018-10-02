package com.example.gueye.memoireprevention2018.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import android.support.v4.app.NotificationManagerCompat;
import android.widget.Toast;


import com.example.gueye.memoireprevention2018.R;
import com.example.gueye.memoireprevention2018.activities.AlerteListenerActivity;

public class NotificationShow extends AsyncTask<Void,Void,Void> {


    public static final String CHANNEL_ID = "test";
    private Context context;
    private int notificationId = 120;

    public NotificationShow(Context context){

        this.context = context;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }

    @Override
    protected Void doInBackground(Void... voids) {

        createNotification();

        return null;
    }


    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

    }

    @Override
    protected void onCancelled() {
        super.onCancelled();


        Toast.makeText(context, "Cancelled ", Toast.LENGTH_SHORT).show();
    }

    private void createNotification() {

        Builder notificationCompat = new Builder(context, CHANNEL_ID);

        notificationCompat.setSmallIcon(R.drawable.profile_icon);
        notificationCompat.setContentTitle("username");
        notificationCompat.setContentText("Alerte envoyé avec sucés ");
        notificationCompat.setPriority(NotificationCompat.PRIORITY_DEFAULT );

        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(context);

        managerCompat.notify(notificationId , notificationCompat.build());


        cancel(true);
    }
}
