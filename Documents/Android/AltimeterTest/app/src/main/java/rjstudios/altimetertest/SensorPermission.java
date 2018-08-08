package rjstudios.altimetertest;

import android.os.Bundle;
import android.widget.TextView;

import rjstudios.altimetertest.engine.AbsRuntimePermission;
import rjstudios.altimetertest.engine.HeightEngine;

/**
 * Created by Ronan on 1/3/2018.
 */

public class SensorPermission extends AbsRuntimePermission {

    private static final int REQUEST_PERMISSION = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        requestAppPermission(new String[]{
                        android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.INTERNET,
                        android.Manifest.permission.BODY_SENSORS},
                R.string.Permission_Text, REQUEST_PERMISSION);
    }



    @Override
    public void onPermissionGranted(int requestCode) {

    }
}
