package com.example.gueye.memoireprevention2018.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
import com.example.gueye.memoireprevention2018.utils.NotificationShow;
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
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AlerteListenerActivity extends AppCompatActivity {
    private SensorManager sm;
    private float acelVal; // current acceleration including gravity
    private float acelLast; // last acceleration including gravity
    private float shake; // acceleration apart from gravity


    private TextView capt;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private String user_id;
    private android.support.v7.widget.Toolbar mToolbar;
    private String saveDate;
    private String saveCurrentTime;
    private String currentTime;
    private static final String TAG = "Alerte";
    APIService mService;
    GoogleMapsServices mapsServices;
    public boolean isLocationFound = false;
    private boolean isPermissionsGranted = false;
    public FusedLocationProviderClient mFusedLocationProviderClient;
    private Location currentLocationDevice;
    private double latitude  ;
    private double longitude ;
    public boolean isAlerteSend ;

    public boolean isNotificationStore = false;
    public boolean isNotificationSend = false;

    private  Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_alerte_listener );
        // SENSOR SECOUEMENT
        capt = findViewById(R.id.capt);
        sm = (SensorManager) getSystemService( Context.SENSOR_SERVICE );
        sm.registerListener( sensorListener, sm.getDefaultSensor( Sensor.TYPE_ACCELEROMETER ), SensorManager.SENSOR_DELAY_NORMAL );
        shake = 0.00f;
        acelVal = SensorManager.GRAVITY_EARTH;
        acelLast = SensorManager.GRAVITY_EARTH;

        context = getApplicationContext();

        //INIT SERVICE

        verifyServices(context);

        // INIT
        init();


    }

    public  AlerteListenerActivity(Context context){

        this.context = context;
        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        user_id = mAuth.getCurrentUser().getUid();

         isAlerteSend = false;

        subscribeToTopic();

    }

    private final SensorEventListener sensorListener = new SensorEventListener() {
        public void onSensorChanged(SensorEvent se) {
            float x = se.values[0];
            float y = se.values[1];
            float z = se.values[2];
            acelLast = acelVal;
            acelVal = (float) Math.sqrt( (double) (x * x + y * y + z * z) );
            float delta = acelVal - acelLast;
            shake = shake * 0.9f + delta; // perform low-cut filter

            capt.setText(shake+"");
            if (shake > 30) {

                sendAlerte();
                Toast toast = Toast.makeText( getApplicationContext(), "DO NOT SHAKE ME", Toast.LENGTH_LONG );
                toast.show();
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }



    };


    private void subscribeToTopic() {
        FirebaseMessaging.getInstance().subscribeToTopic("alerte");
        Common.currentToken = FirebaseInstanceId.getInstance().getToken();
        Log.d( "Message Token", Common.currentToken );
        mService = Common.getFCMClient();
    }

    public  void init(){
        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        mToolbar = findViewById(R.id.alerte_listener_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Secouement");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(false);
        user_id = mAuth.getCurrentUser().getUid();
        subscribeToTopic();
    }


    public  void sendAlerte(){

        if(isLocationFound) {
            LatLng latLng = new LatLng(currentLocationDevice.getLatitude() , currentLocationDevice.getLongitude());
            latitude= latLng.latitude;
            longitude = latLng.longitude;

            Toast.makeText(context, "latitude "+latitude +" longitude "+longitude, Toast.LENGTH_SHORT).show();

            storeFirebaseInstance( Const.DESCRIPTION_ALERT, "","" ,user_id,latitude,longitude);

        }else{
            Toast.makeText( context, "Location not found", Toast.LENGTH_SHORT ).show();
            storeFirebaseInstance(Const.DESCRIPTION_ALERT,"","",user_id,0d,0d);

        }
    }

    @Override
    public void onBackPressed() {
        startActivity( new Intent( AlerteListenerActivity.this , MainActivity.class) );
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
        postMap.put("type", 7);
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

                    Toast.makeText(context, "Alerte a été envoyé avec success.", Toast.LENGTH_LONG).show();

                    // SEND NOTIFICATION
                    isAlerteSend = true;

                    new NotificationShow(context).execute();

                    sendNotification(7,user_id,Const.DESCRIPTION_ALERT,"",latitude,longitude, currentTime);

                    sendNotificationPush(Const.DESCRIPTION_ALERT, Const.DEFAULT_TYPES[7]);


                } else {

                    Toast.makeText(context, "Erroor while trying to send the alert", Toast.LENGTH_SHORT).show();
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

                if(response.body().success == 1){

                    Toast.makeText( context, "Success", Toast.LENGTH_SHORT ).show();

                    isNotificationSend = true;
                }

                else
                    Toast.makeText( context, "Failed", Toast.LENGTH_SHORT ).show();
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

                    isNotificationStore = true;


                }else{
                    String errorMsg = task.getException().getMessage();
                    Toast.makeText(context, "Une erreur s'est produite. "+errorMsg, Toast.LENGTH_SHORT).show();

                }
            }
        } );
    }


    public void getCurrentLocation(final Context context) {

        isPermissionsGranted = mapsServices.getLocationPermission(context);

        if (isPermissionsGranted) {


            Log.d(TAG, "getDeviceLocation: getting devices location");

            mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);

            try {

                Log.d(TAG, "getDeviceLocation: permission is granted , we try now to get the current location");

                Task location = mFusedLocationProviderClient.getLastLocation();

                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {

                        Log.d(TAG, "onComplete: we are trying to found the current location ");

                        Toast.makeText(context, "Trying to found the current location ", Toast.LENGTH_SHORT).show();

                        if (task.isSuccessful() && task.getResult() != null) {

                            Toast.makeText(context, "C'est cool ", Toast.LENGTH_SHORT).show();

                            Log.d(TAG, "onComplete: we have found the location " + task.getResult().toString());

                            currentLocationDevice = (Location) task.getResult();

                            LatLng latLng = new LatLng(currentLocationDevice.getLatitude() , currentLocationDevice.getLongitude());
                            latitude= latLng.latitude;
                            longitude = latLng.longitude;

                            storeFirebaseInstance( Const.DESCRIPTION_ALERT, "","" ,user_id,latitude,longitude);

                            isLocationFound = true;

                            Toast.makeText(context, "Location found ", Toast.LENGTH_SHORT).show();

                        } else {

                            Toast.makeText(context, "location not found ", Toast.LENGTH_SHORT).show();

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

    public void verifyServices(Context context) {

        mapsServices = new GoogleMapsServices(context, TAG);

        boolean serviceOk = mapsServices.isServicesOk(context);

        if (serviceOk) {

            getCurrentLocation(context);
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        // Add the following line to register the Session Manager Listener onResume
        sm.registerListener(sensorListener, sm.getDefaultSensor( Sensor.TYPE_ACCELEROMETER ), SensorManager.SENSOR_DELAY_NORMAL ,SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onPause() {
        // Add the following line to unregister the Sensor Manager onPause
        sm.registerListener(sensorListener, sm.getDefaultSensor( Sensor.TYPE_ACCELEROMETER ), SensorManager.SENSOR_DELAY_NORMAL ,SensorManager.SENSOR_DELAY_UI);
        super.onPause();
    }
}
