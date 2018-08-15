package rjstudios.altimetertest.engine;

import android.content.Intent;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


import rjstudios.altimetertest.MainActivity;
import rjstudios.altimetertest.R;

/**
 * Created by Ronan on 7/19/2017.
 */

public class WeatherClient {

    double[] latLng;
    public WeatherClient() {
        //TO-DO
        latLng= new double[2];
        //Intent intent = getIntent();
        //latLng = intent.getDoubleArrayExtra("Weather");
    }

    public void getPressure() {
        String url = "api.openweathermap.org/data/2.5/weather?lat={" + "}&lon={lon}";

    }
}
