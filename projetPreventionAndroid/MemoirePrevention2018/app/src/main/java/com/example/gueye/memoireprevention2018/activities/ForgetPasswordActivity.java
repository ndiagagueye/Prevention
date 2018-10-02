package com.example.gueye.memoireprevention2018.activities;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.gueye.memoireprevention2018.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgetPasswordActivity extends AppCompatActivity {
    private Toolbar forgetPasswordToolbar;
    private Button btnReset;
    private EditText resetEmail;
    private FirebaseAuth auth;
    private ProgressDialog loadinBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_forget_password );


        auth = FirebaseAuth.getInstance();
        forgetPasswordToolbar = (Toolbar) findViewById(R.id.Forget_password_toolbar);
        btnReset = findViewById(R.id.btn_forget_password);
        resetEmail = findViewById(R.id.email_reset_password);
        loadinBar = new ProgressDialog(this);
        setSupportActionBar(forgetPasswordToolbar);
        getSupportActionBar().setTitle("Réinitialisation de mot de passe");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = resetEmail.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplication(), "Entrer l'adresse e-mail associé à votre compte.", Toast.LENGTH_SHORT).show();
                    return;
                }
                loadinBar.setTitle("Chargement...");
                loadinBar.setMessage("Veuillez patienter un instant.");
                loadinBar.setCanceledOnTouchOutside(false);
                loadinBar.show();

                auth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(ForgetPasswordActivity.this, "We have sent you instructions to reset your password!", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(ForgetPasswordActivity.this, "Failed to send reset email!", Toast.LENGTH_SHORT).show();
                                }

                                loadinBar.dismiss();
                            }
                        });
            }

        });
    }
}
