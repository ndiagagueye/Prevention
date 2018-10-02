package com.example.gueye.memoireprevention2018.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.gueye.memoireprevention2018.R;
import com.facebook.CallbackManager;
import com.facebook.ProfileTracker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hbb20.CountryCodePicker;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetupFacebookActivity extends AppCompatActivity {


    private Toolbar setupToolbar;
    private CircleImageView mProfileImage;
    private TextView mProfileName;
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

    private CallbackManager mCallbackManager;
    ProfileTracker profileTracker;

    //Firebase
    private StorageReference storageReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_setup_facebook );

        setupToolbar =  findViewById(R.id.setup_facebook_toolbar);
        setSupportActionBar(setupToolbar);
        getSupportActionBar().setTitle("Configuration du compte");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        firebaseAuth = FirebaseAuth.getInstance();
        //user_id = firebaseAuth.getCurrentUser().getUid();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        loadinBar = new ProgressDialog( this );

        //Infos facebook
        mProfileImage = findViewById(R.id.setup_profile_facebook_image);
        mProfileName = findViewById(R.id.setup_profile_name_facebook);

        phone = findViewById(R.id.setup_phone_facebook);
        codeCountry = findViewById(R.id.code_country_picker_facebook);
        callEmergency = findViewById(R.id.setup_urgence_phone_facebook);
        codeCountryEmergency = findViewById(R.id.code_urgence_country_picker_facebook);
        saveSetupBtn = findViewById(R.id.save_btn_facebook);

        //Infos Facebook

        final String username = getIntent().getStringExtra("username");
        final String image = getIntent().getStringExtra("imageUrl");

        mProfileName.setText(username);

        RequestOptions plasholderOption = new RequestOptions();
        plasholderOption.placeholder( R.drawable.profile );
       Glide.with(SetupFacebookActivity.this ).setDefaultRequestOptions( plasholderOption ).load( image ).into( mProfileImage );


        //Clique sur le button save

        saveSetupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //final String telephone = call_number.getText().toString();
                final String codePicker = codeCountry.getSelectedCountryCode();
                final String number = phone.getText().toString().trim();
                final String codePickerEmergency = codeCountryEmergency.getSelectedCountryCode();
                final String numberEmergency = callEmergency.getText().toString().trim();
                final String telephone = codePicker+" "+number;
                final String telephoneEmergency = codePickerEmergency+" "+numberEmergency;

                 if(TextUtils.isEmpty(telephone)){
                    Toast.makeText( SetupFacebookActivity.this, "Veuillez saisir votre numéro de telephone.", Toast.LENGTH_LONG ).show();

                }else if(TextUtils.isEmpty(telephoneEmergency)){
                    Toast.makeText( SetupFacebookActivity.this, "Veuillez le numéro de téléphone à contacter en cas d'urgence.", Toast.LENGTH_LONG ).show();

                } else{
                    loadinBar.setTitle("");
                    loadinBar.setMessage("Chargement en cours...");
                    loadinBar.setCanceledOnTouchOutside(false);
                    loadinBar.show();

                    if (isChanged){

                        StorageReference image_path = storageReference.child("profile_image").child(user_id + ".jpg");
                        image_path.putFile(mainImageURI).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                                if (task.isSuccessful()) {
                                    //setup_progress.setVisibility(View.INVISIBLE);
                                    storeFirestore(task, username, telephone, telephoneEmergency,image);

                                } else {
                                    loadinBar.dismiss();
                                    String errorMessage = task.getException().getMessage();
                                    Toast.makeText(SetupFacebookActivity.this, "Image error " + errorMessage, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    }else{
                        storeFirestore(null,username, telephone, telephoneEmergency,image);


                    }
                }
            }
        });




    }

    //Stock infos
    private void storeFirestore(@NonNull Task<UploadTask.TaskSnapshot> task, String username, String telephone, String telephoneEmergency,String image) {

        Uri download_uri;
        if (task != null){
            download_uri = task.getResult().getDownloadUrl();

        }else{
            download_uri = mainImageURI;
        }

        String token_id = FirebaseInstanceId.getInstance().getToken();
        final HashMap<String,String> userMap = new HashMap<>();

        userMap.put("image", image);
        userMap.put("name", username);
        userMap.put("telephone", telephone);
        userMap.put("telephoneEmergency", telephoneEmergency);
        userMap.put("token_id", token_id );
        userMap.put("StatusLine", "online" );

        final String user_id = getIntent().getStringExtra("user_id");


        firebaseFirestore.collection("Users").document(user_id).set(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){

                    Toast.makeText(SetupFacebookActivity.this, "Votre compte a été bien mise à jour. ", Toast.LENGTH_SHORT).show();
                    Intent mainIntent = new Intent(SetupFacebookActivity.this, MainActivity.class);
                    startActivity(mainIntent);
                    finish();
                }else{
                    loadinBar.dismiss();
                    String errorMsg = task.getException().getMessage();
                    Toast.makeText(SetupFacebookActivity.this, "Une erreur s'est produite. "+errorMsg, Toast.LENGTH_SHORT).show();

                }
            }
        });

    }

}
