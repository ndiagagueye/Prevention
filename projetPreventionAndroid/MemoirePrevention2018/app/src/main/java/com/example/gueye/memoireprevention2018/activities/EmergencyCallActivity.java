package com.example.gueye.memoireprevention2018.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gueye.memoireprevention2018.ModeleService.Common;
import com.example.gueye.memoireprevention2018.ModeleService.MyResponse;
import com.example.gueye.memoireprevention2018.ModeleService.Notification;
import com.example.gueye.memoireprevention2018.ModeleService.Sender;
import com.example.gueye.memoireprevention2018.R;
import com.example.gueye.memoireprevention2018.Remote.APIService;
import com.example.gueye.memoireprevention2018.services.GoogleMapsServices;
import com.example.gueye.memoireprevention2018.utils.Const;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EmergencyCallActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private TextView txvResult;
    private FirebaseFirestore mFirestore;
    private String user_id;
    private String saveDate;
    private String saveCurrentTime;
    private String currentTime;
    private CardView listener_sensor;
    private static final String TAG = "Alerte";
    APIService mService;
    GoogleMapsServices mapsServices;
    private boolean isLocationFound = false;
    private boolean isPermissionsGranted = false;
    public FusedLocationProviderClient mFusedLocationProviderClient;
    private Location currentLocationDevice;
    private double latitude  ;
    private double longitude ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView(R.layout.activity_emergency_call );

        verifyServices();
        init();


    }

    private void subscribeToTopic() {
        FirebaseMessaging.getInstance().subscribeToTopic("alerte");
        Common.currentToken = FirebaseInstanceId.getInstance().getToken();
        Log.d( "Message Token", Common.currentToken );
        mService = Common.getFCMClient();
    }

    public  void init(){
        mFirestore = FirebaseFirestore.getInstance();
        txvResult = (TextView) findViewById(R.id.txvResult);
        listener_sensor = findViewById( R.id.listener_sensor );
        mToolbar = findViewById(R.id.emergencency_call_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Urgences");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(false);
        //user_id = mAuth.getCurrentUser().getUid();
        SharedPreferences preferences = getApplicationContext().getSharedPreferences("ShareIdUser", MODE_PRIVATE);
        user_id = preferences.getString("user_id", null);
        //fUser = FirebaseAuth.getInstance().getCurrentUser();

        subscribeToTopic();

        listener_sensor.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText( EmergencyCallActivity.this, "Listener", Toast.LENGTH_SHORT ).show();
                //startActivity( new Intent( EmergencyCallActivity.this, AlerteListenerActivity.class ) );
            }
        } );
    }



    public void getSpeechInput(View view) {

        Intent intent = new Intent( RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, 10);
        } else {
            Toast.makeText(this, "Votre smartphone ne supporte pas l'enregistrement vocal de google", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 10:
                if (resultCode == RESULT_OK && data != null) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    txvResult.setText(result.get(0));
                    if (result.get(0).equals(Const.AU_SECOURS)){
                        sendAlerte();
                    }else {
                        Toast.makeText( this, "Dites au secours", Toast.LENGTH_SHORT ).show();
                    }

                }
                break;
        }
    }

    public  void sendAlerte(){

        if(isLocationFound) {
            LatLng latLng = new LatLng(currentLocationDevice.getLatitude() , currentLocationDevice.getLongitude());
            latitude= latLng.latitude;
            longitude = latLng.longitude;

            Toast.makeText(EmergencyCallActivity.this, "latitude "+latitude +" longitude "+longitude, Toast.LENGTH_SHORT).show();

                storeFirebaseInstance(Const.DESCRIPTION_ALERT, "","" ,user_id,latitude,longitude);

        }else{
            Toast.makeText( this, "Location not found", Toast.LENGTH_SHORT ).show();
            storeFirebaseInstance(Const.DESCRIPTION_ALERT,"","",user_id,0d,0d);

        }
    }

    @Override
    public void onBackPressed() {
        startActivity( new Intent( EmergencyCallActivity.this , MainActivity.class) );
    }

    public void storeFirebaseInstance(final String description, String downloadUri,final String downloadthumbUri,final String current_user_id, final Double latitude  , final Double longitude) {

        Calendar calendar  = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");

        saveDate = currentDate.format(calendar.getTime());

        Calendar calForTime = Calendar.getInstance();
        SimpleDateFormat time = new SimpleDateFormat("H:mm");
        saveCurrentTime = time.format(calForTime.getTime());

        currentTime = saveDate+" "+saveCurrentTime;

        Map<String, Object> postMap = new HashMap<>();
        postMap.put("description", description);
        postMap.put("type", 0);
        postMap.put("latitude", latitude);
        postMap.put("longitude", longitude);
        postMap.put("image_url", downloadUri);
        postMap.put("image_thumb", downloadthumbUri);
        postMap.put("user_id", current_user_id);
        postMap.put("timestamp",currentTime );

        mFirestore.collection("Posts").add(postMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {

                if (task.isSuccessful()) {

                    Toast.makeText(EmergencyCallActivity.this, "Alerte a été envoyé avec success.", Toast.LENGTH_LONG).show();

                    // SEND NOTIFICATION

                    sendNotification(7,user_id,Const.DESCRIPTION_ALERT,"",latitude,longitude, currentTime);

                    sendNotificationPush(Const.DESCRIPTION_ALERT, Const.DEFAULT_TYPES[7]);


                } else {

                    Toast.makeText(EmergencyCallActivity.this, "Erroor while trying to send the alert", Toast.LENGTH_SHORT).show();
                }
                //newPostProgress.setVisibility(View.INVISIBLE);
            }
        });

    }

    // Notification Push
    private void sendNotificationPush(String description,String typeAlerte) {

        Notification notification = new Notification(description,typeAlerte);

        Sender sender = new Sender( "/topics/alerte",notification);
        mService.sendNotification(sender).enqueue( new Callback<MyResponse>() {
            @Override
            public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {

                if(response.body().success == 1)
                    Toast.makeText( EmergencyCallActivity.this, "Success", Toast.LENGTH_SHORT ).show();
                else
                    Toast.makeText( EmergencyCallActivity.this, "Failed", Toast.LENGTH_SHORT ).show();
            }

            @Override
            public void onFailure(Call<MyResponse> call, Throwable t) {

                Log.e( "Error ", t.getMessage());

            }
        } );

    }


    public void sendNotification(final int typeAlert, final String user_id, String description, String image, Double latitude, Double longitude, String date){

        Map<String ,Object > notificationMessage = new HashMap<>();
        notificationMessage.put("type", typeAlert);
        notificationMessage.put("from",user_id );
        notificationMessage.put("description",description );
        notificationMessage.put("latitude",latitude );
        notificationMessage.put("longitude",longitude );
        notificationMessage.put("image",image );
        notificationMessage.put("date",date );
        notificationMessage.put("status","0" );
        mFirestore.collection("Notifications").document().set(notificationMessage).addOnCompleteListener( new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){

                    Toast.makeText(EmergencyCallActivity.this, "alerte envoyé avec success. ", Toast.LENGTH_SHORT).show();
                    Intent mainIntent = new Intent(EmergencyCallActivity.this, MainActivity.class);
                    startActivity(mainIntent);
                    finish();
                }else{
                    String errorMsg = task.getException().getMessage();
                    Toast.makeText(EmergencyCallActivity.this, "Une erreur s'est produite. "+errorMsg, Toast.LENGTH_SHORT).show();

                }
            }
        } );
    }


    public void getCurrentLocation() {

        isPermissionsGranted = mapsServices.getLocationPermission(this);

        if (isPermissionsGranted) {


            Log.d(TAG, "getDeviceLocation: getting devices location");

            mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

            try {

                Log.d(TAG, "getDeviceLocation: permission is granted , we try now to get the current location");

                Task location = mFusedLocationProviderClient.getLastLocation();

                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {

                        Log.d(TAG, "onComplete: we are trying to found the current location ");

                        Toast.makeText(EmergencyCallActivity.this, "Trying to found the current location ", Toast.LENGTH_SHORT).show();

                        if (task.isSuccessful() && task.getResult() != null) {

                            Toast.makeText(EmergencyCallActivity.this, "C'est cool ", Toast.LENGTH_SHORT).show();

                            Log.d(TAG, "onComplete: we have found the location " + task.getResult().toString());

                            currentLocationDevice = (Location) task.getResult();

                            isLocationFound = true;

                            Toast.makeText(EmergencyCallActivity.this, "Location found ", Toast.LENGTH_SHORT).show();

                        } else {

                            Toast.makeText(EmergencyCallActivity.this, "location not found ", Toast.LENGTH_SHORT).show();

                        }

                        Log.d(TAG, "onComplete: currentLocation " + currentLocationDevice);
                    }
                });

            } catch (SecurityException e) {

                Log.d(TAG, "getDeviceLocation: SecurityException : " + e.getMessage());
            }


        } else {
            Log.d(TAG, "getCurrentLocation: asking permissions ");
            mapsServices.askPermissions();
        }

    }

    private void verifyServices() {

        mapsServices = new GoogleMapsServices(this, TAG);

        boolean serviceOk = mapsServices.isServicesOk(EmergencyCallActivity.this);

        if (serviceOk) {

            getCurrentLocation();
        }
    }
}
