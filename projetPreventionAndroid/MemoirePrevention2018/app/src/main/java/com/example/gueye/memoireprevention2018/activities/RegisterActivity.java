package com.example.gueye.memoireprevention2018.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gueye.memoireprevention2018.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegisterActivity extends AppCompatActivity {

    private TextView have_account;
    private Button buttonRegister;
    private EditText fullName;
    private EditText register_email;
    private EditText register_password;
    private ProgressDialog loadinBar;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private RelativeLayout regFacebookSignInButton;
    private RelativeLayout regGoogleSignInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //  LayoutInflaterCompat.setFactory2(getLayoutInflater(), new IconicsLayoutInflater2(getDelegate()));
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_register );

        init();


        // have account

        haveAccount();

        // Process Register

        resisterUser();



    }

    private void init() {


        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();

        loadinBar = new ProgressDialog(this);

        fullName = findViewById(R.id.full_name_register);
        register_email = findViewById(R.id.email_register);
        register_password = findViewById(R.id.password_register);

        regFacebookSignInButton = findViewById( R.id.regFacebookSignInButton );
        regGoogleSignInButton = findViewById( R.id.regGoogleSignInButton );
        have_account = (TextView) findViewById(R.id.have_account);
        buttonRegister = (Button) findViewById(R.id.buttonRegister);
    }

    private void haveAccount() {

        have_account.setText(fromHtml("<font color='#999999'> Déjà membre ? </font><font color='#00b8d4'><style ='bold' >Connectez-vous.</style></font>"));

        have_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent loginIntent =  new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(loginIntent);
                finish();
            }
        });
    }

    private void resisterUser() {

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (checkNetworkConnectionStatus() == true){

                    String email = register_email.getText().toString();
                    String password = register_password.getText().toString();
                    final String name = fullName.getText().toString();
                    if (TextUtils.isEmpty(name)){
                        Toast.makeText(RegisterActivity.this,"Veuillez saisir votre nom complet...", Toast.LENGTH_LONG).show();

                    }else if (TextUtils.isEmpty(email) ){
                        Toast.makeText(RegisterActivity.this,"Veuillez saisir votre adresse e-mail...", Toast.LENGTH_LONG).show();

                    }else  if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                        register_email.setError("Veuillez saisir une adresse email valide!");

                    }else if (TextUtils.isEmpty(password)){
                        Toast.makeText(RegisterActivity.this,"Veuillez saisir un mot de passe...", Toast.LENGTH_LONG).show();

                    }else if (password.length() < 6){
                        register_password.setError("Minimum 6 caracteres!");


                    }else {
                        // On inscrit l'utilisateur

                        loadinBar.setTitle("");
                        loadinBar.setMessage("Veuilllez pendant que nous créons votre nouveau compte...");
                        loadinBar.setCanceledOnTouchOutside(false);
                        loadinBar.show();

                        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if (task.isSuccessful()){

                                    Intent setupIntent = new Intent(RegisterActivity.this, SetupActivity.class);
                                    setupIntent.putExtra("name", name);
                                    startActivity(setupIntent);
                                    finish();
                                    Toast.makeText(RegisterActivity.this, "Votre inscription a réussie avec succes...",Toast.LENGTH_SHORT).show();
                                    loadinBar.dismiss();

                                }
                                else {

                                    String message = task.getException().getMessage();
                                    Toast.makeText(RegisterActivity.this, "Une erreur est survenue..."+message,Toast.LENGTH_SHORT).show();
                                    loadinBar.dismiss();

                                }

                            }
                        });

                        Toast.makeText(RegisterActivity.this,"Stocke infos in database...", Toast.LENGTH_LONG).show();


                        //sendUserToTheMainActivity();


                    }

                }else{
                    tryAgain();
                }


            }
        });
    }


    @SuppressWarnings("deprecation")
    public static Spanned fromHtml(String html) {
        Spanned result;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            result = Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY);
        } else {
            result = Html.fromHtml(html);
        }
        return result;
    }

    public boolean checkNetworkConnectionStatus() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService( Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeInfo = connMgr.getActiveNetworkInfo();
        if (activeInfo != null && activeInfo.isConnected()){ //connected with either mobile or wifi

            return true;
        }
        else { //no internet connection

            return false;
        }
    }
    private void tryAgain() {

        setContentView( R.layout.status_network_connexion );
        ImageView mConStatusIv = findViewById(R.id.conStatusIv);
        TextView mConStatusTv = findViewById(R.id.conStatusTv);
        Button mConStatusBtn = findViewById(R.id.conStatusBtn);
        mConStatusIv.setImageResource(R.drawable.no_connection);
        mConStatusTv.setText("Veuillez vérifier votre connexion internet, puis reéssayer SVP!");

        mConStatusBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity( new Intent( RegisterActivity.this,RegisterActivity.class ) );
            }
        } );
    }


}
