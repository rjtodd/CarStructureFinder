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


import static rjstudios.altimetertest.engine.PressureActivity.SENSOR_MESSAGE;


/**
 * A simple {@link Fragment} subclass.
 */
public class SensorFragment extends Fragment implements SensorEventListener{

    private long sensorId;
    protected boolean flag = false;
    private ListView listview;
    private SensorManager mSensorManager = null;
    int mSensorType = Sensor.TYPE_PRESSURE;
    public static final String SENSOR_MESSAGE = "Sensor type";
    //public static final String SENSOR_FRAG = "Fragment Intent";
    public boolean fragmentIntent = false;
    float pressData = -1f;
    TextView pressView;

    public SensorFragment() {
        // Required empty public constructor
    }

    public void setSensor(long id)
    {
        this.sensorId = id;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*View view = inflater.inflate(R.layout.activity_maps, container, false);
        Typeface myTypeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/COOPPBL.TTF");
        TextView tv = (TextView) view.findViewById(R.id.sensor_text);
        tv.setTypeface(myTypeface);*/


        //Intent intent = getIntent();
        //mSensorType = intent.getIntExtra(SENSOR_MESSAGE, mSensorType); //sensor type is received here
        //mSensorManager = (SensorManager) getSystemService(Service.SENSOR_SERVICE);

        //View view = inflater.inflate(R.layout.fragment_sensor, container, false);
        //listview = (ListView) getActivity().findViewById(R.id.sensor_text);
        //listview.setAdapter(adapter);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sensor, container, false);
    }

    @Override
    public void onStart()
    {
        super.onStart();
        View view = getView();
        if(view != null)
        {
            TextView pressView = (TextView) view.findViewById(R.id.sensor_text);
            pressView.setText(R.string.sensor_frag_text);
            //pressData((CharSequence) pressData);
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
        TextView pressView = (TextView) view.findViewById(R.id.sensor_text);
        float press = MainActivity.HE.calcHeightPress();
        String message;
        if (press < 0){
            message = "Go UP by: " + press * -1 + 'm';
        }
        else if (press > 0){
            message = "Go DOWN by: " + press + 'm';
        }
        else{
            message = "Did not change";
        }
        pressView.setText(message);
        //Toast.makeText(this.getActivity(), "P: " + event.values[0], Toast.LENGTH_SHORT).show();
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
