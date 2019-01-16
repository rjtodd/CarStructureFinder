package rjstudios.altimetertest;

import android.app.Service;
import android.content.Intent;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import java.text.DecimalFormat;

import static rjstudios.altimetertest.engine.PressureActivity.SENSOR_MESSAGE;


/**
 * A simple {@link Fragment} subclass.
 */
public class SensorFragment extends Fragment implements SensorEventListener{

    private final float METER_TO_FEET_CONVERSION = 3.28084f;
    private SensorManager mSensorManager = null;
    float pressData = -1f;

    public SensorFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        return inflater.inflate(R.layout.fragment_sensor, container, false);
    }

    @Override
    public void onStart()
    {
        super.onStart();
        View view = getView();
        if(view != null)
        {
            TextView pressView = view.findViewById(R.id.sensor_text);
            pressView.setText(R.string.sensor_frag_text);
            mSensorManager = (SensorManager) this.getActivity().getSystemService(Service.SENSOR_SERVICE);
            Sensor mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE); //Initialize for specifies sensor type
            if(mSensor == null) //check if user has this sensor
                Toast.makeText(this.getActivity(), "You don't have the necessary sensor" , Toast.LENGTH_SHORT).show();
            mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_UI);

        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        pressData = event.values[0];
        MainActivity.HE.setPressurePhone(pressData, MainActivity.HE.getAFTER());
        View view = getView();
        DecimalFormat df = new DecimalFormat("###.##");
        TextView pressView = view.findViewById(R.id.sensor_text);
        pressView.setSingleLine(false);
        float press = MainActivity.HE.calcHeightPressWeather(pressData);
        float pressFT = press * METER_TO_FEET_CONVERSION;
        float pressRaw = MainActivity.HE.calcHeightPress(pressData) * METER_TO_FEET_CONVERSION;
        String message;
        if (press < 0){
            message = "Go UP by: " + df.format(pressFT * -1 ) + "ft \n" +
                    "No weather: " + df.format(pressRaw * -1) +"ft";
        }
        else if (press > 0){
            message = "Go DOWN by: " + df.format(pressFT) + "ft \n" +
                    "No weather: " + df.format(pressRaw) + "ft";
        }
        else{
            message = "Did not change";
        }
        pressView.setText(message);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //FIGURE OUT WHAT TO DO FOR HERE AS WELL
    }

    @Override
    public void onResume() {
        super.onResume();
        Sensor mPressureSen = mSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        mSensorManager.registerListener(this, mPressureSen, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onPause()
    {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

}
