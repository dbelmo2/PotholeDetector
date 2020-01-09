package pothole.detector.application.android.app;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class DataBaseHelper extends SQLiteOpenHelper {

    //array list of existing potholes
    //access needed to update when deletion from db occurs
    public volatile ArrayList<Coordinate> coordinates;


    // Database Info
    private static final String DATABASE_NAME = "coordinateDatabase";
    private static final int DATABASE_VERSION = 2;

    // Table names
    private static final String TABLE_COORDINATES = "coordinates";

    //Coordinate table columns
    private static final String KEY_COORDINATE_ID = "id";
    private static final String KEY_LATITUDE = "latitude";
    private static final String KEY_LONGITUDE = "longitude";

    private boolean dataChanged;

    /*
     * Constructor is private to prevent direct instantiation
     * Should instead make a call to getInstance() function
     */

    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Called when the database is being configured
    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    // called when database is created for the first time
    // if a database already exists with the same name, this method
    // will not be called
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_COORDINATE_TABLE = "CREATE TABLE " + TABLE_COORDINATES +
                "(" +
                KEY_COORDINATE_ID + " INTEGER PRIMARY KEY, " +
                KEY_LATITUDE + " REAL, " +
                KEY_LONGITUDE + " REAL " +
                ");";
        db.execSQL(CREATE_COORDINATE_TABLE);
        dataChanged = false;

    }

    /*
     * Called when the database needs to be upgraded.
     * This method will only be called if a database already exists on disk
     * with the same DATABASE_NAME, bu the DATABASE_VERSION is different than the version of the database
     * that exists on disk.
     */

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            // Simplest implementation is to drop all old tables and recreate them
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_COORDINATES);
            onCreate(db);
        }
    }

    // Insert a coordinate into the database
    public void addCoordinate(Coordinate coordinate) {
        // set the data change flag, indicating that the database has been updated.
        dataChanged = true;
        // Create and/or open the database for writing
        SQLiteDatabase db = getWritableDatabase();

        // Wrapping insert in a transaction to help with performance and
        // consistency of the database.
        db.beginTransaction();
        try {
            // insert coordinate values into proper columns
            ContentValues values = new ContentValues();
            values.put(KEY_LATITUDE, coordinate.latitude);
            values.put(KEY_LONGITUDE, coordinate.longitude);

            db.insertOrThrow(TABLE_COORDINATES, null, values);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to add coordinate to database.");
        } finally {
            db.endTransaction();
        }
    }


    public void clearData() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_COORDINATES, "1", null);
        db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" + TABLE_COORDINATES + "'");
    }

    public Cursor getAllData() {

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("Select * from " + TABLE_COORDINATES, null);
        return res;

    }

    public void deleteData(int id) {
        dataChanged = true;
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM "  + TABLE_COORDINATES + " WHERE " + KEY_COORDINATE_ID + " = '" + id + "'";
        db.execSQL(query);
        populateArray();
    }

    public void populateArray() {
        Cursor cursor = this.getAllData();
        this.coordinates = new ArrayList<>();


        try {
            if (cursor.moveToFirst()) {

                Coordinate potholeCoordinate = new Coordinate(cursor.getDouble(1), cursor.getDouble(2));
                this.coordinates.add(potholeCoordinate);


            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

    }

    // getter method for data changed flag.
    public boolean isDataChanged() {
        return dataChanged;
    }

    // returns an ArrayList<Coordinate> containing the pothole coordinates stored in the database.
    public ArrayList<Coordinate> getCoordinatesList() {
        Cursor cursor = this.getAllData();
        ArrayList<Coordinate> myList = new ArrayList<>();

        try{
            if(cursor.moveToFirst()) {
                Coordinate potholeCoordinate = new Coordinate(cursor.getDouble(1), cursor.getDouble(2));
                myList.add(potholeCoordinate);

            }
        }catch (Exception E) {
            E.printStackTrace();
        }

    return myList;
    }


    // reset the data changed flag
    // indicating we have the most up to date version of the database
    public void resetDataChanged() {
        dataChanged = false;
    }
}
