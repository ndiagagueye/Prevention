package com.example.gueye.memoireprevention2018.utils;

import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ParserTask extends AsyncTask<String, Integer, List<List<HashMap>>> {

    GoogleMap mMap;

    public static final String TAG = "ParserTask ";

    public ParserTask(GoogleMap mMap){

        this.mMap = mMap;
    }

    @Override
    protected List<List<HashMap>> doInBackground(String... jsonData) {

        JSONObject jObject;
        List<List<HashMap>> routes = null;

        try {
            jObject = new JSONObject(jsonData[0]);
            DirectionsJSONParser parser = new DirectionsJSONParser();

            routes = parser.parse(jObject);

            Log.d(TAG, "doInBackground: routes "+ routes );
        } catch (Exception e) {
            e.printStackTrace();
        }
        return routes;
    }


    @Override
    protected void onPostExecute(List<List<HashMap>> result) {
        ArrayList points = null;
        PolylineOptions lineOptions = null;
        MarkerOptions markerOptions = new MarkerOptions();

        for (int i = 0; i < result.size(); i++) {
            points = new ArrayList();
            lineOptions = new PolylineOptions();

            List<HashMap> path = result.get(i);

            for (int j = 0; j < path.size(); j++) {
                HashMap point = path.get(j);

                double lat = Double.parseDouble((String) point.get("lat"));
                double lng = Double.parseDouble((String) point.get("lng"));
                LatLng position = new LatLng(lat, lng);

                points.add(position);

                Log.d(TAG, "onPostExecute:  positions is "+ position);
            }

            lineOptions.addAll(points);
            lineOptions.width(5);
            lineOptions.color(Color.RED);
            lineOptions.geodesic(true);

        }

// Drawing polyline in the Google Map for the i-th route

        if (lineOptions != null)
        mMap.addPolyline(lineOptions);

        else{

            Log.d(TAG, "onPostExecute: on a pas pu tracer la route ");
        }
    }


}
