package com.example.myfirst;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.QuickContactBadge;
import android.widget.SeekBar;
import android.widget.TextView;
import android.view.Menu;

import com.example.myfirst.android.app.Accelerometer;
import com.example.myfirst.android.app.Coordinate;
import com.example.myfirst.android.app.DataBaseHelper;
import com.example.myfirst.android.app.GPS;
import com.example.myfirst.android.app.Search;

import java.io.Serializable;

public class Activity2 extends AppCompatActivity implements Serializable {

    Button clear;
    DataBaseHelper dataBaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_2);

        /* add event listener for enter button in threshold setting */

        Button enter = (Button) findViewById(R.id.enter);
        Button clear = findViewById(R.id.clearButton);

        dataBaseHelper = new DataBaseHelper(this);
        final EditText userInput = (EditText) findViewById(R.id.userInput);
        enter.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                String thresholdStr = userInput.getText().toString();
                Float threshold = Float.valueOf(thresholdStr);
                Intent intent = new Intent(Activity2.this, MainActivity.class);
                intent.putExtra("newThreshold", threshold);
                startActivity(intent);
            }

        });

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearData();
            }
        });



    }

    public void clearData() {

        try {
            dataBaseHelper.clearData(); }
        catch(Exception R) {
            R.printStackTrace();
        }
    }
}
