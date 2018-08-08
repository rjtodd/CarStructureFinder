package rjstudios.altimetertest.engine;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;

import rjstudios.altimetertest.MainActivity;

/**
 * Created by Ronan on 3/19/2018.
 */

public class LocationActivity extends AppCompatActivity {

    private LocationManager manager;
    private LocationListener locationListener;
    private FusedLocationProviderClient mFusedLocationClient;


    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        final Intent returnIntent = new Intent();
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        //THERE IS A CHANCE IT CAN RETURN A NULL
                        if (location != null){
                            //DATA[0] == LAT && DATA[1] == LONG
                            double[] data = new double[]{location.getLatitude(), location.getLongitude()};
                            returnIntent.putExtra("location", data);
                            setResult(Activity.RESULT_OK, returnIntent);
                            finish();
                        }
                        else{
                           setResult(Activity.RESULT_CANCELED, returnIntent);
                           finish();
                        }
                    }
                });


      /**  manager = (LocationManager) getSystemService(LOCATION_SERVICE); // get location service
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Intent returnIntent = new Intent();
                //  Toast.makeText(this, "In onLocationChanged Intent" , Toast.LENGTH_SHORT).show();
                double coordinate[] = new double[2];
                coordinate[0] = location.getLatitude();
                coordinate[1] = location.getLongitude();
                returnIntent.putExtra("location", coordinate);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };
        manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);**/
    }
}
