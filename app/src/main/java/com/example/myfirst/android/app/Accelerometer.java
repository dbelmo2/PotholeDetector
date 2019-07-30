package com.example.myfirst.android.app;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class Accelerometer extends AppCompatActivity implements SensorEventListener {
    private static final String TAG = "MainActivity";
    private float previousZ = 0.0f;
    private SensorManager sensoryManager;
    Sensor accelerometer;
    private boolean potholeHit = false;
    Context context;
    private boolean potHoleFound = false;
    private float thresh;


    public Accelerometer(Context context) {

        this.context = context;
        Log.d(TAG, "onCreate: Initializing Sensor Services");
        this.sensoryManager = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);
        this. accelerometer = sensoryManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensoryManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        Log.d(TAG, "onCreate: Registered accelerometer listener");
        thresh = 100.0F;
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        float zAxis = sensorEvent.values[2];

       // Log.d(TAG, "previous Z " + previousZ );
        //Log.d(TAG, "new Z " + zAxis);

        float difference = Math.abs(previousZ - zAxis);


        // function call to compare two float values
        if (Float.compare(difference, thresh) >= 0) {

            System.out.println("pothole hit");
            potholeHit = true;
        }
        else if (Float.compare(difference, thresh) < 0) {

            //System.out.println("Just a bump");
            potholeHit = false;
        }


        previousZ = zAxis;
    }


    public boolean potHoleFound() {
        return potholeHit;
    }
    public void setPotholeFound(boolean found) {potholeHit = found;}
    public void reset() {
        potholeHit = false;
    }

    public void setTresh(float thresh) {
        this.thresh = (float) thresh;
    }

}