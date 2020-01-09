package pothole.detector.application.android.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.myfirst.R;

import java.util.ArrayList;

public class Three_column_adapter extends ArrayAdapter<Coordinate> {

    private LayoutInflater minflater;
    private ArrayList<Coordinate> coordinates;
    private int myId;

    public Three_column_adapter(Context context, int textViewResourceId, ArrayList<Coordinate> coordinates) {
        super(context,textViewResourceId,coordinates);
        this.coordinates = coordinates;
        minflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        this.myId = textViewResourceId;

    }

    public View getView(int position, View convertView, ViewGroup parents) {
        convertView = minflater.inflate(myId,null);
        Coordinate coordinate = coordinates.get(position);

        String temp = coordinate.toString();

        if(temp.length() != 0) {
            TextView identity  = (TextView) convertView.findViewById(R.id.ID_text);
            TextView latitude  = (TextView) convertView.findViewById(R.id.LAT_text);
            TextView longitude  = (TextView) convertView.findViewById(R.id.LONG_text);

            if(identity != null) {
                identity.setText(Integer.toString(coordinate.id));

            }
            if(latitude!= null) {
                latitude.setText(Double.toString(coordinate.latitude));

            }
            if(longitude != null) {
                longitude.setText(Double.toString(coordinate.longitude));

            }

        }

        return convertView;
    }

}