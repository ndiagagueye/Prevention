package com.example.gueye.memoireprevention2018.modele;

import android.net.Uri;

import com.google.android.gms.maps.model.LatLng;

public class PlaceInfo {

    private String name;
    private String address;
    private String id;
    private LatLng latLng;
    private String attributions;
    private String phoneNumber;
    private Uri websiteUrl ;
    private float rating;

    public PlaceInfo(String name, String address, String id, LatLng latLng, String attributions, String phoneNumber, Uri websiteUrl, float rating) {
        this.name = name;
        this.address = address;
        this.id = id;
        this.latLng = latLng;
        this.attributions = attributions;
        this.phoneNumber = phoneNumber;
        this.websiteUrl = websiteUrl;
        this.rating = rating;
    }

    public PlaceInfo(String name, String address, String id, LatLng latLng, CharSequence attributions, CharSequence phoneNumber, Uri websiteUri, float rating){


    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public String getAttributions() {
        return attributions;
    }

    public void setAttributions(String attributions) {
        this.attributions = attributions;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNulber) {
        this.phoneNumber = phoneNulber;
    }

    public Uri getWebsiteUrl() {
        return websiteUrl;
    }

    public void setWebsiteUrl(Uri websiteUrl) {
        this.websiteUrl = websiteUrl;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }
}
