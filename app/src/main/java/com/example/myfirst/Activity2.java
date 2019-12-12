package com.example.myfirst;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;


import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myfirst.android.app.Accelerometer;
import com.example.myfirst.android.app.DataBaseHelper;

import java.io.Serializable;

public class Activity2 extends AppCompatActivity implements Serializable {

    Button clear;
    Boolean verticalModeOn, devModeOn;
    ToggleButton orientation, devmode;
    DataBaseHelper dataBaseHelper;
    TextView treshText;
    Accelerometer accelerometer;
    Float thresh;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_2);
        /* add event listener for enter button in threshold setting */
        Bundle extras = getIntent().getExtras();
        Button enter = findViewById(R.id.enter);
        treshText = findViewById(R.id.thresh_text);
        if(extras !=  null) {
        treshText.setText("current: " + extras.getFloat("accelerometerValue"));
        Intent intent = this.getIntent();
        accelerometer = (Accelerometer)intent.getExtras().getSerializable("accelerometer");
        }

        clear = findViewById(R.id.clearButton);
        orientation = findViewById(R.id.orientation_button);
        devmode = findViewById(R.id.devmode_button);



        dataBaseHelper = new DataBaseHelper(this);
        final EditText userInput = (EditText) findViewById(R.id.thresh_input);
        enter.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // update the threshold
                SharedPreferences sharedPreferences = getSharedPreferences("appSettings", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                String thresholdStr = userInput.getText().toString();
                Float threshold = Float.valueOf(thresholdStr);
                editor.putFloat("thresh", threshold);
                editor.apply();
                String thresh = Float.toString(threshold);
                treshText.setText(thresh);
            }
        });

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearData();
            }
        });


        orientation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    verticalModeOn = true;
                    saveSettings(findViewById(android.R.id.content),false);
                }
                else {
                    verticalModeOn = false;
                    saveSettings(findViewById(android.R.id.content),false);
                }
            }
        });

        devmode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    devModeOn = true;
                    saveSettings(findViewById(android.R.id.content),true);
                }
                else {
                    devModeOn = false;
                    saveSettings(findViewById(android.R.id.content),true);
                }
            }
        });
        getprefences(findViewById(android.R.id.content));
        updateButtons();




    }

    // Wipe the pothole database
    public void clearData() {

        try {
            dataBaseHelper.clearData(); }
        catch(Exception R) {
            R.printStackTrace();
        }
    }


    public void saveSettings(View view, boolean devmodePressed) {
        SharedPreferences sharedPreferences = getSharedPreferences("appSettings", Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();


        if(devmodePressed) {
        editor.putBoolean("devmode", devModeOn); }
        else {
        editor.putBoolean("verticalmode", verticalModeOn); }

        editor.apply();





    }

    public void getprefences(View view) {
        SharedPreferences sharedPreferences = getSharedPreferences("appSettings", Context.MODE_PRIVATE);

        verticalModeOn = sharedPreferences.getBoolean("verticalmode", false);
        devModeOn = sharedPreferences.getBoolean("devmode", false);
        thresh = sharedPreferences.getFloat("thresh", 18);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if ((keyCode == KeyEvent.KEYCODE_BACK))
        {
            finishActivity(1);
        }
        return super.onKeyDown(keyCode, event);
    }

   public void updateButtons() {
       if(devModeOn) {
           devmode.setChecked(true);
           devmode.setText("On"); }
       else {
           devmode.setChecked(false);
           devmode.setText("Off");
       }

       if(verticalModeOn) {
           orientation.setChecked(true);
           orientation.setText("vertical");
       }
       else {
           orientation.setChecked(false);
           orientation.setText("horizontal");
       }



   }
}
