package com.example.gueye.memoireprevention2018.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.request.RequestOptions;
import com.example.gueye.memoireprevention2018.R;
import com.example.gueye.memoireprevention2018.activities.SendAlerteActivity;
import com.example.gueye.memoireprevention2018.adaptaters.CustomAdaptater;
import com.example.gueye.memoireprevention2018.adaptaters.TypeUserAdaptater;
import com.example.gueye.memoireprevention2018.utils.Const;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import java.util.HashMap;

public class ProfilActivity extends AppCompatActivity {

    private static final String TAG = "PROFILE";
    private Toolbar mToolbar;
    private EditText mUsername, mTelephone, mTelephoneEmergency;
    private ImageView mProfileImage;
    private ImageView mEditBtn;
    private  ImageView mSaveInfos;
    private FloatingActionButton mchangeProfileImage;
    private Uri mainImageURI = null;
    private boolean isChanged = false;
    private ProgressDialog loadinBar;
    private DatabaseReference reference;
    private int positionSelect = 0;
    private Spinner typeSpinner;
    private static final int REQUEST_CALL =1;
    private TypeUserAdaptater customAdaptater;

    private Button callUrgence;
    private Button inviteFriend;


    private StorageReference storageReference;
    //Firebase

    private FirebaseFirestore mFirestore;
    private FirebaseAuth mAuth;
    private String mUserId, user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_user_profile );

        init();
        loadInfo();

        typeSpinner = (Spinner) findViewById(R.id.spinner_type_user);
        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                positionSelect = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        customAdaptater = new TypeUserAdaptater(ProfilActivity.this, Const.DEFAULT_RESOURCE_ICONES_USER, Const.DEFAULT_TYPES_USER);

        typeSpinner.setAdapter(customAdaptater);


        // Click on CircleImageView
        mchangeProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Allow or Deny
                permissionGaranted();

            }
        });

    }

    public void init(){
        mFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        loadinBar = new ProgressDialog(this);
        callUrgence = findViewById(R.id.urgence_call_btn);
        inviteFriend = findViewById(R.id.invite_friend_btn);


        mUserId = mAuth.getCurrentUser().getUid();

        mToolbar = findViewById(R.id.profile_toolbar);

        setSupportActionBar(mToolbar);

        mUsername = findViewById(R.id.profile_username);
        mTelephone = findViewById(R.id.profile_telephone);
        mTelephoneEmergency = findViewById(R.id.profile_user_contact_here);
        mProfileImage = findViewById( R.id.profile_image_circle);
        mEditBtn = findViewById(R.id.btn_edit_profile);
        mSaveInfos = findViewById(R.id.btn_save_info);
        mchangeProfileImage = findViewById(R.id.take_a_picture);

        mEditBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSaveInfos.setVisibility(View.VISIBLE);
                mEditBtn.setVisibility( View.GONE );
                enableEditText();
            }
        } );

        saveInfo();

        callUrgence.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makePhoneCall();
            }
        } );

        inviteFriend.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText( ProfilActivity.this, "S'invite mes proche", Toast.LENGTH_SHORT ).show();
            }
        } );

    }

    private void saveInfo() {

        mSaveInfos.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadinBar.setTitle( "" );
                loadinBar.setMessage( "Chargement..." );
                loadinBar.setCanceledOnTouchOutside( false );
                loadinBar.show();

                disableEditText();

                final String name = mUsername.getText().toString();
                final String telephone = mTelephone.getText().toString();
                final String urgenceTel = mTelephoneEmergency.getText().toString();

                if (isChanged){

                    StorageReference image_path = storageReference.child("profile_image").child(mUserId + ".jpg");
                    image_path.putFile(mainImageURI).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                            if (task.isSuccessful()) {
                                //setup_progress.setVisibility(View.INVISIBLE);
                                updateProfilUser(task, name, telephone, urgenceTel, positionSelect);

                            } else {

                                String errorMessage = task.getException().getMessage();
                                Toast.makeText(ProfilActivity.this, "Image error " + errorMessage, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }else{
                    updateProfilUser(null,name, telephone, urgenceTel, positionSelect);

                }
            }

        } );
    }

    private void permissionGaranted() {

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){

            if (ContextCompat.checkSelfPermission(ProfilActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

                Toast.makeText(ProfilActivity.this, "Permission denied!", Toast.LENGTH_LONG).show();
                ActivityCompat.requestPermissions(ProfilActivity.this, new String[] { android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }
            else{

                BringImagePicker();
            }
        }else{
            BringImagePicker();
        }
    }


    private void enableEditText() {

        mchangeProfileImage.setVisibility(View.VISIBLE);
        typeSpinner.setVisibility(View.VISIBLE);
        mUsername.setFocusable(true);
        mUsername.setFocusableInTouchMode(true);
        mUsername.setClickable(true);

        mTelephone.setFocusable(true);
        mTelephone.setFocusableInTouchMode(true);
        mTelephone.setClickable(true);

        mTelephoneEmergency.setFocusable(true);
        mTelephoneEmergency.setFocusableInTouchMode(true);
        mTelephoneEmergency.setClickable(true);

    }

    private void disableEditText() {

        mSaveInfos.setVisibility(View.GONE);
        mEditBtn.setVisibility( View.VISIBLE );
        typeSpinner.setVisibility(View.GONE);
        mchangeProfileImage.setVisibility(View.GONE);
        mSaveInfos.setVisibility(View.GONE);
        mUsername.setFocusable(false);
        mUsername.setFocusableInTouchMode(false);
        mUsername.setClickable(false);

        mTelephone.setFocusable(false);
        mTelephone.setFocusableInTouchMode(false);
        mTelephone.setClickable(false);

        mTelephoneEmergency.setFocusable(false);
        mTelephoneEmergency.setFocusableInTouchMode(false);
        mTelephoneEmergency.setClickable(false);

    }

    public  void  loadInfo(){

        mFirestore.collection("Users").document(mUserId).get().addOnSuccessListener( new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                if (documentSnapshot.exists()){
                    String name = documentSnapshot.getString( "name" );
                    String telephone = documentSnapshot.getString( "telephone" );
                    String telephoneEmergency = documentSnapshot.getString( "telephoneEmergency");
                    String image = documentSnapshot.getString( "image" );
                    long position = documentSnapshot.getLong("typeUser");

                    mUsername.setText( name );
                    mTelephone.setText(telephone);
                    mTelephoneEmergency.setText(telephoneEmergency);
                    typeSpinner.setSelection( (int) position );


                    RequestOptions plasholderOption = new RequestOptions();
                    plasholderOption.placeholder( R.drawable.back );

                    Glide.with(ProfilActivity.this ).setDefaultRequestOptions( plasholderOption ).load( image ).into( mProfileImage );

                }

            }
        } );
    }

    // Dialling phone
    private void makePhoneCall() {
        String current_user_id = mAuth.getCurrentUser().getUid();
        mFirestore.collection( "Users" ).document(current_user_id).get().addOnSuccessListener( new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                if (documentSnapshot.exists()) {

                    String number = documentSnapshot.getString( "telephoneEmergency" );

                    if (number.trim().length() > 0) {

                        if (ContextCompat.checkSelfPermission(ProfilActivity.this,
                                android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(ProfilActivity.this,
                                    new String[]{android.Manifest.permission.CALL_PHONE}, REQUEST_CALL);
                        } else {
                            String dial = "tel:" + number;
                            startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
                        }

                    }else {
                        Toast.makeText(ProfilActivity.this, "Veuillez completer votre profile", Toast.LENGTH_SHORT).show();
                    }

                }

            }
        } );

    }




    //Stock infos
    private void updateProfilUser(@NonNull Task<UploadTask.TaskSnapshot> task, String username, String telephone, String telephoneEmergency, int typeUser) {

        Uri download_uri;
        if (task != null){
            download_uri = task.getResult().getDownloadUrl();

        }else{
            download_uri = mainImageURI;
        }

        reference = FirebaseDatabase.getInstance().getReference( "Users" ).child(mUserId);

        final HashMap<String,Object> userMap = new HashMap<>();

        if(task != null){
            userMap.put("image", download_uri.toString());
        }else {
            userMap.put("name", username);
            userMap.put("telephone", telephone);
            userMap.put("telephoneEmergency", telephoneEmergency);
            userMap.put("typeUser", typeUser);
        }

        reference.updateChildren(userMap );

        mFirestore.collection("Users").document(mUserId).update(userMap).addOnSuccessListener( new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                loadinBar.dismiss();
            }
        } );

        loadinBar.dismiss();

    }


    private void BringImagePicker(){
        CropImage.activity()
                .setGuidelines( CropImageView.Guidelines.ON)
                .setAspectRatio(1,1)
                .start(ProfilActivity.this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                mainImageURI = result.getUri();
                mProfileImage.setImageURI(mainImageURI);
                isChanged = true;

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

}
