package com.example.myfirst.android.app;

import android.database.Cursor;

import com.example.myfirst.android.app.DataBaseHelper;

import java.util.ArrayList;

public class ProactiveSearch extends Thread {
    DataBaseHelper database;
    ArrayList<Double> myLat, myLong;

    ProactiveSearch(DataBaseHelper database) {
        this.database = database;

    }


    public void run () {

        try{

           Cursor dataCursor =  database.getAllData();




        } catch (Exception A) {
            A.printStackTrace();
        }


    }



    public void populateArrays(Cursor res) {
        while(res.moveToNext()) {
            myLat.add(res.getDouble(1));
            myLong.add(res.getDouble(2));
        }
    }

    public boolean checkForMatch(ArrayList<Double> myLat, ArrayList<Double> myLong, double myX, double myY) {

        double min1, max1, min2, max2;

        min1 = myX - 0.0002058697;
        max1 = myX + 0.00020586297;

        min2 = myY - 0.0002058697;
        max2 = myY - 0.0002058697;

        int i=0;
        while(i < myLat.size()) {
            if(myLat.get(i) < max1 && myLat.get(i) > min1) {
                if (myLong.get(i) < max2 && myLong.get(i) > min2) {
                    return true;
                }
            }
        }

        return false;

    }


}
