package com.example.gueye.memoireprevention2018.activities;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.gueye.memoireprevention2018.R;
import com.example.gueye.memoireprevention2018.adaptaters.CommentsRecyclerAdapter;
import com.example.gueye.memoireprevention2018.adaptaters.LikesAdaptater;
import com.example.gueye.memoireprevention2018.modele.Comments;
import com.example.gueye.memoireprevention2018.services.GoogleMapsServices;
import com.example.gueye.memoireprevention2018.utils.Const;
import com.example.gueye.memoireprevention2018.utils.ParserTask;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

import static java.lang.String.valueOf;

public class PostAlerteDetails extends AppCompatActivity implements OnMapReadyCallback ,GoogleApiClient.OnConnectionFailedListener{

    //views

    private TextView tvDescriptionPost ;
    private TextView tvTypePost ;
    private TextView tvUsername;
    private TextView tvDatePost;
    private TextView tvLatitude;
    private TextView tvLongitude;

    private ImageView ivPostImage;
    private CircleImageView civPostUserImage;

    private Toolbar toolbar;
    public ImageView ivSeeTrajet;

    private RecyclerView rvLikes;
    private RecyclerView rvComments;

    private View mainScrollView;
    private View bottomSheetScrool ;

    private BottomSheetBehavior mBottomSheetBehavior1;


    private static final String TAG = "PostAlerteDetails";
    private GoogleMapsServices mapsServices;
    private boolean isPermissionsGranted =false;
    private GoogleMap mMap;
    private double longitude ;
    private double latitude ;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private ArrayList<Comments> commentsList;
    private String current_user_id;
    private String blog_post_id;
    private CommentsRecyclerAdapter commentsRecyclerAdapter;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Location currentLocationDevice;

