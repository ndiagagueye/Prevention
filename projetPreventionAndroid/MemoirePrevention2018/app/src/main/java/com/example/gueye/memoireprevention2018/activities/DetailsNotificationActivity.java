package com.example.gueye.memoireprevention2018.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.gueye.memoireprevention2018.R;
import com.example.gueye.memoireprevention2018.utils.Const;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import de.hdodenhof.circleimageview.CircleImageView;

public class DetailsNotificationActivity extends AppCompatActivity {

    private String notif_id ;
    private FirebaseFirestore mFirestore;
    private FirebaseAuth mAuth;
    private String typeAlert = null ;

    private TextView descView;
    private ImageView blogImageView;
    private TextView blogDate;
    private TextView blogUserName;
    private CircleImageView blogUserImage;
    private TextView blogType;

    private android.support.v7.widget.Toolbar mToolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_details_notification );

        init();
        //Toast.makeText( this, notif_id , Toast.LENGTH_SHORT ).show();
        getDetailsNotification();


    }

    private void init() {

        notif_id = getIntent().getStringExtra("notif_id");

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();

        mToolbar = findViewById( R.id.detail_notif_toolbar );

        setSupportActionBar( mToolbar );

        getSupportActionBar().setDisplayHomeAsUpEnabled( true );
        mToolbar.setNavigationOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity( new Intent( getApplicationContext(), MainActivity.class ) );
                finish();
            }
        } );

        final CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById( R.id.detail_toolbar_layout);
        AppBarLayout appBarLayout = (AppBarLayout) findViewById( R.id.detail_app_bar);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = true;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbarLayout.setTitle(" Details Notification");
                    isShow = true;
                } else if(isShow) {
                    collapsingToolbarLayout.setTitle(" ");//carefull there should a space between double quote otherwise it wont work
                    isShow = false;
                }
            }
        });


        descView = findViewById( R.id.detail_desc);
        blogImageView = findViewById( R.id.detail_image);
        blogDate = findViewById( R.id.detail_date);
        blogUserName = findViewById( R.id.detail_user_name);
        blogUserImage = findViewById( R.id.detail_user_image);
        blogType = findViewById( R.id.detail_tv_title );

    }

    @Override
    public void onBackPressed() {
        startActivity( new Intent( DetailsNotificationActivity.this , MainActivity.class) );
    }

    public void getDetailsNotification(){

        mFirestore.collection( "Notifications" ).document(notif_id).get().addOnSuccessListener( new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()) {

                    String date = documentSnapshot.getString( "date" );
                    String description = documentSnapshot.getString( "description" );
                    String imagePub = documentSnapshot.getString( "image" );
                    long position = documentSnapshot.getLong("type") ;
                    String user_id =documentSnapshot.getString( "from" );
                    Double latitude = documentSnapshot.getDouble( "latitude" );
                    Double longitude = documentSnapshot.getDouble( "longitude" );
                    typeAlert = Const.DEFAULT_TYPES[(int) position];

                    descView.setText( description );
                    blogDate.setText( date );
                    blogType.setText( typeAlert );

                    if (imagePub.equals("")){

                        blogImageView.setImageResource( Const.DEFAULT_RESOURCE_IMAGES[(int) position]);
                    }else{

                        RequestOptions placeholderOption = new RequestOptions();
                        placeholderOption.placeholder( R.drawable.profile_placeholder );

                        Glide.with( DetailsNotificationActivity.this ).applyDefaultRequestOptions( placeholderOption ).load( imagePub ).into( blogImageView );
                    }


                    mFirestore.collection( "Users" ).document(user_id).get().addOnSuccessListener( new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            String userImage = documentSnapshot.getString( "image" );
                            String userName = documentSnapshot.getString("name" );

                            blogUserName.setText( userName );

                            RequestOptions placeholderOption = new RequestOptions();
                            placeholderOption.placeholder( R.drawable.profile_placeholder );

                            Glide.with( DetailsNotificationActivity.this ).applyDefaultRequestOptions( placeholderOption ).load( userImage ).into( blogUserImage );

                        }
                    } );

                }



            }
        } );
    }
}
