package com.example.myfirst.android.app;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;

import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;

import java.io.Serializable;

public class Accelerometer extends AppCompatActivity implements SensorEventListener, Serializable {
    private static final String TAG = "MainActivity";
    private float previousZ = 0.0f;
    private SensorManager sensoryManager;
    Sensor accelerometer;
    private boolean potholeHit = false;
    Context context;
    private boolean potHoleFound = false;
    private float thresh;
    private boolean verticalModeOn = false;


    public Accelerometer(Context context, Boolean verticalModeOn) {
        this.context = context;
        Log.d(TAG, "onCreate: Initializing Sensor Services");
        this.sensoryManager = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);
        this.accelerometer = sensoryManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensoryManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        Log.d(TAG, "onCreate: Registered accelerometer listener");
        thresh = 17.0F;
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        float axisValue;
        if(!verticalModeOn) {
             axisValue = sensorEvent.values[2]; }
        else {
            axisValue = sensorEvent.values[1];
        }

       // Log.d(TAG, "previous Z " + previousZ );
        //Log.d(TAG, "new Z " + zAxis);

        float difference = Math.abs(previousZ - axisValue);


        // function call to compare two float values
        if (Float.compare(difference, thresh) >= 0) {

            System.out.println("pothole hit");
            potholeHit = true;
        }
        else if (Float.compare(difference, thresh) < 0) {

            //System.out.println("Just a bump");
            potholeHit = false;
        }
        previousZ = axisValue;
    }


    public void setOrientation(boolean verticalModeOn ) {

        this.verticalModeOn = verticalModeOn;
    }
    public boolean potHoleFound() {
        return potholeHit;
    }
    public void setPotholeFound(boolean found) {potholeHit = found;}
    public void reset() {
        potholeHit = false;
    }

    public void setTresh(float thresh) {
        this.thresh =  thresh;
    }
    public float getThresh() { return thresh; }
}