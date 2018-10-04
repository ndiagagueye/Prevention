package com.example.gueye.memoireprevention2018.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gueye.memoireprevention2018.R;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;


import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private TextView forget_password;
    private TextView createAccount;
    private Button btnLogin;
    private EditText login_email_address, login_password;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private FirebaseFirestore mFirestore;
    private ProgressDialog loadinBar;
    private RelativeLayout btnLoginGoogle;
    private final String TAG = "Facebook Login";
    FirebaseFirestore firebaseFirestore;
    private RelativeLayout mFacebook;
    private CallbackManager mCallbackManager;
    ProfileTracker profileTracker;
    Intent logFacebook;

    //google account signin
    private  final int RC_SIGN_IN =1;
    private GoogleApiClient mGoogleSignInClient;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_login );

        logFacebook = new Intent(LoginActivity.this, SetupFacebookActivity.class);

        init();
        login();
        initFacebook();
        loginGoole();

    }

    private void initFacebook() {

        // Initialize Facebook Login button
        mCallbackManager = CallbackManager.Factory.create();

        mFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("email", "public_profile"));
                LoginManager.getInstance().registerCallback(
                        mCallbackManager, new FacebookCallback<LoginResult>()
                        {
                            @Override
                            public void onSuccess(LoginResult loginResult) {
                                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                                handleFacebookAccessToken(loginResult.getAccessToken());
                            }
                            @Override
                            public void onCancel() {
                                Log.d(TAG, "facebook:onCancel");
                            }
                            @Override
                            public void onError(FacebookException error) {
                                Log.d(TAG, "facebook:onError", error);
                            }
                        }
                );
            }
        });

    }

    private void loginGoole() {

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                        Toast.makeText(LoginActivity.this,"Connexion echouée",Toast.LENGTH_SHORT).show();

                    }
                }).addApi(Auth.GOOGLE_SIGN_IN_API, gso).build();

        btnLoginGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleSignInClient);
                startActivityForResult(signInIntent, RC_SIGN_IN);

            }
        });
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithCredential:success");
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            loadinBar.dismiss();

                        } else {
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            String message = task.getException().getMessage();
                            startActivity(new Intent(LoginActivity.this, LoginActivity.class));
                            Toast.makeText(LoginActivity.this, "Pas d'authentification, réessayez"+message,Toast.LENGTH_SHORT).show();
                            loadinBar.dismiss();
                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result back to the Facebook SDK
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        Profile userProfile = Profile.getCurrentProfile();
        setUpImageAndInfo(userProfile);

        if (requestCode == RC_SIGN_IN) {

            loadinBar.setTitle( "" );
            loadinBar.setMessage( "veuillez patienter, nous vous autorisons à vous connecter à votre compte" );
            loadinBar.setCanceledOnTouchOutside( false );
            loadinBar.show();

            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent( data );

            if (result.isSuccess()) {

                GoogleSignInAccount account = result.getSignInAccount();

                firebaseAuthWithGoogle( account );
                Toast.makeText( LoginActivity.this, "Veuillez patientez s'il vous plait !", Toast.LENGTH_SHORT ).show();
            }
        }

    }

    private void init() {

        FirebaseMessaging.getInstance().subscribeToTopic("alerte");

        firebaseFirestore = FirebaseFirestore.getInstance( );
        loadinBar = new ProgressDialog(this);
        mFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        forget_password = (TextView) findViewById(R.id.forgot_password);
        createAccount = (TextView) findViewById(R.id.textViewCreateAccount);
        login_email_address = (EditText) findViewById(R.id.email_login);
        login_password = (EditText) findViewById(R.id.password_login);
        btnLogin = (Button) findViewById(R.id.btn_login);
        btnLoginGoogle = findViewById(R.id.logGoogleSignInButton);
        //Facebook
        mFacebook = findViewById(R.id.btn_login_fb);

        forget_password.setText(fromHtml("<font color='#999999'>Mot de passe oublié ?</font><font color='#00b8d4'><style ='bold' >Réinitialiser.</style></font>"));
        forget_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, ForgetPasswordActivity.class);
                startActivity(intent);
            }
        });
        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                //this.overridePendingTransition(R.anim.animation_enter,
                // R.anim.animation_leave);
            }
        });

        TextView textViewCreateAccount = (TextView) findViewById(R.id.textViewCreateAccount);
        textViewCreateAccount.setText(fromHtml("<font color='#999999'>Vous n'avez pas de compte ?</font><font color='#00b8d4'><style ='bold' >Inscrivez-vous.</style></font>"));
        textViewCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });


    }

    private void login() {

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadinBar.dismiss();
                boolean service = checkNetworkConnectionStatus();

                //Toast.makeText( LoginActivity.this, ""+service, Toast.LENGTH_SHORT ).show();
                if (service == false) {

                    tryAgain();

                }else{

                    String email = login_email_address.getText().toString();
                    String password = login_password.getText().toString();

                    if (TextUtils.isEmpty(email) ){
                        Toast.makeText(LoginActivity.this,"Veuillez saisir votre adresse e-mail...", Toast.LENGTH_LONG).show();

                    }else  if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                        login_email_address.setError("Veuillez saisir une adresse email valide!");

                    }else if (TextUtils.isEmpty(password)){
                        Toast.makeText(LoginActivity.this,"Veuillez saisir un mot de passe...", Toast.LENGTH_LONG).show();

                    }else if (password.length() < 6){
                        login_password.setError("Minimum 6 caracteres!");

                    }
                    else{
                        loadinBar.setTitle("");
                        loadinBar.setMessage("Veuillez patienter un instant.");
                        loadinBar.setCanceledOnTouchOutside(false);
                        loadinBar.show();

                        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {


                                if (task.isSuccessful()) {

                                    // UPDATE TOKEN ID
                                    updateToken();

                                    startActivity( new Intent( LoginActivity.this, MainActivity.class ) );
                                    finish();
                                    overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);


                                    Toast.makeText(LoginActivity.this, "login in! " , Toast.LENGTH_LONG).show();
                                    loadinBar.dismiss();

                                } else {
                                    loadinBar.dismiss();

                                    String erroMessage = task.getException().getMessage();
                                    Toast.makeText(LoginActivity.this, "error", Toast.LENGTH_LONG).show();
                                }

                                loadinBar.dismiss();

                            }
                        });


                    }

                }

            }
        });
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
                startActivity( new Intent( LoginActivity.this,LoginActivity.class ) );
            }
        } );
    }

    public void updateToken(){

        String token_id = FirebaseInstanceId.getInstance().getToken();
        String current_id = mAuth.getCurrentUser().getUid();
        Map<String,Object> tokenMap = new HashMap<>();
        tokenMap.put("token_id", token_id );
        mFirestore.collection("Users").document(current_id).update(tokenMap).addOnSuccessListener( new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                loadinBar.dismiss();
            }
        } );

    }

    public void setUpImageAndInfo(Profile userProfile) {
        //This method will fill up the ImageView and TextView
        // that we initialized before.
        if (userProfile !=null) {
            final String userInfo = userProfile.getFirstName() + " " + userProfile.getLastName();
            final String user_id = userProfile.getId();
            final String imageUrl = "https://graph.facebook.com/" + userProfile.getId().toString() + "/picture?type=large";

            SharedPreferences preferences = getApplicationContext().getSharedPreferences("MyPrefSetup", MODE_PRIVATE);

            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("user_id",user_id);
            editor.putString("name",userInfo);
            editor.putString("image",imageUrl);
            editor.commit();

            Toast.makeText( this, "user_id_face "+user_id, Toast.LENGTH_SHORT ).show();

            mFirestore.collection( "Users" ).document( user_id ).get().addOnSuccessListener( new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()){
                        String name = documentSnapshot.getString( "name" );
                        String telephone = documentSnapshot.getString( "telephone" );
                        String telephoneEmergency = documentSnapshot.getString( "telephoneEmergency" );
                        String image = documentSnapshot.getString( "image" );
                        if (!TextUtils.isEmpty( name ) && !TextUtils.isEmpty( telephone ) && !TextUtils.isEmpty( image ) && !TextUtils.isEmpty( telephoneEmergency )) {
                            Intent mainIntent = new Intent( LoginActivity.this,MainActivity.class);

                            SharedPreferences prefMain = getApplicationContext().getSharedPreferences("MyPrefMain", MODE_PRIVATE);
                            SharedPreferences.Editor editor = prefMain.edit();
                            editor.putString("user_id",user_id);
                            editor.commit();

                            startActivity(mainIntent);
                        }
                    } else {
                        startActivity(logFacebook);

                    }
                }
            } );

        }else Toast.makeText(LoginActivity.this, "profile null", Toast.LENGTH_SHORT ).show();

    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");

                            profileTracker = new ProfileTracker() {
                                @Override
                                protected void onCurrentProfileChanged(Profile oldProfile, Profile newProfile) {

                                    //final  Intent setupFacebookIntent = new Intent(LoginActivity.this, SetupFacebookActivity.class);
                                    if (newProfile == null) {
                                        Intent setupIntent = new Intent( LoginActivity.this, SetupActivity.class );
                                        startActivity(setupIntent);
                                    }
                                }
                            };
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
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




}
