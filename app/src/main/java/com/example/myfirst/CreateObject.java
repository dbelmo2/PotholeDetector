package com.example.myfirst;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CreateObject extends AppCompatActivity {
    private EditText messageBox;
    private CalendarView itemDate;
    private Button create_button;
    private Switch isAvailable;
    private Date formatterDate;
    private Spinner stop, incident;
    private String stopLocation, typeIncident;


    private void addStop(){
        stop = (Spinner) findViewById(R.id.stop);
        stop.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                stopLocation = String.valueOf(stop.getItemAtPosition(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                stopLocation = null;
            }
        });
    }

    private void addIncident(){
        incident = (Spinner) findViewById(R.id.messageType);
        incident.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                typeIncident = String.valueOf(incident.getItemAtPosition(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                typeIncident = null;
            }
        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_object);
        addStop();
        addIncident();
        messageBox = (EditText) findViewById(R.id.edtItem);
        itemDate = (CalendarView) findViewById(R.id.calendarView);
        create_button = findViewById(R.id.btnCreate);
        isAvailable = (Switch) findViewById(R.id.swiAvailable);
        // Get Date from CalendarView
        itemDate.setOnDateChangeListener( new CalendarView.OnDateChangeListener() {
            public void onSelectedDayChange(CalendarView calendarView, int year, int month, int dayOfMonth) {
                String getDate = (dayOfMonth + "/" + (month+1) + "/" + year);
                formatterDate = convertStringToData(getDate);
            }
        });

        create_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Validating the log in data
                boolean validationError = false;

                StringBuilder validationErrorMessage = new StringBuilder("Please, ");
                if (isEmptyText(messageBox)) {
                    validationError = true;
                    validationErrorMessage.append("insert an name");
                }
                if (isEmptyDate()) {
                    if (validationError) {
                        validationErrorMessage.append(" and ");
                    }
                    validationError = true;
                    validationErrorMessage.append("select a Date");
                }
                if(typeIncident == null) {
                    if (validationError) {
                        validationErrorMessage.append(" and ");
                    }
                    validationErrorMessage.append("select a type of incident");
                }
                if(stopLocation == null) {
                    if (validationError) {
                        validationErrorMessage.append(" and ");
                    }
                    validationErrorMessage.append("select a stop");
                }
                validationErrorMessage.append(".");

                if (validationError) {
                    Toast.makeText(CreateObject.this, validationErrorMessage.toString(), Toast.LENGTH_LONG).show();
                    return;
                } else {
                    saveObject();
                }
            }
        });
    }

    public static Date convertStringToData(String getDate){
        Date today = null;
        SimpleDateFormat simpleDate = new SimpleDateFormat("dd/MM/yyyy");

        try {
            today = simpleDate.parse(getDate);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        return today;
    }
    private boolean isEmptyText(EditText text) {
        if (text.getText().toString().trim().length() > 0) {
            return false;
        } else {
            return true;
        }
    }
    private boolean isEmptyDate() {
        if (String.valueOf(formatterDate) != "null") {
            return false;
        } else {
            return true;
        }
    }
    public void saveObject(){
        // Configure Query
        ParseObject message = new ParseObject("Message");

        // Store an object
        message.put("Message", messageBox.getText().toString());
        message.put("User", ParseUser.getCurrentUser());
        message.put("Stop", stopLocation);
        message.put("Incident", typeIncident);
        // Saving object
        message.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Intent intent = new Intent(CreateObject.this, ReadObjects.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(
                            getApplicationContext(),
                            e.getMessage().toString(),
                            Toast.LENGTH_LONG
                    ).show();
                }
            }
        });
    }

}