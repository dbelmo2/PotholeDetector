package com.example.myfirst;

import android.app.AlertDialog;
import android.database.Cursor;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.QuickContactBadge;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.myfirst.android.app.Accelerometer;
import com.example.myfirst.android.app.Coordinate;
import com.example.myfirst.android.app.DataBaseHelper;
import com.example.myfirst.android.app.GPS;
import com.example.myfirst.android.app.Search;


public class MainActivity extends AppCompatActivity {

    private LocationListener locationListener;
    private Button viewData, start, updateThresh, addCoordinate;
    private TextView threshText;
    private SeekBar seekBar;
    private LocationManager locationManager;
    private GPS gps;
    private Accelerometer accelerometer;
    private boolean mLocationPermissionGranted;
    private DataBaseHelper database;
    private Search search;
    private MediaPlayer mediaPlayer;
    private EditText threshold, latInput, longInput;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ImageView mainBackground = (ImageView)findViewById(R.id.image);
        gps = new GPS(this, this);
        gps.gpsSstup();
        database = new DataBaseHelper(this);
        accelerometer = new Accelerometer(this);
        mediaPlayer = MediaPlayer.create(this, R.raw.alert);
        search = new Search(database, gps, accelerometer, mediaPlayer);



        // get the button from the activity view, using the ID: rollButton
        threshText = findViewById(R.id.threshText);
        start = findViewById(R.id.Startbutton);
        threshold = findViewById(R.id.thresh);
        viewData = findViewById(R.id.viewData);
        latInput = findViewById(R.id.lat_input);
        longInput = findViewById(R.id.long_input);
        updateThresh = findViewById(R.id.update);
        addCoordinate = findViewById(R.id.add_coordinate);



        //CLEAR DATABASE FOR TESTING PURPOSES
        clearData();


        search.start();

        // setup the background image
        //mainBackground.setImageResource(R.drawable.app2);

        // setting up the location manager


        configureButton();
        viewAll();

    }

    // method which setups the view button
    public void viewAll() {
        viewData.setOnClickListener(
                new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Cursor res = database.getAllData();

                        if(res.getCount() == 0) {
                            // show error
                            return;
                        }
                        else {
                            StringBuffer buffer = new StringBuffer();

                            while( res.moveToNext()) {
                                buffer.append("ID :" + res.getString(0) + "\n");
                                buffer.append("DATA :" + res.getDouble(1) + "\n");
                                buffer.append(("DATA2 : " + res.getDouble(2) + "\n"));
                            }

                            showMessage("dData", buffer.toString());
                        }
                    }
                }
        );
    }

    public void clearData() {

        try {
        database.clearData(); }
        catch(Exception R) {
            R.printStackTrace();
        }
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

    private void configureButton() {

        updateThresh.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                float data = Float.valueOf(threshold.getText().toString());
                accelerometer.setTresh(data);
                threshText.setText(threshold.getText());
            }



        });
        addCoordinate.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View V) {
                double data1 = Double.valueOf(latInput.getText().toString());
                double data2 = Double.valueOf(longInput.getText().toString());
                Coordinate coordinate = new Coordinate(data1,data2);
                database.addCoordinate(coordinate);
            }
        });



        start.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                clearData();

            }
        }
        );




    }



}
