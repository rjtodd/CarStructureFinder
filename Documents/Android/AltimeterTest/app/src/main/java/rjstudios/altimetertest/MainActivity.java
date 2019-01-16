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

import com.google.android.gms.maps.model.LatLng;


import rjstudios.altimetertest.engine.AbsRuntimePermission;
import rjstudios.altimetertest.engine.HeightEngine;
import rjstudios.altimetertest.engine.LocationActivity;
import rjstudios.altimetertest.engine.PressureActivity;
import rjstudios.altimetertest.engine.WeatherActivity;

//TO-DO
//CLEAN UP THIS DAMN CODE IT'S A MESS
//IMPLEMENT ERROR HANDLING FOR WHEN USER CANNOT CONNECT TO INTERNET AND GET WEATHER INFO

public class MainActivity extends AbsRuntimePermission {
    //public static Address carLocation;
    TextView textView;
    private static final int REQUEST_PERMISSION = 10; //not too sure why this is 10 so be careful
    public static final int MAP_ACTIVITY_CODE= 123456; //random number to appease returnIntent for the map
    public static final int WEATHER_ACTIVITY_CODE = 43210; //random number to appease returnIntent for the weather
    public static final int WEATHER_PRESS_REQUEST_CODE = 1231231; //random number to differentiate the return type of the Intent
    public static final int WEATHER_TEMP_REQUEST_CODE = 2094823; //random number to differentiate the return type of the Intent
    public static HeightEngine HE; //This class does the math for calculating the height differences between the two recorded pressure data points
    static double carCoordinate[]; //Array that holds the Lat in [0] and Long in [0] easier to pass in the intents
    public static LatLng carLL; //Latitude and Longitude object for storing car location created for the MapsActivity and really only used there
    public static String MAP_RETURN_INTENT = "Map return intent"; //This should be implemented once the car is located and something is done
    public static String MAP_CAR_LOCATION_INTENT = "Coordinates for the car"; //Just a global instance for the map intent
    public static int PRESSURE_CONVERSION = 100; // converter the hPa to Pascal to keep SI
    public static long THIRTY_MINUTES_IN_mMILLIsECONDS = 1800000; //This will be used to ensure that data from OWM won't skew data

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*SensorFragment sFrag = (SensorFragment) getFragmentManager().findFragmentById(R.id.sensor_frag);
        sFrag.setSensor(1);*/
        textView = (TextView) findViewById(R.id.TextView);
        HE = new HeightEngine();
        carLL = new LatLng(-1, 1);
        carCoordinate = new double[2];
        //boolean myBoolean = false;
        try {
            //myBoolean = savedInstanceState.getBoolean("MyBoolean");
            /*float myFloat = savedInstanceState.getFloat("myPressure");
            textView.setText("Last Pressure: " + myFloat);
            HE.setPressurePhone(myFloat, HE.getBEFORE());
            carCoordinate = savedInstanceState.getDoubleArray("myLocation");
            carLL = new LatLng(carCoordinate[0], carCoordinate[1]);
            //Toast.makeText(this, carLL.toString(), Toast.LENGTH_LONG).show();*/

            //------------TRY AND REPLACE WITH THE LOADDATA() FUNCTION--------------//
            loadData();

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
        editor.putFloat("Pressure", HE.getPhonePressure(HE.getBEFORE()));
        editor.putFloat("Weather", HE.getPressureAir(HE.getBEFORE()));
        editor.putFloat("Humidity", HE.getHumidity(HE.getBEFORE()));
        editor.putFloat("Temperature", HE.getTemperature(HE.getBEFORE()));
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
        HE.setHumidity(sharedPreferences.getFloat("Humidity", temp), HE.getBEFORE());
        HE.setTemperatureAir(sharedPreferences.getFloat("Temperature", temp), HE.getBEFORE());
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
                else if(requestCode == Sensor.TYPE_AMBIENT_TEMPERATURE) {
                    float temperatureData = -999f;
                    temperatureData = data.getFloatExtra("temperature", temperatureData);
                    textView.setText("Temperature: " + temperatureData);
                    HE.setTemperaturePhone(temperatureData, HE.getBEFORE());
                }

            }
        }
        //LOCATION INTENT
        else if (requestCode == MAP_ACTIVITY_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                //remove hardcode
                //USE RETURN INTENT
                //carCoordinate = data.getDoubleArrayExtra("location");
                carCoordinate = data.getDoubleArrayExtra(getResources().getString(R.string.Location_Intent_Return));
                carLL = new LatLng(carCoordinate[0], carCoordinate[1]);
                //textView.setText(carLL.toString());
                saveData();
            }
            else {
                //Toast.makeText(this, "Location Failed" , Toast.LENGTH_SHORT).show();
            }
        }
        else if (requestCode == WEATHER_ACTIVITY_CODE) {
            if(resultCode == Activity.RESULT_OK) {


                //removing hardcoded values
                //MUST USE RETURN INTENT
                // Bundle bundle = data.getBundleExtra("returnBundle");
                Bundle bundle = data.getBundleExtra(getResources().getString(R.string.Bundle_Return_Weather));
                //double temp = bundle.getDouble("pressure");
                double temp = bundle.getDouble(getResources().getString(R.string.Pressure_Intent_Return));
                double temp2 = bundle.getDouble(getResources().getString(R.string.Temperature_Intent_Return));
                double temp3 = bundle.getDouble(getResources().getString(R.string.Humidity_Intent_Return));
                //int position = bundle.getInt("position");
                int position = bundle.getInt(getResources().getString(R.string.Position_Intent_Return));
                HE.setTemperatureAir((float) temp2, position);
                HE.setHumidity((float) temp3, position);
                HE.setPressureAir(((float) temp), position);
                if (position == HE.getAFTER()) {
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
        saveData();
    }

    //================ONCLICK LISTENERS===================//

    //MARK THE CAR (BEFORE)
    public void startMapResult(View view){
        startLocationResultIntent(MAP_ACTIVITY_CODE);
        //startLocationResultIntent(getResources().getString(R.string.Position_Intent));
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
        //Replace these hardcoded messages with values from R.String
        //bundle.putDoubleArray("location", carCoordinate);
        bundle.putDoubleArray(getResources().getString(R.string.Location_Intent), carCoordinate);

        //bundle.putInt("position", position);
        bundle.putInt(getResources().getString(R.string.Position_Intent), position);

        //intent.putExtra("bundle", bundle);
        intent.putExtra(getResources().getString(R.string.Bundle_Start_Weather), bundle);
        startActivityForResult(intent, weatherCode);
    }
    //This is for starting the Map to locate the car
    public void startMapIntent(){
        Intent intent = new Intent(MainActivity.this, MapsActivity.class);
        Bundle bundle = new Bundle();
        bundle.putDoubleArray(getResources().getString(R.string.Map_Intent), carCoordinate);
        intent.putExtra(getResources().getString(R.string.Bundle_Start_Map), bundle);
        startActivity(intent);
    }
}
