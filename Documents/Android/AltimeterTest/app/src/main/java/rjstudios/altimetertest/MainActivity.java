package rjstudios.altimetertest;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;


import rjstudios.altimetertest.engine.AbsRuntimePermission;
import rjstudios.altimetertest.engine.HeightEngine;
import rjstudios.altimetertest.engine.LocationActivity;
import rjstudios.altimetertest.engine.PressureActivity;
import rjstudios.altimetertest.engine.WeatherActivity;
import rjstudios.altimetertest.engine.WeatherClient;

//TO-DO
//CLEAN UP THIS DAMN CODE IT'S A MESS
//IMPLEMENT ERROR HANDLING FOR WHEN USER CANNOT CONNECT TO INTERNET AND GET WEATHER INFO

public class MainActivity extends AbsRuntimePermission {
    //public static Address carLocation;
    TextView textView;
    private static final int REQUEST_PERMISSION = 10; //not too sure why this is 10 so be careful
    public static final int MAP_ACTIVITY_CODE= 123456; //random number to appease returnIntent for the map
    public static final int WEATHER_ACTIVITY_CODE = 43210; //random number to appease returnIntent for the weather
    static HeightEngine HE; //This class does the math for calculating the height differences between the two recorded pressure data points
    static double carCoordinate[]; //Array that holds the Lat in [0] and Long in [0] easier to pass in the intents
    public static LatLng carLL; //Latitude and Longitude object for storing car location created for the MapsActivity and really only used there
    public static String MAP_RETURN_INTENT = "Map return intent"; //This should be implemented once the car is located and something is done
    public static String MAP_CAR_LOCATION_INTENT = "Coordinates for the car"; //Just a global instance for the map intent
     public static int PRESSURE_CONVERSION = 100; // converter the hPa to Pascal to keep SI

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //loadData();
        /*SensorFragment sFrag = (SensorFragment) getFragmentManager().findFragmentById(R.id.sensor_frag);
        sFrag.setSensor(1);*/
        textView = (TextView) findViewById(R.id.TextView);
        HE = new HeightEngine();
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
    }

    //MUST KEEP THESE TWO OVERRIDES IN ORDER FOR DATA SAVING AND LOADING TO BE FUNCTIONAL
    @Override
    protected void onPause() {
        super.onPause();
        //saveData();
    }
    //DO NOT DELETE
    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }


    //SAVE DATA FUNCTION
    //MAKE SURE TO UPDATE THIS EVERY TIME A NEW VARIABLE IS IMPLEMENTED INTO THE CODE
    //FORGOT THE WEATHER AND GAVE ME A LONG HEADACHE
    private void saveData(){
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat("Latitude", ((float) carLL.latitude));
        editor.putFloat("Longitude", ((float) carLL.longitude));
        editor.putFloat("Pressure", HE.getBeforePhonePressure());
        editor.putFloat("Weather", HE.getPressureAir(HE.getBEFORE()));
        editor.commit();
        //Toast.makeText(this, "saving: " + carLL.toString(), Toast.LENGTH_LONG).show();
        //textView.setText("Saving: " + carLL.toString() );
    }
    //LOAD DATA FUNCTION
    private void loadData(){
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        carLL = new LatLng(sharedPreferences.getFloat("Latitude", (float) carLL.latitude),
                sharedPreferences.getFloat("Longitude", (float) carLL.longitude));
        carCoordinate = new double[]{carLL.latitude, carLL.longitude};
        float temp = -1;
        /*float temp1 = -1;
        temp = sharedPreferences.getFloat("Pressure", temp1);*/
        HE.setPressurePhone(sharedPreferences.getFloat("Pressure", temp), HE.getBEFORE());
        HE.setPressureAir(sharedPreferences.getFloat("Weather", temp), HE.getBEFORE());
        //Toast.makeText(this, "Loading: " + carLL.toString(), Toast.LENGTH_LONG).show();
    }

    //KEEP THIS FOR THE APP PERMISSIONS
    @Override
    public void onPermissionGranted(int requestCode)
    {

    }
    //This function handles every intentForResult that returns and crunches the appropriate numbers
    //for the appropriate needs
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
                carCoordinate = data.getDoubleArrayExtra("location");
                carLL = new LatLng(carCoordinate[0], carCoordinate[1]);
                //textView.setText(carLL.toString());
                saveData();
            }
            else {
                //Toast.makeText(this, "Location Failed" , Toast.LENGTH_SHORT).show();
            }
        }
        else if (requestCode == WEATHER_ACTIVITY_CODE) {
            if(resultCode == Activity.RESULT_OK){
                Bundle bundle = data.getBundleExtra("returnBundle");
                double temp = bundle.getDouble("pressure");
                int position = bundle.getInt("position");
                HE.setPressureAir(((float) temp), position);
                if (position == HE.getAFTER()){
                    startMapIntent();
                }
                //textView.setText("" + data.getDoubleExtra("weather", temp));
            }
            else {
                textView.setText("Failed to get weather info");
                //start a map intent and use a calculate function that does not use the weather
                //maybe the weather is throwing everything off
            }
        }
    }

    //================ONCLICK LISTENERS===================//

    //MARK THE CAR (BEFORE)
    public void startMapResult(View view){
        startLocationResultIntent(MAP_ACTIVITY_CODE);
        startSensorIntent(Sensor.TYPE_PRESSURE);
        startWeatherIntent(WEATHER_ACTIVITY_CODE, HE.getBEFORE());
    }

    //LOCATE THE CAR (AFTER)
    public void MapActivity(View view){
        //HE.setPressurePhone(,HE.getBEFORE());

        //There is a bug that is causing an issue where the calculations is off by a mile
        //Temporarily disabling this to see if the weather API is the issue
        startWeatherIntent(WEATHER_ACTIVITY_CODE, HE.getAFTER());

        //changing back to this for now until the weather bug is fixed

        //changing the order of intent calling in order to specify weather info for locating car
        //this will be called in the onIntentReturn() function and will be passed a boolean to check
        //startMapIntent();
    }

    //===============INTENT STARTERS=======================//
    //This one is for marking the location of the car in the beginning
    public void startLocationResultIntent(int mapCode){
        Intent intent = new Intent(MainActivity.this, LocationActivity.class);
        //intent.putExtra(MapsActivity.INTENT_RESULT,true);
        startActivityForResult(intent, mapCode);
    }
    //This one is for getting the pressure information when marking the car
    public void startSensorIntent(int SENSOR_TYPE) {
        Intent i = new Intent(MainActivity.this, PressureActivity.class);
        i.putExtra(PressureActivity.SENSOR_MESSAGE, SENSOR_TYPE);
        startActivityForResult(i,SENSOR_TYPE);
    }
    //int position is used to clarify whether this weather data is for marking or locating car
    public void startWeatherIntent(int weatherCode, int position){
        Intent intent = new Intent(MainActivity.this, WeatherActivity.class);
        Bundle bundle = new Bundle();
        bundle.putDoubleArray("location", carCoordinate);
        bundle.putInt("position", position);
        intent.putExtra("bundle", bundle);
        startActivityForResult(intent, weatherCode);
    }
    //This is for starting the Map to locate the car
    public void startMapIntent(){
        Intent intent = new Intent(MainActivity.this, MapsActivity.class);
        intent.putExtra(MAP_CAR_LOCATION_INTENT, carCoordinate);
        startActivity(intent);
    }
}
