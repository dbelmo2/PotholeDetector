package com.example.myfirst;


import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.text.SimpleDateFormat;
import java.util.Date;

public class UpdateObject extends AppCompatActivity {
    private EditText itemName;
    private EditText itemAdd;
    private Switch itemAvailable;
    private CalendarView itemCalendary;
    private Date formatterDate;
    private String getObjectId;

    private Button create_button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_object);
        create_button = (Button) findViewById(R.id.btnCreate);
        itemName = (EditText) findViewById(R.id.edtItem);
        itemAdd = (EditText) findViewById(R.id.edtAdditionalInformation);
        itemAvailable = (Switch) findViewById(R.id.swiAvailable);
        itemCalendary = (CalendarView) findViewById(R.id.calendarView);

        // Defining event to Calendar
        itemCalendary.setOnDateChangeListener( new CalendarView.OnDateChangeListener() {
            public void onSelectedDayChange(CalendarView calendarView, int year, int month, int dayOfMonth) {
                String getDate = (dayOfMonth + "/" + (month+1) + "/" + year);
                formatterDate = convertStringToData(getDate);
            }
        });

        final Intent element = getIntent();

        // Recover string from other activity
        final String objectName = element.getStringExtra("objectName").toString();

        create_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveObject();
            }
        });

        // Recover the CurrentUser Id with the getObjectId method
        String currentUser = ParseUser.getCurrentUser().getObjectId();
        //
        final ParseObject obj = ParseObject.createWithoutData("_User",currentUser);

        //Configure Query
        ParseQuery<ParseObject> query = ParseQuery.getQuery("reminderList");

        // Query Parameters
        query.whereEqualTo("userId", obj);
        query.whereEqualTo("itemName", objectName);

        // How we need retrive exactly one result we can use the getFirstInBackground method
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    try {
                        itemCalendary.setDate(makeLongTODate(object.getDate("dateCommitment")), true, true);
                    } catch (java.text.ParseException e1) {
                        e1.printStackTrace();
                    }
                    // Insert the retrieve Content inside the Input at the view
                    itemName.setText(objectName);
                    itemAdd.setText(object.getString("additionalInformation"));
                    itemAvailable.setChecked(object.getBoolean("isAvailable"));
                    getObjectId = object.getObjectId().toString();
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
    public static long makeLongTODate(Date dateTime) throws java.text.ParseException {
        String d1 = null;
        try {
            d1 = new SimpleDateFormat("dd/MM/yyyy")
                    .format(new SimpleDateFormat("EEE MMM d HH:mm:ss zzz yyyy")
                            .parse(String.valueOf(dateTime)));
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }

        long result = new SimpleDateFormat("dd/MM/yyyy").parse(d1).getTime();

        return result;

    }
    public void saveObject(){
        final Editable itemNameUpdate = itemName.getText();
        final Editable itemAddUpdate = itemAdd.getText();
        final Boolean isAvailableUpdate = itemAvailable.isChecked();


        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        final String selectedDate = sdf.format(new Date(itemCalendary.getDate()));

        final Date formatterDateHere = convertStringToData(selectedDate);

        ParseQuery<ParseObject> query = ParseQuery.getQuery("reminderList");

        // Retrieve the object by id
        query.getInBackground(getObjectId, new GetCallback<ParseObject>() {
            public void done(ParseObject reminderList, ParseException e) {
                if (e == null) {
                    reminderList.put("itemName", itemNameUpdate.toString());
                    reminderList.put("additionalInformation", itemAddUpdate.toString());
                    if (formatterDate != null) {
                        reminderList.put("dateCommitment", formatterDate);
                    }
                    reminderList.put("isAvailable", isAvailableUpdate);
                    reminderList.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                Intent intent = new Intent(UpdateObject.this, ReadObjects.class);
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