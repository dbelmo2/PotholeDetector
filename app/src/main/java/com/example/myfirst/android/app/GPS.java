package com.example.myfirst.android.app;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.Serializable;
import java.util.concurrent.Executor;

import static androidx.constraintlayout.widget.Constraints.TAG;


public class GPS {

    Context context;
    Activity activity;
    private double latitude, longitude;
    private FusedLocationProviderClient fusedProvider;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;

    public GPS(Context con, Activity activity) {
        this.context = con;
        this.activity = activity;
        this.fusedProvider = LocationServices.getFusedLocationProviderClient(context);

    }
    public void gpsSetup() {

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions( activity,new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.INTERNET

            },10 );
            return;
        }else {
            buildLocationCallback();
            buildLocationRequest();


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
        //locationManager.requestLocationUpdates("gps", 5000, 5, locationListener);
        try {
            fusedProvider.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        } catch(SecurityException e) {
            System.out.println(e);

        }

    }

    public synchronized double getLatitude() {
        return latitude;

    }
    public synchronized double getLongitude() {
        return longitude;
    }



        @SuppressLint("RestrictedApi")
        public void buildLocationRequest() {
            locationRequest = new LocationRequest();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(5000);
            locationRequest.setFastestInterval(3000);
            locationRequest.setSmallestDisplacement(10);
        }

        private void buildLocationCallback() {
            locationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    for (Location location : locationResult.getLocations()) {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                    }
                }

            };
        }
}

