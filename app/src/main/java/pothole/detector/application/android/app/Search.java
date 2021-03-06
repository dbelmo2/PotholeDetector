package pothole.detector.application.android.app;

import android.content.Context;
import android.media.MediaPlayer;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class Search extends Thread {
    private double pLat, pLong;
    private DataBaseHelper database;
    GPS gps;
    Accelerometer accelerometer;
    private boolean potHoleFound = false;
    static MediaPlayer mediaPlayer;
    static String distanceToPot;
    int distance_int;
    Double distance_double;
    private Context context;
    PreSearch preSearch = new PreSearch();




    public Search(DataBaseHelper dataBase, GPS gps, Accelerometer accelerometer, MediaPlayer mediaPlayer, Context context) {
        this.database = dataBase;
        this.gps = gps;
        this.accelerometer = accelerometer;
        this.mediaPlayer = mediaPlayer;
        this.context = context;
        database.populateArray();

    }

    /*main Search thread, continuously checks if
    a pothole has been hit */
    public void run() {


        try {

            preSearch.start();

            while (true) {
                // while loop responsible for detecting new potholes


                if (accelerometer.potHoleFound()) {

                    gps.requestLoc();

                    pLat = gps.getLatitude();
                    pLong = gps.getLongitude();


                    database.coordinates.add(new Coordinate(pLat,pLong));
                    recordLocation(pLat, pLong);
                    accelerometer.reset();
                    sleep(5000);

                }


            }
        } catch (Exception A) {
            A.printStackTrace();
        }


    }

    // function which returns true if both coordinates were successfully recorded
    private synchronized void recordLocation(double lat, double lon) {
        Coordinate coordinate = new Coordinate(lat, lon);
        database.addCoordinate(coordinate);
    }


    public class GetJson  {


        Coordinate current;
        Coordinate pothole;
        String API_KEY;


        public synchronized String doInBackground(Coordinate c1, Coordinate c2) {
            current = c1;
            pothole = c2;



            String JSON = getJasonFromAPI();

            String distance = getDistance(JSON);


            distanceToPot = distance;
            return distance;
        }



        private synchronized String getJasonFromAPI() {

            String output = "";
            StringBuffer stringBuffer = new StringBuffer();
            API_KEY = "AIzaSyBXKa025y69ZY6Uj3vCMD_JEe7Nqx5o7hI";


            try {
                URL url = new URL("https://maps.googleapis.com/maps/api/distancematrix/json?units=metric&origins="+current.latitude+","+current.longitude+ "&destinations="+pothole.latitude+","+pothole.longitude+"&key=" + API_KEY);

                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.setReadTimeout(15000);
                httpURLConnection.setConnectTimeout(15000);
                httpURLConnection.connect();


                InputStream inputStream = httpURLConnection.getInputStream();

                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));

                String row;
                while ((row = br.readLine()) != null) {
                    stringBuffer.append(row);
                }
                br.close();
                output = stringBuffer.toString();


            } catch (MalformedURLException m) {
                m.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }


            return output;
        }

        String getDistance(String JSON) {

            String distanceValue = null;

            try {
                JSONObject jsonObject = new JSONObject(JSON)
                        .getJSONArray("rows")
                        .getJSONObject(0)
                        .getJSONArray("elements")
                        .getJSONObject(0)
                        .getJSONObject("distance");

                String distance = jsonObject.get("text").toString();
                distanceValue = distance;


            } catch (JSONException e) {
                e.printStackTrace();

            }


            return distanceValue;


        }

    }

    public synchronized void addToVector(Coordinate coordinate) {
        database.coordinates.add(coordinate);
    }


    public class PreSearch extends Thread {
        Coordinate current = new Coordinate(0,0);
        ArrayList<Coordinate> cList;

        public void run() {

            while (true) {
                if(database != null) {
                    cList = database.getCoordinatesList();
                    if(database.isDataChanged()) {
                        cList = database.getCoordinatesList();
                        database.resetDataChanged();
                    }
                }
                else {
                    continue;
                }


                gps.requestLoc();
                current.latitude = gps.getLatitude();
                current.longitude = gps.getLongitude();
                for (Coordinate c : cList) {
                    GetJson getJson = new GetJson();
                    distanceToPot = getJson.doInBackground(current, c);
                    if(distanceToPot != null) {
                        String[] tokens = distanceToPot.split(" ");

                        if (tokens[1] == "km") {
                            // very far
                            // do something here with speed
                            distance_double = Double.parseDouble(tokens[0]);
                            if (distance_double.compareTo(0.29) <= 0) {
                                Search.mediaPlayer.start();
                            }
                        } else {
                            try {
                            distance_int = Integer.parseInt(tokens[0]); }
                            catch(Exception E) {
                                E.printStackTrace();
                                System.out.println("distance: " + tokens);
                            }
                            if (distance_int < 150) {
                                Search.mediaPlayer.start();
                            }
                        }
                    }
                }
            }

        }
    }




}
