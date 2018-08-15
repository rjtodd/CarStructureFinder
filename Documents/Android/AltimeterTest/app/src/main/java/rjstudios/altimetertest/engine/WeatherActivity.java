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
    TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        textView = (TextView) findViewById(R.id.WeatherText);
        textView.setText("It booted up");
        Intent intent = getIntent();
        double[] temp = new double[2];
        temp = intent.getDoubleArrayExtra("location");
        getWeather("" + temp[0],"" + temp[1]);

    }

    public void getWeather(String lat, String longt){
        final Intent returnIntent = new Intent();
        press = 0;
      /*  String lat = "" + MainActivity.carLL.latitude;
        String longt = "" + MainActivity.carLL.longitude;*/
        String url_2 = "http://api.openweathermap.org/data/2.5/weather?lat=34.052&lon=-118.04&APPID=30d4c6dc386cb72d7288dce7bb5d81e8";
        String url = "http://api.openweathermap.org/data/2.5/weather?lat=" + lat + "&lon=" + longt + "&" + R.string.weatherApi;
        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url_2, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject mainObject = response.getJSONObject("main");
                            press = mainObject.getDouble("pressure");
                            returnIntent.putExtra("weather",press);
                            //Toast.makeText(MainActivity.class,"JSON Parsing", Toast.LENGTH_LONG).show();
                            textView.setText("Pressure: " + press);
                            setResult(Activity.RESULT_OK, returnIntent);
                            finish();
                        }catch (JSONException e){
                            textView.setText("This failed");
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                textView.setText("ErrorResponse");
                setResult(Activity.RESULT_CANCELED, returnIntent);
                finish();
            }
        });
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(jor);

    }

}