    public LatLng currentLatLng;
    public LatLng destLatLng ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_alerte_details);

        toolbar = (Toolbar)findViewById(R.id.post_details_toolbar);


        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        mapsServices = new GoogleMapsServices(this, TAG);

        ivSeeTrajet = (ImageView) findViewById(R.id.iv_see_trajet);

        ivSeeTrajet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });




        mainScrollView = findViewById(R.id.scrollView_main);
        bottomSheetScrool = (NestedScrollView) findViewById(R.id.bottom_sheet_scroll);

        mBottomSheetBehavior1 = BottomSheetBehavior.from(bottomSheetScrool);
        mBottomSheetBehavior1.setPeekHeight(120);
        mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_COLLAPSED);
        mBottomSheetBehavior1.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

                switch (newState){

                    case BottomSheetBehavior.STATE_COLLAPSED : mainScrollView.setVisibility(View.VISIBLE);break;

                    case BottomSheetBehavior.STATE_EXPANDED: mainScrollView.setVisibility(View.GONE); break;

                    case BottomSheetBehavior.STATE_DRAGGING: mainScrollView.setVisibility(View.GONE); break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        mainScrollView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mBottomSheetBehavior1.getState() == BottomSheetBehavior.STATE_COLLAPSED) {

                    mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_EXPANDED);

                }


            }
        });


        tvUsername = (TextView) findViewById(R.id.tv_post_username);
        tvDatePost = (TextView) findViewById(R.id.tv_post_date);
        tvDescriptionPost = (TextView) findViewById(R.id.tv_post_description);
        tvLatitude  = (TextView) findViewById(R.id.tv_post_latitude);
        tvLongitude = (TextView) findViewById(R.id.tv_post_longitude);
        tvTypePost = (TextView) findViewById(R.id.tv_post_type);
        ivPostImage = (ImageView) findViewById(R.id.iv_post_image);
        civPostUserImage= (CircleImageView) findViewById(R.id.civ_image_profile_post);
        rvLikes = (RecyclerView) findViewById(R.id.recycle_post_likes);
        rvComments = (RecyclerView) findViewById(R.id.recycle_post_comments);

        latitude = getIntent().getDoubleExtra(Const.LAT ,0.d);
        longitude = getIntent().getDoubleExtra(Const.LONG,0.d);

        // fro the map ;

        init();

        //for the post it-self

        getCurrentLocation(getApplicationContext(), "Post alerte details");

        showCurrentPost();


        ivSeeTrajet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setupTrajet();
            }
        });


    }



    private void init() {



        Toast.makeText(this, " latitude "+ latitude +"  longitude "+ longitude , Toast.LENGTH_SHORT).show();


        isPermissionsGranted = mapsServices.getLocationPermission(this);

        if(isPermissionsGranted){

            Toast.makeText(this, "permissions granted", Toast.LENGTH_SHORT).show();
        }

        initmMap();

    }


    private void verifyServices() {



        boolean serviceOk = mapsServices.isServicesOk(PostAlerteDetails.this);

        Toast.makeText(this, "services "+serviceOk, Toast.LENGTH_SHORT).show();

        if (serviceOk) {

            Toast.makeText(this, "Services are OK !!!", Toast.LENGTH_SHORT).show();

            // get the location post

            getPostLocation(latitude,longitude );

        }

    }

    private void initmMap() {

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Toast.makeText(this, "map try to initialise it ", Toast.LENGTH_SHORT).show();


    }

    public void getPostLocation (double latitude , double longitude ) {


        Double latitudeDevice =0.d;
        Double longitudeDevice = 0.d;

        Log.d(TAG, "getPostLocation: permissions "+ isPermissionsGranted);

        Toast.makeText(this, "Permissions "+isPermissionsGranted, Toast.LENGTH_SHORT).show();

        if (isPermissionsGranted) {

            Toast.makeText(this, "lat "+latitude, Toast.LENGTH_SHORT).show();



             destLatLng = new LatLng(latitude,longitude);

            moveCameraTo(destLatLng, Const.DEFAULT_ZOOM_MAP,"no tittle pour l'instant");


            Log.d(TAG, "getDeviceLocation: getting devices location");



        } else {
            Log.d(TAG, "getCurrentLocation: asking permissions ");
            mapsServices.askPermissions();
        }

    }


    private void moveCameraTo(LatLng latLng, float zoom , String title) {

        Log.d(TAG, "mooveCamera:  mooving the camera to latitude : "+ latLng.latitude +" and longitude "+latLng.longitude);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,zoom));

        mMap.clear();

        Toast.makeText(this, "Trying to moove the camera ...  ", Toast.LENGTH_SHORT).show();

        if(!title.equals("My location")){

            MarkerOptions options = new MarkerOptions()
                    .position(latLng)
                    .title(title);

            mMap.addMarker(options);
        }


    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        Log.d(TAG, "onMapReady: loading map ");
        mMap = googleMap;
        Toast.makeText(this, "Map's is ready !!!", Toast.LENGTH_SHORT).show();



        if (isPermissionsGranted) {


            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);

            verifyServices();

        }

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        if(connectionResult.isSuccess()){

            Toast.makeText(this, "Conexion reussie ", Toast.LENGTH_SHORT).show();
        }else{

            Toast.makeText(this, "Connection faild "+ connectionResult.getErrorMessage(), Toast.LENGTH_SHORT).show();
        }

    }


    /// for the post ------------- Post -----------------

    private void showCurrentPost() {

        String username = getIntent().getStringExtra(Const.USERNAME);
        Log.d(TAG, "showCurrentPost: username "+username);
        String userImage = getIntent().getStringExtra(Const.USERNAME_IMAGE);
        String postImage = getIntent().getStringExtra(Const.IMAGE_POST);
        String type = getIntent().getStringExtra(Const.TYPE);
        String date = getIntent().getStringExtra(Const.DATE);

        Log.d(TAG, "showCurrentPost: latitude "+ latitude);

        Toast.makeText(this, "longitude "+longitude, Toast.LENGTH_SHORT).show();

        String description = getIntent().getStringExtra(Const.DESC_DATA);
        String blogPostId = getIntent().getStringExtra(Const.BLOG_POST_ID);

        RequestOptions placeholderOption = new RequestOptions();
        placeholderOption.placeholder(R.drawable.profile_placeholder);

        Glide.with(this).applyDefaultRequestOptions(placeholderOption).load(userImage).into(civPostUserImage);
        Glide.with(this).applyDefaultRequestOptions(placeholderOption).load(postImage).into(ivPostImage);

        tvUsername.setText(username);
        tvTypePost.setText(type);
        tvLongitude.setText("" +longitude);
        tvLatitude.setText(""+latitude);
        tvDatePost.setText(date);
        tvDescriptionPost.setText(description);


        setupRecycleViews(blogPostId);

    }


    public void  getCurrentLocation(final Context context, final String TAG_ACTIVITY) {

        isPermissionsGranted = mapsServices.getLocationPermission(context);

        final double[] position = new double[2];

        if (isPermissionsGranted) {


            Log.d(TAG, "getDeviceLocation: getting devices location");

            mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);

            try {

                Log.d(TAG_ACTIVITY, "getDeviceLocation: permission is granted , we try now to get the current location");

                Task location = mFusedLocationProviderClient.getLastLocation();

                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {

                        Log.d(TAG_ACTIVITY, "onComplete: we are trying to found the current location ");

                        Toast.makeText(context, "Trying to found the current location ", Toast.LENGTH_SHORT).show();

                        if (task.isSuccessful() && task.getResult() != null) {

                            Toast.makeText(context, "C'est cool ", Toast.LENGTH_SHORT).show();

                            Log.d(TAG, "onComplete: we have found the location " + task.getResult().toString());

                            currentLocationDevice = (Location) task.getResult();

                             currentLatLng = new LatLng(currentLocationDevice.getLatitude() , currentLocationDevice.getLongitude());

                            double latitude = currentLatLng.latitude;

                            double longitude = currentLatLng.longitude;

                            Toast.makeText(context, "Location found ", Toast.LENGTH_SHORT).show();

                        } else {

                            Toast.makeText(context, "location not found ", Toast.LENGTH_SHORT).show();

                        }

                        Log.d(TAG, "onComplete: currentLocation " + currentLocationDevice);
                    }
                });

            } catch (SecurityException e) {

                Log.d(TAG, "getDeviceLocation: SecurityException : " + e.getMessage());
            }


        } else {
            Log.d(TAG, "getCurrentLocation: asking permissions ");
            mapsServices.askPermissions();
        }

    }

    private void setupRecycleViews(String blogPostId) {

        LikesAdaptater likesAdaptater = new LikesAdaptater();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvLikes.setLayoutManager(linearLayoutManager);
        rvLikes.setAdapter(likesAdaptater);


        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        current_user_id = firebaseAuth.getCurrentUser().getUid();


        //RecyclerView Firebase List
        commentsList = new ArrayList<>();
        commentsRecyclerAdapter = new CommentsRecyclerAdapter(commentsList);
        rvComments.setHasFixedSize(true);
        rvComments.setLayoutManager(new LinearLayoutManager(this));



        firebaseFirestore.collection("Posts/" + blogPostId + "/Comments").orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener(PostAlerteDetails.this, new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                        if (!documentSnapshots.isEmpty()) {

                            for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                                if (doc.getType() == DocumentChange.Type.ADDED) {
                                    String commentId = doc.getDocument().getId();
                                    Comments comments = doc.getDocument().toObject(Comments.class).withId(commentId);
                                    commentsList.add(comments);
                                    commentsRecyclerAdapter.notifyDataSetChanged();

                                    rvComments.setAdapter(commentsRecyclerAdapter);

                                }
                            }

                        }

                    }
                });

         }

        public void setupTrajet(){

        mMap.clear();

        if(currentLatLng != null){

            MarkerOptions originMarkerOptions = new MarkerOptions()
                    .position(currentLatLng)
                    .title("Ma position").
                    icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

            MarkerOptions destMarkerOptions = new MarkerOptions()
                    .position(destLatLng)
                    .title("Position Alerte")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));



            mMap.addMarker(originMarkerOptions);
            mMap.addMarker(destMarkerOptions);
            String url = getDirectionsUrl(currentLatLng, destLatLng);

            DownloadTask downloadTask = new DownloadTask();

            // Start downloading json data from Google Directions API
            downloadTask.execute(url);

            Log.d(TAG, "setupTrajet:  current location is "+ currentLatLng + " And also des location is "+ destLatLng);


        }else{

            Toast.makeText(this, "Current location is null check your location enbale ", Toast.LENGTH_SHORT).show();
        }





        }



    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";
        String mode = "mode=driving";


        //renseigner ici la clé
        String key = "AIzaSyBrwhQJgU4lnB5TQRVKmtkAtNu-1Eme25A";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + mode + "&" +key;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;

        Log.d(TAG, "getDirectionsUrl: url is "+ url);


        return url;
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.connect();

            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);

                Log.d(TAG, "downloadUrl: line is "+ line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }

        Log.d(TAG, "downloadUrl: here is the download url "+ data);
        return data;
    }

    private class DownloadTask extends AsyncTask <String ,Integer, String> {



        @Override
        protected String doInBackground(String... url) {

            String data = "";

            try {
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;

        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask(mMap);


            parserTask.execute(result);

        }

    }
}





