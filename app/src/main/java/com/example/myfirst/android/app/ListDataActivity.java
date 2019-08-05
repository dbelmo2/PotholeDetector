package com.example.myfirst.android.app;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.BundleCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.myfirst.R;

import java.util.ArrayList;

public class ListDataActivity extends AppCompatActivity implements CallBack{

    private static final String TAG = "ListDataAcivity";
    private Coordinate coordinate;
    DataBaseHelper dataBaseHelper;
    private ListView listView;
    PopHandler popHandler;
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
        final ArrayList<Coordinate> coordinateList = new ArrayList<>();
        if(numRows == 0) {
            Toast.makeText(ListDataActivity.this, "Database is empty!", Toast.LENGTH_LONG).show();
        } else {

            while(cursor.moveToNext()) {
                coordinate = new Coordinate(cursor.getInt(0),cursor.getDouble(1),cursor.getDouble(2));
                coordinateList.add(coordinate);
            }
        }

        final Three_column_adapter listAdapter = new Three_column_adapter(this, R.layout.list_adapter_view, coordinateList);
        listView.setAdapter(listAdapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Coordinate coordinate1 =  (Coordinate) parent.getItemAtPosition(position);

                popHandler = new PopHandler(ListDataActivity.this, view, coordinate1.getId(),ListDataActivity.this);
                popHandler.showPopup();

                Toast.makeText(ListDataActivity.this, "You clicked on: " + coordinate1.getId(), Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    public void methodToCall() {
        fillList(dataBaseHelper.getAllData());
    }
}
