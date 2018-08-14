package rjstudios.altimetertest;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import rjstudios.altimetertest.engine.AbsRuntimePermission;
import rjstudios.altimetertest.engine.HeightEngine;
import rjstudios.altimetertest.engine.LocationActivity;
import rjstudios.altimetertest.engine.PressureActivity;

//CLEAN UP THIS DAMN CODE IT'S A MESS


public class MainActivity extends AbsRuntimePermission {
    //public static Address carLocation;
    TextView textView;
    private static final int REQUEST_PERMISSION = 10; //not too sure why this is 10 so be careful
    public static final int MAP_ACTIVITY_CODE= 123456790; //random number to appease returnIntent for the map
    static HeightEngine HE;
    static int LOCATION = -1;
    static double carCoordinate[];
    static LatLng carLL;
    static Location carLocation;
    public static String MAP_RETURN_INTENT = "Map return intent";
    public static String MAP_CAR_LOCATION_INTENT = "Coordinates for the car";
    static int PRESSURE_CONVERSION = 100; // converter the hPa to Pascal to keep SI

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*SensorFragment sFrag = (SensorFragment) getFragmentManager().findFragmentById(R.id.sensor_frag);
        sFrag.setSensor(1);*/
        textView = (TextView) findViewById(R.id.TextView);
        HE = new HeightEngine();
        LOCATION = HE.getBEFORE();
        carLL = new LatLng(-1, 1);
        carCoordinate = new double[2];
        //boolean myBoolean = false;
        try {
            //myBoolean = savedInstanceState.getBoolean("MyBoolean");
            float myFloat = savedInstanceState.getFloat("myPressure");
            textView.setText("Last Pressure: " + myFloat);
            HE.setPressurePhone(myFloat, HE.getBEFORE());
            carCoordinate = savedInstanceState.getDoubleArray("myLocation");
            carLL = new LatLng(carCoordinate[0], carCoordinate[1]);
            //Toast.makeText(this, carLL.toString(), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            //Toast.makeText(this, "Failed to load data", Toast.LENGTH_LONG).show();
            //loadData();
            e.printStackTrace();
        }



        requestAppPermission(new String[]{
                        android.Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        android.Manifest.permission.INTERNET,},
                //android.Manifest.permission.BODY_SENSORS},
                R.string.Permission_Text, REQUEST_PERMISSION);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Restore UI state from the savedInstanceState.
        // This bundle has also been passed to onCreate.
        /*float myFloat = savedInstanceState.getFloat("myPressure");
        boolean myBoolean = savedInstanceState.getBoolean("MyBoolean");

        textView.setText("Last Pressure: " + myFloat);
        HE.setPressurePhone(myFloat, HE.getBEFORE());
        carCoordinate = savedInstanceState.getDoubleArray("myLocation");
        carLL = new LatLng(carCoordinate[0], carCoordinate[1]);*/

    }

    @Override
    protected void onPause() {
        super.onPause();

        //saveData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    //SAVE FUNCTION
    private void saveData(){
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat("Latitude", ((float) carLL.latitude));
        editor.putFloat("Longitude", ((float) carLL.longitude));
        editor.putFloat("Pressure", HE.getBeforePhonePressure());
        editor.commit();
        //Toast.makeText(this, "saving: " + carLL.toString(), Toast.LENGTH_LONG).show();
        textView.setText("Saving: " + carLL.toString() );
    }
    //LOAD FUNCTION
    private void loadData(){
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        carLL = new LatLng(sharedPreferences.getFloat("Latitude", (float) carLL.latitude),
                sharedPreferences.getFloat("Longitude", (float) carLL.longitude));
        carCoordinate = new double[]{carLL.latitude, carLL.longitude};
        float temp = -1;
        /*float temp1 = -1;
        temp = sharedPreferences.getFloat("Pressure", temp1);*/
        HE.setPressurePhone(sharedPreferences.getFloat("Pressure", temp), HE.getBEFORE());
        Toast.makeText(this, "Loading: " + carLL.toString(), Toast.LENGTH_LONG).show();
    }

    //KEEP THIS FOR THE APP PERMISSIONS
    @Override
    public void onPermissionGranted(int requestCode)
    {

    }
    //AFTER PRESSURE ACTIVITY ENDS
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Sensor.TYPE_AMBIENT_TEMPERATURE || requestCode == Sensor.TYPE_PRESSURE) {
            if (resultCode == Activity.RESULT_OK) {
                /*Toast.makeText(this,"Text",Toast.LENGTH_LONG).show();*/
                //float temperatureData = -99999f;
                float pressureData = -1f; //arbitrary initialization. The input in next line in uncertain FIX
                if (requestCode == Sensor.TYPE_PRESSURE) {
                    pressureData = data.getFloatExtra("pressure", pressureData); //pressure in hPa (millibar) 100 pascals
                    HE.setPressurePhone(pressureData, HE.getBEFORE());
                    //textView.setText("Pressure: " + pressureData);
                    //Toast.makeText(this, "Pressure: " + pressureData, Toast.LENGTH_LONG).show();
                    saveData();
                }
                /*else if(requestCode == Sensor.TYPE_AMBIENT_TEMPERATURE) {
                    temperatureData = data.getFloatExtra("temperature", temperatureData);
                    textView.setText("Temperature: " + temperatureData);
                    HE.setTemperaturePhone(temperatureData, LOCATION);
                }*/

            }
        }
        else if (requestCode == MAP_ACTIVITY_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                //Location location = new Location("String");
                double coord[] = new double[2];
                carCoordinate = data.getDoubleArrayExtra("location");
                carLL = new LatLng(carCoordinate[0], carCoordinate[1]);
                textView.setText(carLL.toString());
                saveData();
                //carLocation = data.getParcelableExtra(MAP_RETURN_INTENT);
                //location = data.getBundleExtra()
                //location = data.getBundleExtra(MAP_RETURN_INTENT, location);
            }
            else {
                //Toast.makeText(this, "Location Failed" , Toast.LENGTH_SHORT).show();
            }
        }
    }

    //===============ON CLICK LISTENERS====================/
    /*public void Before(View view) {
        LOCATION = HE.getBEFORE();
        startSensorIntent(Sensor.TYPE_PRESSURE);
    }*/

    public void MapActivity(View view){
        //HE.setPressurePhone(,HE.getBEFORE());
        startMapIntent();
    }

    public void startMapResult(View view){
        startLocationResultIntent(MAP_ACTIVITY_CODE);
        startSensorIntent(Sensor.TYPE_PRESSURE);
    }

    //===============INTENT STARTERS=======================//
    public void startLocationResultIntent(int mapCode){
        Intent intent = new Intent(MainActivity.this, LocationActivity.class);
        //intent.putExtra(MapsActivity.INTENT_RESULT,true);
        startActivityForResult(intent, mapCode);
    }

    public void startMapIntent(){
        Intent intent = new Intent(MainActivity.this, MapsActivity.class);
        intent.putExtra(MAP_CAR_LOCATION_INTENT, carCoordinate);
        startActivity(intent);
    }

    public void startSensorIntent(int SENSOR_TYPE) {
        Intent i = new Intent(MainActivity.this, PressureActivity.class);
        i.putExtra(PressureActivity.SENSOR_MESSAGE, SENSOR_TYPE);
        startActivityForResult(i,SENSOR_TYPE);
    }
}
