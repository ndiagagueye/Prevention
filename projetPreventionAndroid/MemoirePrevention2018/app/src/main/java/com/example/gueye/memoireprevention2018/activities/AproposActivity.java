package com.example.gueye.memoireprevention2018.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.gueye.memoireprevention2018.R;

public class AproposActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_apropos );

        Toolbar toolbar = (Toolbar) findViewById(R.id.apropostoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("A propos de l'application");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity( new Intent( AproposActivity.this , MainActivity.class) );
    }
}
