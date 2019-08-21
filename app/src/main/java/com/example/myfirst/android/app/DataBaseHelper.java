package com.example.myfirst.android.app;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.math.BigDecimal;

import static android.content.ContentValues.TAG;

public class DataBaseHelper extends SQLiteOpenHelper {

    // Database Info
    private static final String DATABASE_NAME = "coordinateDatabase";
    private static final int DATABASE_VERSION = 2;

    // Table names
    private static final String TABLE_COORDINATES = "coordinates";

    //Coordinate table columns
    private static final String KEY_COORDINATE_ID = "id";
    private static final String KEY_LATITUDE = "latitude";
    private static final String KEY_LONGITUDE = "longitude";

    private static DataBaseHelper sInstance;

    public static synchronized DataBaseHelper getInstance(Context context) {
        // Using application context to ensure no memory leaks
        if (sInstance == null) {
            sInstance = new DataBaseHelper(context.getApplicationContext());
        }
        return sInstance;
    }

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

    public boolean checkCoordinate(Coordinate coordinate) {
        // Find latitude within range of 100 feet of current coordinate
        // https://sciencing.com/convert-latitude-longtitude-feet-2724.html
        final int formulaConst = 364320;
        final double threshold = 100.0 / formulaConst;
        double LAT_MIN = coordinate.latitude - threshold;
        double LAT_MAX = coordinate.latitude + threshold;
        double LONG_MIN = coordinate.longitude - threshold;
        double LONG_MAX = coordinate.longitude + threshold;

        String COORDINATES_SELECT_QUERY =
                String.format("SELECT * FROM %s " +
                                "WHERE %s BETWEEN %s AND %s ",
                        TABLE_COORDINATES,
                        KEY_LATITUDE, LAT_MIN,
                        LAT_MAX);

        // get readable and write-able objects unless low-disk space scenarios
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(COORDINATES_SELECT_QUERY, null);

        try {
            if (cursor.moveToFirst()) {

                // Get the registered pothole from the database with a latitude
                // that's in range and then make sure the longitude's are the same as well
                Coordinate potholeCoordinate = new Coordinate(cursor.getDouble(1), cursor.getDouble(2));

                // Function call to compare the two double values for latitude
                // If a match is found then check the corresponding item



                    if(LONG_MIN <= potholeCoordinate.longitude || potholeCoordinate.longitude >= LONG_MAX) {
                        return true;

                    }

            }
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to get posts from database");
            return false;
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return false;
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

        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM "  + TABLE_COORDINATES + " WHERE " + KEY_COORDINATE_ID + " = '" + id + "'";
        db.execSQL(query);


    }
}
