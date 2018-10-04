package com.example.gueye.memoireprevention2018.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.gueye.memoireprevention2018.R;
import com.example.gueye.memoireprevention2018.utils.Const;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileBenevolActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private String mToken_id, mImage;
    private TextView mProfilename, mProfileTel, mTypeBenevol;
    private CircleImageView mProfileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_profile_benevol );
        init();
    }

    private void init() {

        mProfileImage = findViewById(R.id.benevol_image);
        mProfilename = findViewById(R.id.benevol_name);
        mProfileTel = findViewById(R.id.benevol_telephone);
        mTypeBenevol = findViewById(R.id.type_benevol);

        mToolbar = findViewById( R.id.profile_benevol_toolbar);
        setSupportActionBar( mToolbar );
        getSupportActionBar().setTitle( "Profile" );
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mProfilename.setText(getIntent().getStringExtra("username"));
        mProfileTel.setText( getIntent().getStringExtra("telephone") );
        mTypeBenevol.setText(getIntent().getStringExtra("type"));

        Toast.makeText( this, getIntent().getStringExtra("type"), Toast.LENGTH_SHORT ).show();

        mImage = getIntent().getStringExtra("image");
        mToken_id = getIntent().getStringExtra("token_id");
        RequestOptions plasholderOption = new RequestOptions();
        plasholderOption.placeholder( R.drawable.back );

        Glide.with(ProfileBenevolActivity.this).setDefaultRequestOptions( plasholderOption ).load(mImage).into( mProfileImage );


    }

}
