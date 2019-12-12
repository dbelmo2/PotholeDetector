package com.example.myfirst;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;


import android.text.Html;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import android.widget.EditText;
import android.widget.ImageView;
import android.widget.QuickContactBadge;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.view.Menu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myfirst.android.app.Accelerometer;
import com.example.myfirst.android.app.Coordinate;
import com.example.myfirst.android.app.DataBaseHelper;
import com.example.myfirst.android.app.GPS;
import com.example.myfirst.android.app.ListDataActivity;
import com.example.myfirst.android.app.Search;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;


import java.io.Serializable;



import pl.bclogic.pulsator4droid.library.PulsatorLayout;


public class MainActivity extends AppCompatActivity implements Serializable {


    private LocationListener locationListener;
    private Button viewData, start, addCoordinate, settings, mapButton;
    private TextView timerView;
    private Switch orientationSwitch;
    private GPS gps;
    private Accelerometer accelerometer;
    private DataBaseHelper database;
    private Search search;
    private MediaPlayer mediaPlayer, timerSoundPlayer;
    private EditText latInput, longInput;
    private PulsatorLayout pulsatorLayout;
    private CountDownTimer countDownTimer;
    private Location mLocation;
    private Float thresh;
    private FusedLocationProviderClient fusedProvider;
    private Boolean devModeOn,verticalModeOn, timerRunning;



    private long timeLeft = 3000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        getprefences(findViewById(android.R.id.content));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ImageView mainBackground = (ImageView)findViewById(R.id.image);
        gps = new GPS(this, this);
        gps.gpsSetup();
        database = new DataBaseHelper(this);
        accelerometer = new Accelerometer(this, verticalModeOn);
        mediaPlayer = MediaPlayer.create(this, R.raw.alert);
        orientationSwitch = new Switch(this);

        search = new Search(database, gps, accelerometer, mediaPlayer,this);
        pulsatorLayout = (PulsatorLayout)findViewById(R.id.pulsator);
        timerSoundPlayer = MediaPlayer.create(this,R.raw.timer_tone);



        // get the button from the activity view, using the ID: rollButton

        start = findViewById(R.id.startSearch);
        viewData = findViewById(R.id.viewData);
        latInput = findViewById(R.id.lat_input);
        longInput = findViewById(R.id.long_input);
        addCoordinate = findViewById(R.id.add_coordinate);
        settings =  findViewById(R.id.SettingsButton);
        timerView = findViewById(R.id.timer_view);
        timerView.setVisibility(View.INVISIBLE);
        mapButton = findViewById(R.id.MapButton);
        /* Adding event listener for SETTING button */

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSettingsActivity();
            }
        });
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start.setEnabled(false);
                startTimer();
                timerView.setVisibility(View.VISIBLE);

            }
        });

        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMapActivity();
            }
        });

        addCoordinate.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View V) {
                double data1 = Double.valueOf(latInput.getText().toString());
                double data2 = Double.valueOf(longInput.getText().toString());
                Coordinate coordinate = new Coordinate(data1,data2);
                search.addToVector(coordinate);
                database.addCoordinate(coordinate);
            }
        });

        // setup the background image
        //mainBackground.setImageResource(R.drawable.app2);
        // setting up the location manager
        viewAll();


        accelerometer.setTresh(thresh);
        //update dev gui and accelerometer setting based on shared pref
        updateDevUI();
    }

    /* Method that calls the settings menu screen when button is clicked on */

    public void openSettingsActivity() {
        Intent intent = new Intent(this, Activity2.class);
        //Bundle bundle = new Bundle();
        //bundle.putSerializable("accelerometer",(Serializable)accelerometer);
       // intent.putExtras();
        startActivityForResult(intent,1);
    }

    // Method that starts the map activity
    public void openMapActivity() {
        Intent intent = new Intent(this, PotholeMap.class);
        startActivity(intent);
    }



    // function to start timer
    public void startTimer(){
        countDownTimer = new CountDownTimer(timeLeft,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeft = millisUntilFinished;
                updateTimer();
                timerSoundPlayer.start();
            }

            @Override
            public void onFinish() {
                pulsatorLayout.start();
                timerRunning = false;
                timerView.setVisibility(View.INVISIBLE);
                search.start();
            }
        }.start();
        timerRunning = true;
    }

    //method to update the timer view
    public void updateTimer() {
        int seconds = (int) timeLeft % 60000 / 1000;

        String timeLeftText;

        timeLeftText = "" + seconds;
        timerView.setText(timeLeftText);

    }

    // method which setups the view button
    public void viewAll() {
        viewData.setOnClickListener(
                new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                       Intent intent = new Intent(MainActivity.this, ListDataActivity.class);
                       startActivity(intent);
                    }
                }
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        gps.onResults(requestCode, permissions, grantResults);
    }

    public void getprefences(View view) {
        SharedPreferences sharedPreferences = getSharedPreferences("appSettings", Context.MODE_PRIVATE);

        verticalModeOn = sharedPreferences.getBoolean("verticalmode", false);
        devModeOn = sharedPreferences.getBoolean("devmode", false);
        thresh = sharedPreferences.getFloat("thresh", 18);

    }
    public void updateDevUI() {
        accelerometer.setOrientation(verticalModeOn);
        if(!devModeOn) {
            longInput.setVisibility(View.INVISIBLE);
            latInput.setVisibility(View.INVISIBLE);
            addCoordinate.setEnabled(false);
            addCoordinate.setVisibility(View.INVISIBLE);
        }
        else {
            longInput.setVisibility(View.VISIBLE);
            latInput.setVisibility(View.VISIBLE);
            addCoordinate.setEnabled(true);
            addCoordinate.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            // Make sure the request was successful
            if (resultCode == RESULT_CANCELED) {
                // The user picked a contact.
                // The Intent's data Uri identifies which contact was selected.
                // Do something with the contact here (bigger example below)
                getprefences(findViewById(android.R.id.content));
                updateDevUI();
            }
        }
    }




}
