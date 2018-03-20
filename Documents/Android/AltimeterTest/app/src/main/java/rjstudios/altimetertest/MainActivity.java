package rjstudios.altimetertest;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.hardware.Sensor;
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


public class MainActivity extends AbsRuntimePermission {
    TextView textView;
    private static final int REQUEST_PERMISSION = 10; //not too sure why this is 10 so be careful
    public static final int MAP_ACTIVITY_CODE= 123456790; //random number to appease returnIntent for the map
    static HeightEngine HE;
    static int LOCATION = -1;
    static double carCoordinate[] = new double[2];
    static LatLng carLL;
    static Location carLocation;
    public static String MAP_RETURN_INTENT = "Map return intent";
    public static String MAP_CAR_LOCATION_INTENT = "Coordinates for the car";
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
        if (requestCode == Sensor.TYPE_AMBIENT_TEMPERATURE || requestCode == Sensor.TYPE_PRESSURE) {
            if (resultCode == Activity.RESULT_OK) {
                /*Toast.makeText(this,"Text",Toast.LENGTH_LONG).show();*/
                //float temperatureData = -99999f;
                float pressureData = -1f; //arbitrary initialization. The input in next line in uncertain FIX
                if (requestCode == Sensor.TYPE_PRESSURE) {
                    pressureData = data.getFloatExtra("pressure", pressureData); //pressure in hPa (millibar) 100 pascals
                    HE.setPressurePhone(pressureData, HE.getBEFORE());
                    //textView.setText("Pressure: " + pressureData);
                    Toast.makeText(this, "Pressure: " + pressureData, Toast.LENGTH_LONG).show();
                }
                /*else if(requestCode == Sensor.TYPE_AMBIENT_TEMPERATURE) {
                    temperatureData = data.getFloatExtra("temperature", temperatureData);
                    textView.setText("Temperature: " + temperatureData);
                    HE.setTemperaturePhone(temperatureData, LOCATION);
                }*/

            }
        } else if (requestCode == MAP_ACTIVITY_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                //Location location = new Location("String");
                double coord[] = new double[2];
                carCoordinate = data.getDoubleArrayExtra("location");
                carLL = new LatLng(carCoordinate[0], carCoordinate[1]);
                textView.setText("Lat Long" + carLL.toString());
                //carLocation = data.getParcelableExtra(MAP_RETURN_INTENT);
                //location = data.getBundleExtra()
                //location = data.getBundleExtra(MAP_RETURN_INTENT, location);
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
        startMapResultIntent(MAP_ACTIVITY_CODE);
        startSensorIntent(Sensor.TYPE_PRESSURE);
    }

    //===============ON CLICK LISTENERS====================//


    //===============INTENT STARTERS=======================//
    public void startMapResultIntent(int mapCode){
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
