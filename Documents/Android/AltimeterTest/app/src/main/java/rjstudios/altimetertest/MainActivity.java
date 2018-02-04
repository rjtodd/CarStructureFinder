package rjstudios.altimetertest;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.hardware.Sensor;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import rjstudios.altimetertest.engine.AbsRuntimePermission;
import rjstudios.altimetertest.engine.HeightEngine;
import rjstudios.altimetertest.engine.PressureActivity;


public class MainActivity extends AbsRuntimePermission {
    TextView textView;
    private static final int REQUEST_PERMISSION = 10; //not too sure why this is 10 so be careful
    HeightEngine HE;
    static int LOCATION = -1;
    static int PRESSURE_CONVERSION = 100; // converter the hPa to Pascal

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*SensorFragment sFrag = (SensorFragment) getFragmentManager().findFragmentById(R.id.sensor_frag);
        sFrag.setSensor(1);*/
        textView =(TextView)findViewById(R.id.TextView);
        HE = new HeightEngine();
        LOCATION = HE.getBEFORE();
        requestAppPermission(new String[]{
                        android.Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        android.Manifest.permission.INTERNET,},
                        //android.Manifest.permission.BODY_SENSORS},
                R.string.Permission_Text, REQUEST_PERMISSION);
    }

    //KEEP THIS FOR THE APP PERMISSIONS
    @Override
    public void onPermissionGranted(int requestCode)
    {

    }
    //AFTER PRESSURE ACTIVITY ENDS
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == Sensor.TYPE_AMBIENT_TEMPERATURE || requestCode == Sensor.TYPE_PRESSURE){
            if(resultCode == Activity.RESULT_OK) {
                /*Toast.makeText(this,"Text",Toast.LENGTH_LONG).show();*/
                //float temperatureData = -99999f;
                float pressureData = -1f; //arbitrary initialization. The input in next line in uncertain FIX
                if(requestCode == Sensor.TYPE_PRESSURE) {
                    pressureData = data.getFloatExtra("pressure", pressureData) * PRESSURE_CONVERSION; //pressure in hPa (millibar) 100 pascals
                    HE.setPressurePhone(pressureData, LOCATION);
                    textView.setText("Pressure: " + pressureData);
                }
                /*else if(requestCode == Sensor.TYPE_AMBIENT_TEMPERATURE) {
                    temperatureData = data.getFloatExtra("temperature", temperatureData);
                    textView.setText("Temperature: " + temperatureData);
                    HE.setTemperaturePhone(temperatureData, LOCATION);
                }*/

            }
            else {
                textView.setText("Error");
            }
        }
    }

    //===============ON CLICK LISTENERS====================/
    public void Before(View view) {
        LOCATION = HE.getBEFORE();
        startSensorIntent(Sensor.TYPE_PRESSURE);
    }
    public void After(View view)
    {
        LOCATION = HE.getAFTER();
        startSensorIntent(Sensor.TYPE_PRESSURE);
    }
    public void Result(View view){
        resultDialog();
    }
    public void MapActivity(View view){
        startMapIntent();
    }


    //===============ON CLICK LISTENERS====================//


    //===============INTENT STARTERS=======================//
    public void startMapIntent(){
        Intent intent = new Intent(MainActivity.this, MapsActivity.class);
        startActivity(intent);
    }

    public void startSensorIntent(int SENSOR_TYPE) {
        Intent i = new Intent(MainActivity.this, PressureActivity.class);
        i.putExtra(PressureActivity.SENSOR_MESSAGE, SENSOR_TYPE);
        startActivityForResult(i,SENSOR_TYPE);
    }
    //===============INTENT STARTERS=======================//

    public void resultDialog(){
        float press = HE.calcHeightPress();
        String message;
        if (press < 0){
            message = "Decreased by: " + press + 'm';
        }
        else if (press > 0){
            message = "Increased by: " + press + 'm';
        }
        else{
            message = "Did not change";
        }
        textView.setText(message);
    }


}
