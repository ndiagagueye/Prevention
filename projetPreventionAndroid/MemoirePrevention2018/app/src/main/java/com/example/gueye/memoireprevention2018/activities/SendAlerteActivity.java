
package com.example.gueye.memoireprevention2018.activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.gueye.memoireprevention2018.ModeleService.Common;
import com.example.gueye.memoireprevention2018.ModeleService.MyResponse;
import com.example.gueye.memoireprevention2018.ModeleService.Notification;
import com.example.gueye.memoireprevention2018.ModeleService.Sender;
import com.example.gueye.memoireprevention2018.R;
import com.example.gueye.memoireprevention2018.Remote.APIService;
import com.example.gueye.memoireprevention2018.adaptaters.CustomAdaptater;
import com.example.gueye.memoireprevention2018.services.GoogleMapsServices;
import com.example.gueye.memoireprevention2018.utils.Const;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import id.zelory.compressor.Compressor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SendAlerteActivity extends Activity {

    private static final String TAG = "SendAlerteActivity";
    private ImageView ivDescriptionPostAlert;
    private FloatingActionButton flTakeApicture;
    private EditText etdescriptionPostAlert;
    private Button btnSendAlert;
    private LinearLayout mChooseDate;

    private GeoPoint mGeoPoint;

    APIService mService;


    private Spinner typeSpinner;
    private CustomAdaptater customAdaptater;

    //vars
    // private Uri mainUrlImage = null;
    //cette variable va nous permettre de savoir l'option choisie par l'utilisateur
    //pour les types
    private int positionSelect = 0;
    public static final Uri DEFAULT_IMAGE_URI = null;
    private boolean isPermissionsGranted = false;
    public FusedLocationProviderClient mFusedLocationProviderClient;
    private Location currentLocationDevice;
    private double latitude  ;
    private double longitude ;
    private static final int DIALOG_ID = 0;
    int year_x, month_x ,day_x;


    GoogleMapsServices mapsServices;
    private boolean isLocationFound = false;
    private FirebaseFirestore firebaseFirestore;
    private Uri postImageUri = null;
    private Bitmap compressedImageFile;
    private StorageReference storageReference;

    private FirebaseAuth firebaseAuth;

    private  String  currentUserId ;
    private String saveDate;
    private String saveCurrentTime;
    private String currentTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_alerte);


        subscribeToTopic();
        FirebaseMessaging.getInstance().subscribeToTopic("alerte");

        Common.currentToken = FirebaseInstanceId.getInstance().getToken();
        Log.d( "Message Token", Common.currentToken );
        mService = Common.getFCMClient();

        ivDescriptionPostAlert = (ImageView) findViewById(R.id.iv_description_send_alerte);
        flTakeApicture = (FloatingActionButton) findViewById(R.id.fl_take_a_picture);
        etdescriptionPostAlert = (EditText) findViewById(R.id.et_description_send_alerte);
        btnSendAlert = (Button) findViewById(R.id.btn_send_alerte);
        mChooseDate = findViewById(R.id.ll_choose_date);

        init();
        verifyServices();

        typeSpinner = (Spinner) findViewById(R.id.spinner_type_of_send_alerte);
        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                positionSelect = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        customAdaptater = new CustomAdaptater(SendAlerteActivity.this, Const.DEFAULT_RESOURCE_ICONES, Const.DEFAULT_TYPES);

        typeSpinner.setAdapter(customAdaptater);

        // gerer le click sur le floating bouton : prendre une photo dans la galerie

        flTakeApicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //prendre une photo depuis l'appareil

                takeApicture();
            }
        });

        //gerer le cllick sur le bouton envoyer

        btnSendAlert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d(TAG, "onClick:  current locatioon is " + currentLocationDevice);

                //methode de base de l'enregistrement de l'alerte

                if(!TextUtils.isEmpty(etdescriptionPostAlert.getText().toString()) ){

                    String description = etdescriptionPostAlert.getText().toString();


                    if(isLocationFound) {
                        LatLng latLng = new LatLng(currentLocationDevice.getLatitude() , currentLocationDevice.getLongitude());
                       latitude= latLng.latitude;
                       longitude = latLng.longitude;

                      //  mGeoPoint = new GeoPoint(latitude,longitude);

                        Toast.makeText(SendAlerteActivity.this, "latitude "+latitude +" longitude "+longitude, Toast.LENGTH_SHORT).show();

                        if(postImageUri == null){

                            storeFirebaseInstance(description,positionSelect,"","",currentUserId,latitude,longitude);
                        }

                        else{

                            storePostAlertRequest(description, positionSelect,currentUserId,latitude,longitude);
                        }
                    }
                }else{

                    Toast.makeText( SendAlerteActivity.this, "Veuillez donner une description", Toast.LENGTH_SHORT ).show();
                }
            }
        });
    }

    private void subscribeToTopic() {
        FirebaseMessaging.getInstance().subscribeToTopic("alerte");
        Common.currentToken = FirebaseInstanceId.getInstance().getToken();
        Log.d( "Message Token", Common.currentToken );
        mService = Common.getFCMClient();
    }

    private void init() {

        final Calendar cal = Calendar.getInstance();
        year_x = cal.get( Calendar.YEAR );
        month_x = cal.get( Calendar.MONTH );
        day_x = cal.get( Calendar.DAY_OF_MONTH );

        showDialogPicker();

        firebaseAuth = FirebaseAuth.getInstance();
        SharedPreferences preferences = getApplicationContext().getSharedPreferences("ShareIdUser", MODE_PRIVATE);
        currentUserId = preferences.getString("user_id", null);

        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
    }

    private UploadTask storeImage(final String randomName, byte[] imageData) {

        // PHOTO UPLOAD
        UploadTask filePath = storageReference.child("post_images").child(randomName + ".jpg").putBytes(imageData);

        return filePath;
    }

    private byte[] compressedImage(File newImageFile, Bitmap compressedImageFile) {

        try {

            compressedImageFile = new Compressor(SendAlerteActivity.this).setMaxHeight(720).setMaxWidth(720).setQuality(50).compressToBitmap(newImageFile);

        } catch (IOException e) {
            e.printStackTrace();
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        compressedImageFile.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageData = baos.toByteArray();

        return  imageData;
    }

    public  UploadTask storeImageThumb(String randomName) {

        Log.d(TAG, "storeImageThumb:  trying to upload thum image now ");

        File newThumbFile = new File(postImageUri.getPath());
        try {
            compressedImageFile = new Compressor(SendAlerteActivity.this).setMaxHeight(100).setMaxWidth(100).setQuality(1).compressToBitmap(newThumbFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        compressedImageFile.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] thumbData = baos.toByteArray();

        UploadTask uploadTask = storageReference.child("post_images/thumbs").child(randomName + ".jpg").putBytes(thumbData);

        return  uploadTask;


    }


    //Datepicker

    public void showDialogPicker(){
        mChooseDate.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(DIALOG_ID);
            }
        } );

    }

    protected Dialog onCreateDialog(int id){

        if(id == DIALOG_ID){
            return new DatePickerDialog(this, datePickerListener, year_x, month_x, day_x);
        }

        return null ;

    }

    private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfYear) {
            year_x  =  year ;
            month_x = monthOfYear + 1 ;
            day_x = dayOfYear;
            Toast.makeText( SendAlerteActivity.this, year_x+"/"+month_x+"/"+day_x, Toast.LENGTH_SHORT ).show();

        }
    };


    //---------------------------------*********** FIN ************ ---------------------------------------------------

    private void storePostAlertRequest(final String description, final int positionSelect, final String currentUserId, final Double latitude, final Double longitude) {

        final String randomName = UUID.randomUUID().toString();

        File newImageFile = new File(postImageUri.getPath());

        byte [] imageData =  compressedImage(newImageFile, compressedImageFile);

        storeImage(randomName,imageData).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                final String downloadUri = task.getResult().getDownloadUrl().toString();

                if (task.isSuccessful()) {

                    Log.d(TAG, "onComplete: image upload succesfully ");

                    storeImageThumb(randomName).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            Log.d(TAG, "onSuccess:  Thumb image upload successfully ");

                            String downloadthumbUri = taskSnapshot.getDownloadUrl().toString();


                            if(isLocationFound){

                                storeFirebaseInstance(description,positionSelect,downloadUri,downloadthumbUri,currentUserId,latitude,longitude);
                            }



                        }
                    });

                }
            }
        });


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

                        Toast.makeText(SendAlerteActivity.this, "Trying to found the current location ", Toast.LENGTH_SHORT).show();

                        if (task.isSuccessful() && task.getResult() != null) {

                            Toast.makeText(SendAlerteActivity.this, "C'est cool ", Toast.LENGTH_SHORT).show();

                            Log.d(TAG, "onComplete: we have found the location " + task.getResult().toString());

                            currentLocationDevice = (Location) task.getResult();

                            isLocationFound = true;

                            Toast.makeText( SendAlerteActivity.this, "UserSend "+currentUserId, Toast.LENGTH_SHORT ).show();


                            Toast.makeText(SendAlerteActivity.this, "Location found ", Toast.LENGTH_SHORT).show();

                        } else {

                            Toast.makeText(SendAlerteActivity.this, "location not found ", Toast.LENGTH_SHORT).show();

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

        boolean serviceOk = mapsServices.isServicesOk(SendAlerteActivity.this);

        if (serviceOk) {

            getCurrentLocation();
        }
    }

    @Override
    public void onBackPressed() {
        startActivity( new Intent( SendAlerteActivity.this , MainActivity.class) );
    }

    public void storeFirebaseInstance(final String description, final int typeOfAlert, String downloadUri, final String downloadthumbUri, final String current_user_id, final Double latitude  , final Double longitude) {

        Calendar calendar  = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");

        saveDate = currentDate.format(calendar.getTime());

        Calendar calForTime = Calendar.getInstance();
        SimpleDateFormat time = new SimpleDateFormat("H:mm");
        saveCurrentTime = time.format(calForTime.getTime());

        currentTime = saveDate+" "+saveCurrentTime;

        Map<String, Object> postMap = new HashMap<>();
        postMap.put("description", description);
        postMap.put("type", typeOfAlert);
        postMap.put("latitude", latitude);
        postMap.put("longitude", longitude);
        postMap.put("image_url", downloadUri);
        postMap.put("image_thumb", downloadthumbUri);
        postMap.put("user_id", current_user_id);
        postMap.put("timestamp",currentTime );



        firebaseFirestore.collection("Posts").add(postMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {

                if (task.isSuccessful()) {

                    Toast.makeText(SendAlerteActivity.this, "Alerte a été envoyé avec success.", Toast.LENGTH_LONG).show();

                    // SEND NOTIFICATION

                    sendNotification(typeOfAlert,current_user_id,description,downloadthumbUri,latitude,longitude, currentTime);

                    sendNotificationPush(description, Const.DEFAULT_TYPES[positionSelect]);


                    finish();

                } else {

                    Toast.makeText(SendAlerteActivity.this, "Erroor while trying to send the alert", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText( SendAlerteActivity.this, "Success", Toast.LENGTH_SHORT ).show();
                else
                    Toast.makeText( SendAlerteActivity.this, "Failed", Toast.LENGTH_SHORT ).show();
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
        firebaseFirestore.collection("Notifications").document().set(notificationMessage).addOnCompleteListener( new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){

                    Toast.makeText(SendAlerteActivity.this, "alerte envoyé avec success. ", Toast.LENGTH_SHORT).show();
                    Intent mainIntent = new Intent(SendAlerteActivity.this, MainActivity.class);
                    startActivity(mainIntent);
                    finish();
                }else{
                    String errorMsg = task.getException().getMessage();
                    Toast.makeText(SendAlerteActivity.this, "Une erreur s'est produite. "+errorMsg, Toast.LENGTH_SHORT).show();

                }
            }
        } );
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                postImageUri = result.getUri();

                ivDescriptionPostAlert.setImageURI(postImageUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    private void takeApicture() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (ContextCompat.checkSelfPermission(SendAlerteActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                Toast.makeText(SendAlerteActivity.this, "Permission denied ", Toast.LENGTH_SHORT).show();

                ActivityCompat.requestPermissions(SendAlerteActivity.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            } else {

                //cette methode nous va permettre de prendre une photo depuis la galerie ou l'appareil photo

                pickImageFromGalery();
            }
        } else {

            pickImageFromGalery();
        }
    }

    private void pickImageFromGalery() {

        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(SendAlerteActivity.this);
    }

}


