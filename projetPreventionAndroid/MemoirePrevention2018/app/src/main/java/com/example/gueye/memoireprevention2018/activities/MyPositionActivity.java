package com.example.gueye.memoireprevention2018.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.gueye.memoireprevention2018.R;
import com.example.gueye.memoireprevention2018.modele.PlaceInfo;
import com.example.gueye.memoireprevention2018.services.GoogleMapsServices;
import com.example.gueye.memoireprevention2018.utils.Const;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import static com.example.gueye.memoireprevention2018.services.GoogleMapsServices.DEFAULT_ZOOM;

public class MyPositionActivity extends AppCompatActivity implements OnMapReadyCallback,GoogleApiClient.OnConnectionFailedListener{

    private static final String TAG = "PostAlerteDetails";
    private GoogleMapsServices mapsServices;
    private boolean isPermissionsGranted =false;
    private GoogleMap mMap;
    private ImageView ivMyPosition;
    private ImageView ivPlaceInfo;
    private double longitude ;
    private double latitude ;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Location currentLocation;
    private PlaceInfo placeInfo;
    private PlaceAutocompleteFragment placeAutocompleteFragment;
    private Marker marker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_my_position );

        ivMyPosition = (ImageView) findViewById( R.id.my_position) ;
        ivPlaceInfo = (ImageView) findViewById( R.id.place_info);
        placeAutocompleteFragment = (PlaceAutocompleteFragment)getFragmentManager().findFragmentById( R.id.my_place_autocomplete_fragment);

        init();
    }

    private void init() {

        latitude = getIntent().getDoubleExtra( Const.LAT ,0.d);
        longitude = getIntent().getDoubleExtra( Const.LONG,0.d);

        Toast.makeText(this, " latitude "+ latitude +"  longitude "+ longitude , Toast.LENGTH_SHORT).show();
        mapsServices = new GoogleMapsServices(this, TAG);

        isPermissionsGranted = mapsServices.getLocationPermission(this);

        if(isPermissionsGranted){

            Toast.makeText(this, "permissions granted", Toast.LENGTH_SHORT).show();
        }

        ivMyPosition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MyPositionActivity.this, "Patienter svp !!!", Toast.LENGTH_SHORT).show();
                getDeviceLocation();
            }
        });

        ivPlaceInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(placeInfo !=null){

                    String idPlace = placeInfo.getId();

                    Intent displayPlaceIntent = new Intent(MyPositionActivity.this, DisplayPlaceActivity.class);

                    displayPlaceIntent.putExtra("placeId",idPlace);
                    startActivity(displayPlaceIntent);

                    Toast.makeText(MyPositionActivity.this, "place details patienter ", Toast.LENGTH_SHORT).show();
                }

                else{

                    Toast.makeText(MyPositionActivity.this, "Please choose a place ", Toast.LENGTH_SHORT).show();
                }



            }
        });



        placeAutocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {

                String attributions=null;
                String phoneNumber = null;
                Uri websiteUri = null;
                String name = place.getName().toString();
                String address =  place.getAddress().toString();
                LatLng latLng = place.getLatLng();
                String placeId = place.getId();

                if(place.getAttributions() !=null){

                    attributions = place.getAttributions().toString();
                }

                if(place.getPhoneNumber() !=null){

                    phoneNumber  = place.getPhoneNumber().toString();
                }

                if(place.getWebsiteUri() != null ){

                    websiteUri = place.getWebsiteUri();
                }
                float rating = place.getRating();

                placeInfo = new PlaceInfo(name,address, placeId,
                        latLng, attributions,phoneNumber,websiteUri,
                        rating);

                moveCameraTo(latLng,DEFAULT_ZOOM,placeInfo);


                // recupéré une photo de la place

            }

            @Override
            public void onError(Status status) {

            }
        });

        initmMap();

    }

    private void moveCameraTo(LatLng latLng, float zoom , String title) {

        Log.d(TAG, "mooveCamera:  mooving the camera to latitude : "+ latLng.latitude +" and longitude "+latLng.longitude);

        mMap.moveCamera( CameraUpdateFactory.newLatLngZoom(latLng,zoom));

        mMap.clear();

        Toast.makeText(this, "Trying to moove the camera ...  ", Toast.LENGTH_SHORT).show();

        if(!title.equals("My location")){

            MarkerOptions options = new MarkerOptions()
                    .position(latLng)
                    .title(title);

            mMap.addMarker(options);
        }


    }

    private void getDeviceLocation(){

        Log.d(TAG, "getDeviceLocation: getting devices location");

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try{

            if(isPermissionsGranted){
                Log.d(TAG, "getDeviceLocation: permission is granted , we try now to get the current location");

                final Task location = mFusedLocationProviderClient.getLastLocation();

                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {

                        if(task.isSuccessful() ){

                            Log.d(TAG, "onComplete:  Location can found ");

                            if(task.getResult() != null){

                                currentLocation = (Location) task.getResult();

                                //we need to call moove camera method

                                Log.i(TAG, "onComplete:  current LOcation : "+ currentLocation);
                                Log.e(TAG, "onComplete: task "+task.toString() );

                                moveCameraTo(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),DEFAULT_ZOOM, "My location" );
                            }
                        }else{

                            Log.d(TAG, "onComplete:  enable to found locatiion ");
                            Toast.makeText(MyPositionActivity.this, "We can't found location , please chack your netwok connection ", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

        }catch (SecurityException e ){

            Log.d(TAG, "getDeviceLocation: SecurityException : "+ e.getMessage());
        }
    }


    private void moveCameraTo(LatLng latLng , float zoom , PlaceInfo placeInfo){

        Log.d(TAG, "mooveCamera:  mooving the camera to latitude : "+ latLng.latitude +" and longitude "+latLng.longitude);

        mMap.moveCamera( CameraUpdateFactory.newLatLngZoom(latLng,zoom));

        mMap.clear();

        try{

            if (placeInfo != null) {

                String snippet = "Adress "+ placeInfo.getAddress() + "\n "+
                        "Phone number  "+ placeInfo.getPhoneNumber() + "\n "+
                        "Website  "+ placeInfo.getWebsiteUrl() + "\n "+
                        "Place rating  "+ placeInfo.getRating() + "\n ";

                MarkerOptions options = new MarkerOptions()
                        .position(latLng)
                        .title(placeInfo.getName().toString())
                        .snippet(snippet);

                marker =   mMap.addMarker(options);


            }else{

                mMap.addMarker(new MarkerOptions().position(latLng));
            }

        }catch (NullPointerException e){

            Log.d(TAG, "mooveCamera:NullPointerException : "+ e.getMessage());

        }

    }


    private void verifyServices() {



        boolean serviceOk = mapsServices.isServicesOk(MyPositionActivity.this);

        Toast.makeText(this, "services "+serviceOk, Toast.LENGTH_SHORT).show();

        if (serviceOk) {

            Toast.makeText(this, "Services are OK !!!", Toast.LENGTH_SHORT).show();

            // get the location divice

            getDeviceLocation();

            //getPostLocation(latitude,longitude );

        }

    }


    private void initmMap() {

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById( R.id.my_map);
        mapFragment.getMapAsync(  this );
        Toast.makeText(this, "map try to initialise it ", Toast.LENGTH_SHORT).show();


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
            mMap.getUiSettings().setMyLocationButtonEnabled(false);

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



    @Override
    public void onBackPressed() {
        startActivity( new Intent( MyPositionActivity.this , MainActivity.class) );
    }
}
