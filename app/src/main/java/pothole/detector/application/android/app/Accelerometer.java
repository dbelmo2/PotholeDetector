package pothole.detector.application.android.app;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import androidx.appcompat.app.AppCompatActivity;

import java.io.Serializable;

public class
Accelerometer extends AppCompatActivity implements SensorEventListener, Serializable {
    private static final String TAG = "MainActivity";
    private float previousZ = 0.0f;
    private SensorManager sensoryManager;
    Sensor accelerometer;
    private boolean potholeHit = false;
    Context context;
    private boolean potHoleFound = false;
    private float thresh;
    private boolean verticalModeOn = false;


    public Accelerometer(Context context, Boolean verticalModeOn) {
        this.context = context;
        this.sensoryManager = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);
        this.accelerometer = sensoryManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensoryManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        thresh = 10.0F;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        float axisValue;
        if(!verticalModeOn) {
             axisValue = sensorEvent.values[2]; }
        else {
            axisValue = sensorEvent.values[1];
        }

        float difference = Math.abs(previousZ - axisValue);


        // function call to compare two float values
        if (Float.compare(difference, thresh) >= 0) {
            potholeHit = true;
        }
        else if (Float.compare(difference, thresh) < 0) {

            //System.out.println("Just a bump");
            potholeHit = false;
        }
        previousZ = axisValue;
    }

    public void setOrientation(boolean verticalModeOn ) {

        this.verticalModeOn = verticalModeOn;
    }
    public synchronized boolean potHoleFound() {
        return potholeHit;
    }
    public synchronized void reset() {
        potholeHit = false;
    }
    public synchronized void setTresh(float thresh) {
        this.thresh =  thresh;
    }
}