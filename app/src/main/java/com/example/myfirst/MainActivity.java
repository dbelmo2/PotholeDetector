package com.example.myfirst;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.widget.TextView;

import java.util.Random;


public class MainActivity extends AppCompatActivity {

    private LocationListener locationListener;
    private Button getLocation;
    private TextView resultsText;
    private SeekBar seekBar;
    private LocationManager locationManager;
    private GPS gps;


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gps = new GPS(this, this, resultsText);
        gps.gpsSstup();



        // get the button from the activity view, using the ID: rollButton
        getLocation = findViewById(R.id.getLocation);
        resultsText = findViewById(R.id.results);
        configureButton();

        // setting up the location manager


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        gps.onResults(requestCode, permissions, grantResults);
    }

    private void configureButton() {
        getLocation.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                gps.requestLoc();
                resultsText.append("\n" + gps.getLatitude() + " " + gps.getLongitude());
            }
        });

    }
}
