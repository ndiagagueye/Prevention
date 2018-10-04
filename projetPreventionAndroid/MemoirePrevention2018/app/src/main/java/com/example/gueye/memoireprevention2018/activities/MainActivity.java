package com.example.gueye.memoireprevention2018.activities;
        import android.content.DialogInterface;
        import android.content.Intent;
        import android.content.SharedPreferences;
        import android.content.pm.PackageManager;
        import android.graphics.drawable.Drawable;
        import android.net.Uri;
        import android.support.annotation.NonNull;
        import android.support.design.widget.BottomNavigationView;
        import android.support.design.widget.FloatingActionButton;
        import android.support.v4.app.ActivityCompat;
        import android.support.v4.app.Fragment;
        import android.support.v4.app.FragmentTransaction;
        import android.support.v4.content.ContextCompat;
        import android.support.v7.app.AlertDialog;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.support.v7.widget.Toolbar;
        import android.util.Log;
        import android.view.Menu;
        import android.view.MenuItem;
        import android.view.View;
        import android.widget.ImageView;
        import android.widget.TextView;
        import android.widget.Toast;

        import com.example.gueye.memoireprevention2018.R;
        import com.example.gueye.memoireprevention2018.appServices.ShakeService;
        import com.example.gueye.memoireprevention2018.fragments.HomeFragment;
        import com.example.gueye.memoireprevention2018.fragments.NotificationFragment;
        import com.example.gueye.memoireprevention2018.modele.Notifications;
        import com.example.gueye.memoireprevention2018.modele.Users;
        import com.example.gueye.memoireprevention2018.utils.Const;
        import com.example.gueye.memoireprevention2018.utils.MySharePreference;
        import com.facebook.login.LoginManager;
        import com.google.android.gms.tasks.OnCompleteListener;
        import com.google.android.gms.tasks.OnSuccessListener;
        import com.google.android.gms.tasks.Task;
        import com.google.firebase.auth.FirebaseAuth;
        import com.google.firebase.auth.FirebaseUser;
        import com.google.firebase.auth.UserInfo;
        import com.google.firebase.database.DataSnapshot;
        import com.google.firebase.database.DatabaseError;
        import com.google.firebase.database.DatabaseReference;
        import com.google.firebase.database.FirebaseDatabase;
        import com.google.firebase.database.ValueEventListener;
        import com.google.firebase.firestore.DocumentChange;
        import com.google.firebase.firestore.DocumentSnapshot;
        import com.google.firebase.firestore.EventListener;
        import com.google.firebase.firestore.FirebaseFirestore;
        import com.google.firebase.firestore.FirebaseFirestoreException;
        import com.google.firebase.firestore.QuerySnapshot;
        import com.google.firebase.messaging.FirebaseMessaging;
        import com.mikepenz.materialdrawer.AccountHeader;
        import com.mikepenz.materialdrawer.AccountHeaderBuilder;
        import com.mikepenz.materialdrawer.Drawer;
        import com.mikepenz.materialdrawer.DrawerBuilder;
        import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
        import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
        import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
        import com.mikepenz.materialdrawer.model.interfaces.IProfile;
        import com.mikepenz.materialdrawer.util.AbstractDrawerImageLoader;
        import com.mikepenz.materialdrawer.util.DrawerImageLoader;
        import com.squareup.picasso.Picasso;

        import java.util.ArrayList;
        import java.util.List;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private String user_id_login_with_facebook;
    private FirebaseFirestore firebaseFirestore;
    private BottomNavigationView mainBottomNav;
    private HomeFragment homeFragment;
    private FirebaseUser fUser;
    private String current_user_id;
    private String userIdFace;
    private static final int REQUEST_CALL = 1;
    private DatabaseReference reference;
    //private AccountFragment accountFragment;
    private NotificationFragment notificationFragment;
    private FloatingActionButton flabAddNewAlerte;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );

        mAuth = FirebaseAuth.getInstance();

        SharedPreferences preferences = getApplicationContext().getSharedPreferences("MyPrefMain", MODE_PRIVATE);
        userIdFace = preferences.getString("user_id", null);
        fUser = FirebaseAuth.getInstance().getCurrentUser();

        //user_is_logged_in_via_Facebook_in_Firebase_Auth();

        FirebaseMessaging.getInstance().subscribeToTopic("alerte");
        //Log.d( "Message Token", Common.currentToken );

        firebaseFirestore = FirebaseFirestore.getInstance();

        navigationDrawer( savedInstanceState, Const.AVATAR, "", "" );
        current_user_id = getCurrentUserId();

        SharedPreferences pref = getApplicationContext().getSharedPreferences("ShareIdUser", MODE_PRIVATE);

        SharedPreferences.Editor editor = pref.edit();
        editor.putString("user_id",current_user_id);
        editor.commit();

            Log.d( "MainActivity", "USER_ID"+current_user_id);

            if (current_user_id != null){

                firebaseFirestore.collection("Users").document(current_user_id).get().addOnSuccessListener( new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()){
                            String name = documentSnapshot.getString("name");
                            String telephone = documentSnapshot.getString("telephone");
                            String image = documentSnapshot.getString("image");
                            if (name != null && telephone != null && image != null) navigationDrawer( savedInstanceState, image, name, telephone );
                        }
                    }
                } );

            }else {
                sendTologin();
            }




            flabAddNewAlerte = (FloatingActionButton) findViewById( R.id.fl_add_new_alerte );

            flabAddNewAlerte = (FloatingActionButton) findViewById( R.id.fl_add_new_alerte );

            flabAddNewAlerte.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    sendUserToSendAlertActivity();
                }
            } );

            mainBottomNav = findViewById( R.id.mainBottomNav );

            getNotification();


            // FRAGMENT
            notificationFragment = new NotificationFragment();
            //accountFragment = new AccountFragment();
            homeFragment = new HomeFragment();

            initializeFragment();

            //replaceFragment(homeFragment);

            //MainNavigationView
            mainBottomNav.setOnNavigationItemSelectedListener( new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                    Fragment currentFragment = getSupportFragmentManager().findFragmentById( R.id.main_container );

                    switch (item.getItemId()) {

                        case R.id.bottom_action_home:

                            replaceFragment( homeFragment, currentFragment );
                            return true;

                        // case R.id.bottom_action_account:

                        //replaceFragment(accountFragment, currentFragment);
                        //return true;

                        case R.id.bottom_action_notif:

                            //mNotifReceveid.setVisibility( View.INVISIBLE );

                            replaceFragment( notificationFragment, currentFragment );
                            return true;

                        default:
                            return false;
                    }
                }
            } );



            flabAddNewAlerte.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    sendUserToSendAlertActivity();
                }
            } );


}

   private boolean user_is_logged_in_via_Facebook_in_Firebase_Auth(){

       if (fUser != null) {
           for (UserInfo userInfo : fUser.getProviderData()) {
               if (userInfo.getProviderId().equals("facebook.com")) {
                   Toast.makeText( this, "Using facebook", Toast.LENGTH_SHORT ).show();
                   return true;
               }
           }
           Toast.makeText( this, "Using Firebase", Toast.LENGTH_SHORT ).show();

           return false;
       }
       return false;
   }


    // Dialling phone
    private void makePhoneCall() {
        String current_user_id = mAuth.getCurrentUser().getUid();
        firebaseFirestore.collection( "Users" ).document(current_user_id).get().addOnSuccessListener( new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                if (documentSnapshot.exists()) {

                    String number = documentSnapshot.getString( "telephoneEmergency" );

                    if (number.trim().length() > 0) {

                        if (ContextCompat.checkSelfPermission(MainActivity.this,
                                android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{android.Manifest.permission.CALL_PHONE}, REQUEST_CALL);
                        } else {
                            String dial = "tel:" + number;
                            startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
                        }

                    }else {
                        Toast.makeText(MainActivity.this, "Veuillez completer votre profile", Toast.LENGTH_SHORT).show();
                    }

                }

            }
        } );

    }


    private void navigationDrawer(final Bundle savedInstanceState,String image, String name,String telephone) {


        //initialize and create the image loader logic
        DrawerImageLoader.init( new AbstractDrawerImageLoader() {
            @Override
            public void set(ImageView imageView, Uri image, Drawable placeholder) {
                Picasso.with( imageView.getContext() ).load( image ).placeholder( placeholder ).into( imageView );
            }

            @Override
            public void cancel(ImageView imageView) {
                Picasso.with( imageView.getContext() ).cancelRequest( imageView );
            }


        } );

        final IProfile profile = new ProfileDrawerItem().withName( name ).withEmail( telephone ).withIcon( image ).withIdentifier( 101 );

        //Navigation drawer
        new DrawerBuilder().withActivity( MainActivity.this ).build();

        AccountHeader accountHeader = new AccountHeaderBuilder().withActivity( MainActivity.this ).withHeaderBackgroundScaleType( ImageView.ScaleType.FIT_CENTER ).withHeightDp( 200 ).addProfiles( profile ).withHeaderBackground( R.color.colorPrimary ).build();

        //secondary items
        SecondaryDrawerItem home = (SecondaryDrawerItem) new SecondaryDrawerItem().withIdentifier( 1 ).withName( "Accueil" ).withIcon( R.drawable.home1 );
        SecondaryDrawerItem my_profile = (SecondaryDrawerItem) new SecondaryDrawerItem().withIdentifier( 2 ).withName( "Mon Profile" ).withIcon( R.drawable.man );
        SecondaryDrawerItem add_alert = (SecondaryDrawerItem) new SecondaryDrawerItem().withIdentifier( 3 ).withName( "Alertés" ).withIcon( R.drawable.clock );

        //settings, help, urgences items
        SecondaryDrawerItem help = (SecondaryDrawerItem) new SecondaryDrawerItem().withIdentifier( 4 ).withName( "Aide" ).withIcon( R.drawable.helpp );
        SecondaryDrawerItem location = (SecondaryDrawerItem) new SecondaryDrawerItem().withIdentifier( 5 ).withName( "Ma position" ).withIcon( R.drawable.marker_map );
        SecondaryDrawerItem urgences = (SecondaryDrawerItem) new SecondaryDrawerItem().withIdentifier( 6 ).withName( "Urgences" ).withIcon( R.drawable.call );
        SecondaryDrawerItem apropos = (SecondaryDrawerItem) new SecondaryDrawerItem().withIdentifier( 7 ).withName( "A propos" ).withIcon( R.drawable.about );
        SecondaryDrawerItem membres = (SecondaryDrawerItem) new SecondaryDrawerItem().withIdentifier( 8 ).withName( "membres" ).withIcon( R.drawable.usersgroup );
        SecondaryDrawerItem benevol = (SecondaryDrawerItem) new SecondaryDrawerItem().withIdentifier( 9 ).withName( "Bénévoles" ).withIcon( R.drawable.help );
        SecondaryDrawerItem logout = (SecondaryDrawerItem) new SecondaryDrawerItem().withIdentifier( 10 ).withName( "Deconnexion" ).withIcon( R.drawable.logoutt );

        //Toolbar
        Toolbar toolbar = (Toolbar) findViewById( R.id.main_page_toolbar );
        setSupportActionBar( toolbar );
        getSupportActionBar().setTitle( "Accueil" );
        getSupportActionBar().setDisplayHomeAsUpEnabled( true );
        getSupportActionBar().setHomeButtonEnabled( false );


        new DrawerBuilder().withHeaderDivider( true ).withActivity( MainActivity.this ).withAccountHeader( accountHeader ).withToolbar( toolbar ).withActionBarDrawerToggleAnimated( true ).withTranslucentStatusBar( false ).withFullscreen( true ).withSavedInstance( savedInstanceState ).addDrawerItems(

                //new SectionDrawerItem(),//.withName("Categories"),
                home, my_profile,
                //new DividerDrawerItem(),
                add_alert, location, urgences, membres, benevol, help, apropos, logout

        ).withOnDrawerItemClickListener( new Drawer.OnDrawerItemClickListener() {
            @Override
            public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                if (drawerItem != null) {
                    Intent intent = null;
                    if (drawerItem.getIdentifier() == 1) {
                        intent = new Intent( MainActivity.this, MainActivity.class );
                    } else if (drawerItem.getIdentifier() == 2) {
                        intent = new Intent( MainActivity.this, ProfilActivity.class );
                        startActivity( intent );

                    } else if (drawerItem.getIdentifier() == 3) {

                        intent = new Intent(MainActivity.this, SendAlerteActivity.class);
                        //startActivity(intent);
                        //finish();
                        overridePendingTransition(R.anim.animation_enter, R.anim.animation_leave);

                    } else if (drawerItem.getIdentifier() == 4) {
                        //intent = new Intent(MainActivity.this, Class.class);
                    } else if (drawerItem.getIdentifier() == 5) {


                        intent = new Intent(MainActivity.this, MyPositionActivity.class);

                    } else if (drawerItem.getIdentifier() == 6) {

                        makePhoneCall();

                        //intent = new Intent(MainActivity.this, EmergencyCallActivity.class);

                    } else if (drawerItem.getIdentifier() == 7) {

                        intent = new Intent(MainActivity.this, AproposActivity.class);
                        finish();
                        overridePendingTransition(R.anim.animation_enter, R.anim.animation_leave);

                    } else if (drawerItem.getIdentifier() == 8) {

                        intent = new Intent(MainActivity.this, ChatActivity.class);
                        overridePendingTransition(R.anim.animation_enter, R.anim.animation_leave);

                    } else if (drawerItem.getIdentifier() == 9) {

                        intent = new Intent(MainActivity.this, ListBenevolActivity.class);
                        overridePendingTransition(R.anim.animation_enter, R.anim.animation_leave);


                    } else if (drawerItem.getIdentifier() == 10) {

                        logout();

                    }
                    if (intent != null) {
                        MainActivity.this.startActivity( intent );
                    }
                }

                return false;
            }
        } ).build();
        //End of Navigation drawer
    }


    private void logout() {
        new AlertDialog.Builder(this)
                //.setTitle("Deconnection")
                .setMessage("Voulez-vous vraiment vous déconnecter ?")
                .setPositiveButton("Déconnection", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        mAuth.signOut();
                        LoginManager.getInstance().logOut();
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity( intent );
                        overridePendingTransition(R.anim.animation_enter, R.anim.animation_leave);

                    }
                })
                .setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    ////Get Notification

    public void getNotification(){
        countNotif();
    }

    public String getCurrentUserId(){
        String userId = null;
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth != null)
            userId = firebaseAuth.getUid();

        if (user_is_logged_in_via_Facebook_in_Firebase_Auth() == true){
            return  userIdFace;
        }
        return userId;
    }



    public void countNotif(){

        final TextView countNotif = findViewById( R.id.count_notif );
        firebaseFirestore.collection("Notifications").whereEqualTo( "status", "0").addSnapshotListener( new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                if (documentSnapshots == null) return;
                if(documentSnapshots.isEmpty()){
                    countNotif.setVisibility( View.INVISIBLE );
                }else{
                    int count = ((int) documentSnapshots.size());

                    List<Notifications> mNotifList = new ArrayList<>(  );
                    for(DocumentChange doc: documentSnapshots.getDocumentChanges()) {

                        if (doc.getType() == DocumentChange.Type.ADDED) {

                            Notifications notifications = doc.getDocument().toObject( Notifications.class );

                            if (current_user_id !=null){
                                if (!current_user_id.equals( notifications.getFromNotif() )) {
                                    mNotifList.add( notifications );

                                    countNotif.setVisibility( View.VISIBLE );
                                    countNotif.setText(""+ count );

                                }
                            }

                        }
                    }

                }
            }
        } );
    }


    private void sendUserToSendAlertActivity() {

        Intent sendAlertIntent = new Intent(MainActivity.this, SendAlerteActivity.class);

        startActivity(sendAlertIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.navigation_left, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_apropos:
                // TODO Something when menu item selected
                startActivity(new Intent( MainActivity.this, AproposActivity.class ));
                return true;

            case R.id.menu_help:
                // TODO Something when menu item selected
                startActivity(new Intent( MainActivity.this, HelpActivity.class ));

                return true;

            case R.id.menu_call:
                // TODO Something when menu item selected
                startActivity(new Intent( MainActivity.this, EmergencyCallActivity.class ));

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override

    protected void onStart(){

        //launch the service for emmergency alerte
        startServiceApp();

        super.onStart();
        // Get instance for current user
        FirebaseUser currentUser =  FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null && user_is_logged_in_via_Facebook_in_Firebase_Auth() == false){

            sendTologin();

        }else{

            current_user_id = getCurrentUserId();
            firebaseFirestore.collection("Users").document(current_user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()){
                        if (!task.getResult().exists()){
                            Intent setupIntent = new Intent(MainActivity.this, SetupActivity.class);
                            startActivity(setupIntent);
                            finish();
                        }
                    }
                    else{
                        String errorMsg = task.getException().getMessage();
                        Toast.makeText(MainActivity.this, "Error accured", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    private void startServiceApp() {

        Intent shakeIntent = new Intent(MainActivity.this, ShakeService.class);
        shakeIntent.setAction("com.example.gueye.memoireprevention2018.appServices.ShakeService");
        startService(shakeIntent);

        Log.d("Main Activity", "onStart:  shake service is running  ");
    }

    private void sendTologin() {

        Intent loginIntent = new Intent(MainActivity.this,LoginActivity.class);
        startActivity(loginIntent);
        finish();
    }

    private void replaceFragment(Fragment fragment, Fragment currentFragment){

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if(fragment == homeFragment){

            //fragmentTransaction.hide(accountFragment);
            fragmentTransaction.hide(notificationFragment);

        }

       /* if(fragment == accountFragment){

            fragmentTransaction.hide(homeFragment);
            fragmentTransaction.hide(notificationFragment);

        }*/

        if(fragment == notificationFragment){

            fragmentTransaction.hide(homeFragment);
            //fragmentTransaction.hide(accountFragment);

        }
        fragmentTransaction.show(fragment);

        //fragmentTransaction.replace(R.id.main_container, fragment);
        fragmentTransaction.commit();

    }



    private void initializeFragment(){

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        fragmentTransaction.add(R.id.main_container, homeFragment);
        fragmentTransaction.add(R.id.main_container, notificationFragment);
        //fragmentTransaction.add(R.id.main_container, accountFragment);

        fragmentTransaction.hide(notificationFragment);
        //fragmentTransaction.hide(accountFragment);

        fragmentTransaction.commit();

    }







}
