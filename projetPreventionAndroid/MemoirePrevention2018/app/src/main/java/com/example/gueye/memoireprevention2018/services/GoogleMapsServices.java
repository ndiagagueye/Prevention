package com.example.gueye.memoireprevention2018.services;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.example.gueye.memoireprevention2018.modele.PlaceInfo;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;

import static com.example.gueye.memoireprevention2018.utils.Const.COARSE_LOCATION;
import static com.example.gueye.memoireprevention2018.utils.Const.FINE_LOCATION;
import static com.example.gueye.memoireprevention2018.utils.Const.PERMISSIONS;

public class GoogleMapsServices implements OnMapReadyCallback {

    public static final int ERROR_DIALOG_REQUEST = 9001;

    public boolean isPermissionsGranted = false;
    public GoogleMap mMap;
   // private PlaceAutocompleteAdapter mPlaceAutocompleteAdapter;
    public GoogleApiClient mGoogleApiClient;
    private Marker marker;
 //   private PlaceInfo mplace ;
    private GeoDataClient mGeoDataClient;

   // private PlaceInfo placeInfo;

    public String TAG =null ;
    public Context context;
    public GoogleMap googleMap;

    Location currentLocation;


    public static final int LOCATION_PERMISSION_REQUEST_CODE = 124;
    public static final float DEFAULT_ZOOM = 15f;




    public GoogleMapsServices(Context context , String TAG ){

        this.context = context;

       this.TAG =TAG;
    }

    public GoogleMapsServices(Context context , String TAG, GoogleMap googleMap ){

        this.context = context;
        this.googleMap = googleMap;

        this.TAG =TAG;
    }




    public Task getDeviceLocation(FusedLocationProviderClient mFusedLocationProviderClient){

        Task location = null;
        Log.d(TAG, "getDeviceLocation: getting devices location");

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);

        try{

            Log.d(TAG, "getDeviceLocation: permission is granted , we try now to get the current location");

            location    = mFusedLocationProviderClient.getLastLocation();

        }catch (SecurityException e ){

            Log.d(TAG, "getDeviceLocation: SecurityException : "+ e.getMessage());
        }

        return  location;
    }

    public boolean getLocationPermission(Context context){
        Log.d(TAG, "getLocationPermission: getting location and permissions ");



        if(ContextCompat.checkSelfPermission(context, FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){

            if(ContextCompat.checkSelfPermission(context,COARSE_LOCATION)== PackageManager.PERMISSION_GRANTED){

                return true;

            }

        }else{

           askPermissions();

        }

        return false;
    }

    public void askPermissions() {

        ActivityCompat.requestPermissions((Activity) context,PERMISSIONS,LOCATION_PERMISSION_REQUEST_CODE);
    }


    public void mooveCamera(LatLng latLng , float zoom , PlaceInfo placeInfo){

        Log.d(TAG, "mooveCamera:  mooving the camera to latitude : "+ latLng.latitude +" and longitude "+latLng.longitude);

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,zoom));

        googleMap.clear();

        try{

            if (placeInfo != null) {

                String snippet = "Adress "+ placeInfo.getAddress() + "\n "+
                        "Phone number  "+ placeInfo.getPhoneNumber() + "\n "+
                        "Website  "+ placeInfo.getWebsiteUrl() + "\n "+
                        "Place rating  "+ placeInfo.getRating() + "\n ";

//                MarkerOptions options = new MarkerOptions()
//                        .position(currentLatLng)
//                        .title(placeInfo.getName().toString())
//                        .snippet(snippet);

//                marker =   googleMap.addMarker(options);


            }else{

                googleMap.addMarker(new MarkerOptions().position(latLng));
            }

        }catch (NullPointerException e){

            Log.d(TAG, "mooveCamera:NullPointerException : "+ e.getMessage());

        }

    }

    public void mooveCamera(LatLng latLng , float zoom , String title){

        Log.d(TAG, "mooveCamera:  mooving the camera to latitude : "+ latLng.latitude +" and longitude "+latLng.longitude);

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,zoom));

        if(!title.equals("My location")){

            MarkerOptions options = new MarkerOptions()
                    .position(latLng)
                    .title(title);

            googleMap.addMarker(options);
        }

       // hideSoftKeyboard();

    }

    public void initMap(Context activity, SupportMapFragment mapFragment){

        Log.d(TAG, "initMap: initialise map ");

        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap mgoogleMap) {

        Log.d(TAG, "onMapReady: loading map ");
        this.googleMap = mgoogleMap;
      //  Toast.makeText(this, "Map's is ready !!!", Toast.LENGTH_SHORT).show();

        if (isPermissionsGranted) {

            //getDeviceLocation();
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setMyLocationButtonEnabled(false);

            //calling the init() method

        }

    }

    public boolean isServicesOk(Context context){

        Log.d(TAG, "isServicesOk : checking if services is ok !!!!");
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context);

        if(available == ConnectionResult.SUCCESS){
            Log.d(TAG, "isServiceOk : services are ok , we can do the map request ");

            return true;
        }else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){

            Log.d(TAG,"isServicesOk : an error occurred but we can fixed it !!!");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog((Activity) context,available,ERROR_DIALOG_REQUEST);
            dialog.show();
        }else{

            Toast.makeText(context, "We can't make map resuest ", Toast.LENGTH_SHORT).show();


        }

        return  false;
    }
}
