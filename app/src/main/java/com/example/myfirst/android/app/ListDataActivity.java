package com.example.myfirst.android.app;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.BundleCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.myfirst.R;

import java.util.ArrayList;

public class ListDataActivity extends AppCompatActivity {

    private static final String TAG = "ListDataAcivity";
    private Coordinate coordinate;
    DataBaseHelper dataBaseHelper;
    private ListView listView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_layout);
        listView = (ListView) findViewById(R.id.listView);

        fillList();



    }

    private void fillList() {

        Cursor cursor = ;
        int numRows = cursor.getCount();
        ArrayList<String> dataList = new ArrayList<>();
        if(numRows == 0) {
            Toast.makeText(ListDataActivity.this, "Database is empty!", Toast.LENGTH_LONG).show();
        } else {

            while(cursor.moveToNext()) {
                dataList.add(cursor.getString(0));
            }
        }

        ListAdapter listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, cursor);
        listView.setAdapter(listAdapter);


    }
}
