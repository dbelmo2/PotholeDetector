package com.example.myfirst.android.app;

import android.content.Context;

import android.media.MediaPlayer;

import com.example.myfirst.android.app.DataBaseHelper;
import com.example.myfirst.android.app.GPS;

import java.sql.SQLOutput;

public class Search extends Thread {
    private double pLat, pLong;
    private DataBaseHelper database;
    GPS gps;
    Accelerometer accelerometer;
    private boolean potHoleFound = false;
    MediaPlayer mediaPlayer;

    // Geofencing variables




    public Search(DataBaseHelper dataBase, GPS gps, Accelerometer accelerometer, MediaPlayer mediaPlayer) {
        this.database = dataBase;
        this.gps = gps;
        this.accelerometer = accelerometer;
        this.mediaPlayer = mediaPlayer;

    }

    /*main Search thread, continuously checks if
    a pothole has been hit */
    public void run() {

        try {
            while (true) {
                gps.requestLoc();
                Coordinate current = new Coordinate(gps.getLatitude(), gps.getLongitude());

                System.out.println("Lat: " + gps.getLatitude() + "  Long: " + gps.getLongitude());
                if(database.checkCoordinate(current)) {
                    mediaPlayer.start();
                }

                if(accelerometer.potHoleFound()) {
                    gps.requestLoc();
                    pLat = gps.getLatitude();
                    pLong = gps.getLongitude();
                    recordLocation(pLat, pLong);
                    accelerometer.reset();
                    wait(5000);
                }
            }
        }
        catch(Exception A) {
            A.printStackTrace();
        }
    }

    // function which returns true if both coordinates were successfully recorded
    private void recordLocation(double lat, double lon) {
        Coordinate coordinate = new Coordinate(lat,lon);
        database.addCoordinate(coordinate);
    }

    public void setPotHoleFound(boolean state) {
        this.potHoleFound = state;
    }





}
