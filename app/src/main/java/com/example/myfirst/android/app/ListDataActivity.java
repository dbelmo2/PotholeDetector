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

        dataBaseHelper = new DataBaseHelper(this);
        fillList(dataBaseHelper.getAllData());



    }

    private void fillList(Cursor mCursor) {

        Cursor cursor = mCursor;
        int numRows = cursor.getCount();
        ArrayList<Coordinate> coordinateList = new ArrayList<>();
        if(numRows == 0) {
            Toast.makeText(ListDataActivity.this, "Database is empty!", Toast.LENGTH_LONG).show();
        } else {

            while(cursor.moveToNext()) {
                coordinate = new Coordinate(cursor.getInt(0),cursor.getDouble(1),cursor.getDouble(2));
                coordinateList.add(coordinate);
            }
        }

        Three_column_adapter listAdapter = new Three_column_adapter(this, R.layout.list_adapter_view, coordinateList);
        listView.setAdapter(listAdapter);


    }
}
