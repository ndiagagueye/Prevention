package com.example.gueye.memoireprevention2018.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hbb20.CountryCodePicker;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetupActivity extends AppCompatActivity {

    private Toolbar setupToolbar;
    private CircleImageView profileImage;
    private TextView textProfileImage;
    private CheckBox isAlert;
    private CheckBox isBenevol;
    private EditText fullName;
    private EditText phone;
    private EditText callEmergency;
    //private String call_number;
    private CountryCodePicker codeCountry;
    private CountryCodePicker codeCountryEmergency;
    private Button saveSetupBtn;
    private Uri mainImageURI = null;
    private ProgressBar setup_progress;
    private boolean isChanged = false;
    private ProgressDialog loadinBar;

    //Firebase
    private StorageReference storageReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private String user_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( com.example.gueye.memoireprevention2018.R.layout.activity_setup );

        setupToolbar =  findViewById(com.example.gueye.memoireprevention2018.R.id.setup_toolbar);
        setSupportActionBar(setupToolbar);
        getSupportActionBar().setTitle("Configuration du compte");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        firebaseAuth = FirebaseAuth.getInstance();
        user_id = firebaseAuth.getCurrentUser().getUid();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        loadinBar = new ProgressDialog( this );

        profileImage = findViewById(com.example.gueye.memoireprevention2018.R.id.setup_image);
        textProfileImage = findViewById(com.example.gueye.memoireprevention2018.R.id.setup_text_image);
        fullName = findViewById(com.example.gueye.memoireprevention2018.R.id.setup_full_name);
        phone = findViewById(com.example.gueye.memoireprevention2018.R.id.setup_phone);
        codeCountry = findViewById(com.example.gueye.memoireprevention2018.R.id.code_country_picker);
        callEmergency = findViewById(com.example.gueye.memoireprevention2018.R.id.setup_urgence_phone);
        codeCountryEmergency = findViewById(com.example.gueye.memoireprevention2018.R.id.code_urgence_country_picker);

        //call_number = findViewById(R.id.setup_phone);

        saveSetupBtn = findViewById(com.example.gueye.memoireprevention2018.R.id.save_btn);

        String user_name = getIntent().getStringExtra("name");
        fullName.setText(user_name);



        //Clique sur le button save

        saveSetupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String username = fullName.getText().toString();
                //final String telephone = call_number.getText().toString();
                final String codePicker = codeCountry.getSelectedCountryCode();
                final String number = phone.getText().toString().trim();
                final String codePickerEmergency = codeCountryEmergency.getSelectedCountryCode();
                final String numberEmergency = callEmergency.getText().toString().trim();
                final String telephone = codePicker+" "+number;
                final String telephoneEmergency = codePickerEmergency+" "+numberEmergency;

                //if (!TextUtils.isEmpty(telephoneEmergency)  && !TextUtils.isEmpty(username)  && !TextUtils.isEmpty(telephone)  && ) {
                if (mainImageURI == null) {
                    Toast.makeText( SetupActivity.this, "Veuillez choisir une image de profile.", Toast.LENGTH_LONG ).show();
                } else if(TextUtils.isEmpty(username)){
                    Toast.makeText( SetupActivity.this, "Veuillez saisir votre nom complet.", Toast.LENGTH_LONG ).show();

                }else if(TextUtils.isEmpty(telephone)){
                    Toast.makeText( SetupActivity.this, "Veuillez saisir votre numéro de telephone.", Toast.LENGTH_LONG ).show();

                }else if(TextUtils.isEmpty(telephoneEmergency)){
                    Toast.makeText( SetupActivity.this, "Veuillez le numéro de téléphone à contacter en cas d'urgence.", Toast.LENGTH_LONG ).show();

                } else{
                    loadinBar.setTitle("");
                    loadinBar.setMessage("Chargement en cours...");
                    loadinBar.setCanceledOnTouchOutside(false);
                    loadinBar.show();

                    if (isChanged){

                        user_id = firebaseAuth.getCurrentUser().getUid();

                        StorageReference image_path = storageReference.child("profile_image").child(user_id + ".jpg");
                        image_path.putFile(mainImageURI).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                                if (task.isSuccessful()) {
                                    //setup_progress.setVisibility(View.INVISIBLE);
                                    storeFirestore(task, username, telephone, telephoneEmergency);

                                } else {
                                    loadinBar.dismiss();
                                    String errorMessage = task.getException().getMessage();
                                    Toast.makeText(SetupActivity.this, "Image error " + errorMessage, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    }else{
                        storeFirestore(null,username, telephone, telephoneEmergency);


                    }
                }
            }
        });

        // Click on CircleImageView
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

             // Allow or Deny
             permissionGaranted();

            }
        });


    }

    private void permissionGaranted() {

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){

            if (ContextCompat.checkSelfPermission(SetupActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

                Toast.makeText(SetupActivity.this, "Permission denied!", Toast.LENGTH_LONG).show();
                ActivityCompat.requestPermissions(SetupActivity.this, new String[] { android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }
            else{

                BringImagePicker();
            }
        }else{
            BringImagePicker();
        }
    }


    //Stock infos
    private void storeFirestore(@NonNull Task<UploadTask.TaskSnapshot> task, String username, String telephone, String telephoneEmergency) {
        DatabaseReference reference  = FirebaseDatabase.getInstance().getReference("Users").child(user_id);

        Uri download_uri;
        if (task != null){
            download_uri = task.getResult().getDownloadUrl();

        }else{
            download_uri = mainImageURI;
        }

        String token_id = FirebaseInstanceId.getInstance().getToken();
        final HashMap<String,String> userMap = new HashMap<>();

        userMap.put("image", download_uri.toString());
        userMap.put("name", username);
        userMap.put("telephone", telephone);
        userMap.put("telephoneEmergency", telephoneEmergency);
        userMap.put("status", "offline" );
        userMap.put( "token_id", token_id);
        userMap.put( "user_id", user_id);

        reference.setValue(userMap);

        firebaseFirestore.collection("Users").document(user_id).set(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){

                    Toast.makeText(SetupActivity.this, "Votre compte a été bien mise à jour. ", Toast.LENGTH_SHORT).show();
                    Intent mainIntent = new Intent(SetupActivity.this, MainActivity.class);
                    startActivity(mainIntent);
                    finish();
                }else{
                    loadinBar.dismiss();
                    String errorMsg = task.getException().getMessage();
                    Toast.makeText(SetupActivity.this, "Une erreur s'est produite. "+errorMsg, Toast.LENGTH_SHORT).show();

                }
            }
        });

    }


    private void BringImagePicker(){
        CropImage.activity()
                .setGuidelines( CropImageView.Guidelines.ON)
                .setAspectRatio(1,1)
                .start(SetupActivity.this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                mainImageURI = result.getUri();
                profileImage.setImageURI(mainImageURI);
                isChanged = true;

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}
