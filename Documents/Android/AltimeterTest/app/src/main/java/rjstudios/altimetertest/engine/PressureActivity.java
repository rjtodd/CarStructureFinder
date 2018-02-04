package rjstudios.altimetertest.engine;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;


public class PressureActivity extends AppCompatActivity implements SensorEventListener{

    private SensorManager mSensorManager = null;
    int mSensorType;
    public static final String SENSOR_MESSAGE = "Sensor type";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        mSensorType = intent.getIntExtra(SENSOR_MESSAGE, mSensorType); //sensor type is received here
        mSensorManager = (SensorManager) getSystemService(Service.SENSOR_SERVICE);
        Sensor mSensor = mSensorManager.getDefaultSensor(mSensorType); //Initialize for specifies sensor type
        if(mSensor == null) //check if user has this sensor
            Toast.makeText(this, "You don't have the necessary sensor" , Toast.LENGTH_SHORT).show();
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Sensor mPressureSen = mSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        mSensorManager.registerListener(this, mPressureSen, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Intent returnIntent = new Intent();
        if (event.sensor.getType() == Sensor.TYPE_PRESSURE) {
            returnIntent.putExtra("pressure", event.values[0]);
            setResult(Activity.RESULT_OK, returnIntent);
            finish();
        }
        else if (event.sensor.getType() == Sensor.TYPE_AMBIENT_TEMPERATURE){
            returnIntent.putExtra("temperature", event.values[0]);
            setResult(Activity.RESULT_OK, returnIntent);
            finish();
        }
        else {
            setResult(Activity.RESULT_CANCELED, returnIntent);
            finish();
        }
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {}
}
