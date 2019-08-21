package com.example.myfirst.android.app;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import android.util.Log;

import java.io.Serializable;

import static android.content.ContentValues.TAG;


public class GPS implements Serializable {

    private  LocationListener locationListener;
    private  LocationManager locationManager;
    Context context;
    Activity activity;
    private double latitude, longitude;
    private Location currentLocation;

    public GPS(Context con, Activity activity) {
        this.context = con;
        this.activity = activity;


    }
    public void gpsSetup() {
        locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();

                currentLocation = location;
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                context.startActivity(intent);

            }
        };
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions( activity,new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.INTERNET

            },10 );
            return;
        }else {
            requestLoc();
        }
    }


    public void onResults(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode) {
            case 10:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    requestLoc();
                }
        }

    }

    // updates the latitude and longitude values inside the GPS.java class
    public void requestLoc() {
        try {
            locationManager.requestLocationUpdates("gps", 5000, 5, locationListener);
        }
        catch(SecurityException e) {
            Log.d(TAG, "requestLoc: " + e);
        }
    }

    public double getLatitude() {
        return latitude;

    }
    public double getLongitude() {
        return longitude;
    }

    public Location getCurrentLocation() { return currentLocation; }
}

