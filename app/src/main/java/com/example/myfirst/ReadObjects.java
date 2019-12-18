package com.example.myfirst;

import android.os.Bundle;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReadObjects extends AppCompatActivity {

    public ArrayList<String> dataList = new ArrayList<String>();
    public String[] myArray = {};
    private EditText filter;
    private String filterType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_objects);
        filter = (EditText) findViewById(R.id.filterType);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ReadObjects.this, CreateObject.class);
                startActivity(intent);
            }
        });

        findObjects();
    }

    private void findObjects(){
        myArray = new String[]{};
        final ListView listView = (ListView) findViewById(R.id.listviewA);

        // Configure Query
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Message");

        // Query Parameters
        if(checkIncident()) {
            query.whereEqualTo("Incident", filter.getText());
            ;        }
        else if(checkStop()) {
            query.whereEqualTo("Stop", filter.getText());
        }
        else {
            //do nothing
        }

        // Sorts the results in ascending order by the Date field
        query.orderByAscending("Date");

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, final ParseException e) {
                if (e == null){
                    // Adding objects into the Array
                    for(int i= 0 ; i < objects.size(); i++){
                        String element = objects.get(i).getString("Message");
                        dataList.add(element.toString());
                    }
                } else {

                }
                myArray = dataList.toArray(new String[dataList.size()]);

                final ArrayList<String> list  = new ArrayList<String>(Arrays.asList(myArray));

                ArrayAdapter<String> adapterList
                        = new ArrayAdapter<String>(ReadObjects.this, android.R.layout.simple_list_item_single_choice, myArray);

                listView.setAdapter(adapterList);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(final AdapterView<?> adapter, View v, final int position,
                                            long id) {

                        final String value = (String) adapter.getItemAtPosition(position);

                        //Alert showing the options related with the object (Update or Delete)
                        AlertDialog.Builder builder = new AlertDialog.Builder(ReadObjects.this)
                                .setTitle(value + " movie" )
                                .setMessage("What do you want to do?")
                                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dataList.remove(position);
                                        deleteObject(value);
                                        myArray = dataList.toArray(new String[dataList.size()]);

                                        ArrayAdapter<String> adapterList
                                                = new ArrayAdapter<String>(ReadObjects.this, android.R.layout.simple_list_item_single_choice, myArray);

                                        listView.setAdapter(adapterList);
                                    }
                                })
                                .setNeutralButton("Update",  new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent(ReadObjects.this, UpdateObject.class);
                                        //Send string value to UpdateObject Activity with putExtra Method
                                        intent.putExtra("objectName", value.toString());
                                        startActivity(intent);
                                    }
                                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });
                        AlertDialog ok = builder.create();
                        ok.show();
                    }
                });
            }
        });
    }

    private boolean checkStop() {
        switch(filterType) {
            case "Forest Park":
            case "Harlem":
            case "Oak Park":
            case "Austin":
            case "Cicero":
            case "Pulaski":
            case "Kedzie-Homan":
            case "Western":
            case "Illinois Medical District":
            case "Racine":
            case "UIC-Halsted":
            case "Clinton":
            case "LaSalle":
            case "Jackson":
                return true;
            default:
                return false;
        }
    }

    private boolean checkIncident() {
        switch(filterType) {
            case "Police Presence":
            case "Police Checkpoint":
            case "Crime":
            case "Other":
                return true;
            default:
                return false;
        }
    }

    // Delete object
    private void deleteObject(final String value) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("reminderList");

        // Query parameters based on the item name
        query.whereEqualTo("itemName", value.toString());
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(final List<ParseObject> object, ParseException e) {
                if (e == null) {
                    //Delete based on the position
                    object.get(0).deleteInBackground(new DeleteCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {

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
            };
        });
    }
}