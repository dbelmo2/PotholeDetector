package com.example.myfirst.android.app;

public class Coordinate {

    public int id;
    public double latitude;
    public double longitude;



    public Coordinate(double latitude, double longitude) {
        this.id = 0;
        this.latitude = latitude;
        this.longitude = longitude;
    }
    public Coordinate(int id, double latitude, double longitude) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
    }
    public Coordinate(Coordinate c) {
        this.id = c.id;
        this.longitude = c.longitude;
        this.latitude = c.latitude;
    }

    public int getId() {
        return this.id;
    }
}
