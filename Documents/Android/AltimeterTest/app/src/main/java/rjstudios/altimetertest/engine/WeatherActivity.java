package rjstudios.altimetertest.engine;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import rjstudios.altimetertest.MainActivity;
import rjstudios.altimetertest.R;

public class WeatherActivity extends AppCompatActivity {

    public double press;
    public double tempAir;
    public double humidity;
    static int position = -1; //THIS IS TO PASS ON THE ARRAY POSITION TO DETERMINE BEFORE OR AFTER FOR LOCATING THE CAR
    //TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();

        //remove hardcoding
        //START INTENT MUST BE USED HERE
        //Bundle bundle = intent.getBundleExtra("bundle");
        Bundle bundle = intent.getBundleExtra(getResources().getString(R.string.Bundle_Start_Weather));
        //double[] temp = bundle.getDoubleArray("location");
        double[] temp = bundle.getDoubleArray(getResources().getString(R.string.Location_Intent));
        //position = bundle.getInt("position");
        position = bundle.getInt(getResources().getString(R.string.Position_Intent));
        //temp = intent.getDoubleArrayExtra("location");
        getWeather(temp[0], temp[1]);

    }
    //The workhorse function that will get and parse the JSON from OWM and returns it
    public void getWeather(double lat, double longt){
        final Intent returnIntent = new Intent();
        final Bundle returnBundle= new Bundle();
        press = 0;
        tempAir = 0;
        humidity = 0;

        //PREPPING THE API CALLING USING LATITUDE AND LONGITUDE
        //String url = "http://api.openweathermap.org/data/2.5/weather?lat=" + lat + "&lon=" + longt + getResources().getString(R.string.weatherApi);
        String url = "http://api.openweathermap.org/data/2.5/weather?lat=" + lat + "&lon=" + longt + getResources().getString(R.string.WeatherMapAPI);
        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject mainObject = response.getJSONObject("main");
                            press = mainObject.getDouble("pressure");
                            tempAir = mainObject.getDouble("temp"); //Default unit is Kelvin
                            humidity = mainObject.getDouble("humidity");
                            //Replacing hardcoded string with values in R.string
                            //RETURN INTENTS NEED TO BE USED HERE
                            returnBundle.putDouble(getResources().getString(R.string.Pressure_Intent_Return), press);
                            returnBundle.putDouble(getResources().getString(R.string.Temperature_Intent_Return), tempAir);
                            returnBundle.putDouble(getResources().getString(R.string.Humidity_Intent_Return), humidity);
                            returnBundle.putInt(getResources().getString(R.string.Position_Intent_Return), position);
                            returnIntent.putExtra(getResources().getString(R.string.Bundle_Return_Weather), returnBundle);
                            setResult(Activity.RESULT_OK, returnIntent);
                            finish();
                        }catch (JSONException e){
                            //textView.setText("This failed");
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //textView.setText("ErrorResponse");
                setResult(Activity.RESULT_CANCELED, returnIntent);
                finish();
            }
        });
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(jor);

    }

}
