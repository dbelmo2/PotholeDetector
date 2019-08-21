package com.example.myfirst;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;


import android.text.Html;
import android.util.Log;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import android.widget.EditText;
import android.widget.ImageView;
import android.widget.QuickContactBadge;
import android.widget.SeekBar;
import android.widget.TextView;
import android.view.Menu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myfirst.android.app.Accelerometer;
import com.example.myfirst.android.app.Coordinate;
import com.example.myfirst.android.app.DataBaseHelper;
import com.example.myfirst.android.app.GPS;
import com.example.myfirst.android.app.ListDataActivity;
import com.example.myfirst.android.app.Search;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;


import java.io.Serializable;



import pl.bclogic.pulsator4droid.library.PulsatorLayout;


public class MainActivity extends AppCompatActivity
        implements
        Serializable,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener
{

    private static final String TAG = MainActivity.class.getSimpleName();
    private Button viewData, start, addCoordinate, settings, mapButton;
    private TextView timerView;
    private GPS gps;
    private Accelerometer accelerometer;
    private DataBaseHelper database;
    private Search search;
    private MediaPlayer mediaPlayer, timerSoundPlayer;
    private EditText latInput, longInput;
    private PulsatorLayout pulsatorLayout;
    private CountDownTimer countDownTimer;
    private long timeLeft = 3000;
    private GoogleApiClient googleApiClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ImageView mainBackground = (ImageView)findViewById(R.id.image);
        gps = new GPS(this, this);
        gps.gpsSetup();
        database = new DataBaseHelper(this);
        accelerometer = new Accelerometer(this);
        mediaPlayer = MediaPlayer.create(this, R.raw.alert);


        search = new Search(database, gps, accelerometer, mediaPlayer);
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
        //CLEAR DATABASE FOR TESTING PURPOSES
        addCoordinate.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View V) {
                double data1 = Double.valueOf(latInput.getText().toString());
                double data2 = Double.valueOf(longInput.getText().toString());
                Coordinate coordinate = new Coordinate(data1,data2);
                database.addCoordinate(coordinate);
            }
        });

        // setup the background image
        //mainBackground.setImageResource(R.drawable.app2);
        // setting up the location manager
        viewAll();

        // Check for threshold change
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                //do nothing
            } else {
                float temp = extras.getFloat("newThreshold");
                accelerometer.setTresh(temp);

            }
        } else {
            if(savedInstanceState.getSerializable("newThreshold") != null) {
                float temp = (Float) savedInstanceState.getSerializable("newThreshold");
                accelerometer.setTresh(temp);

            }
        }

        createGoogleApi();

    }

    // Function to Create an instance of the GoogleAPIClient
    private void createGoogleApi() {
        Log.d(TAG, "createGoogleApi()");
        if(googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        else {
            return;
        }
    }

    // Abstract function #1 from GoogleApiClient to start
    @Override
    protected void onStart() {
        super.onStart();

        // Call GoogleApiClient connection when starting activity
        googleApiClient.connect();
    }

    // Abstract function #2 from GoogleApiClient that runs when stopping activity
    @Override
    protected void onStop() {
        super.onStop();

        // disconnecting
        googleApiClient.disconnect();
    }

    // Abstract function for GoogleApiClient.ConnectionCallbacks used when its connected
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(TAG, "onConnected()");
    }

    // Abstract function used when GoogleApiClient.ConnectionCallBacks is disconnected
    @Override
    public void onConnectionSuspended(int i) {
        Log.w(TAG, "onConnectionSuspended()");
    }

    //GoogleApiClient.OnConnectionFailedListener fail
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.w(TAG, "onConnection()");
    }


    /* Method that calls the settings menu screen when button is clicked on */

    public void openSettingsActivity() {
        Intent intent = new Intent(this, Activity2.class);
        intent.putExtra("accelerometerValue", accelerometer.getThresh());
        startActivity(intent);
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
                timerView.setVisibility(View.INVISIBLE);
                search.start();
            }
        }.start();
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

    //method called to display the database results in a popup window
    public void showMessage(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        gps.onResults(requestCode, permissions, grantResults);
    }
}
