package com.example.android.moodymausam;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.android.moodymausam.data.SunshinePreferences;
import com.example.android.moodymausam.utilities.NetworkUtils;
import com.example.android.moodymausam.utilities.OpenWeatherJsonUtils;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {


    private TextView mWeatherTextView; //To store the weather display TextView

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast);

        mWeatherTextView = (TextView) findViewById(R.id.tv_weather_data);

        /*Once all our views are setup.Call loadWeatherData method to perform the network request to get data  */

        loadWeatherData();
    }


    /*
    * This method will get the user's preferred location for weather,and then tell
    * doInBackground method to get the weather data in background.
    * */
    private void loadWeatherData(){
        String location = SunshinePreferences.getPreferredWeatherLocation(this);
        //call the AsyncTask
        new FetchWeatherTask().execute(location);

    }


    /*
     * @Class FetchWeatherTask class that extends AsycnTask to perform network requests.
     */
    public class FetchWeatherTask extends AsyncTask<String, Void, String[]> {

        //Override doInBackground method to perform network requests.
        @Override
        protected String[] doInBackground(String... strings) {
            /*If there is no zip code then there is nothing to lookup.*/
            if (strings.length == 0) {
                return null;
            }

            String location = strings[0];

            //Pass the location var to buildUrl method in NetworkUtilities class for Request URL
            URL weatherRequestUrl = NetworkUtils.buildUrl(location);


            try {
                String jsonWeatherResponse = NetworkUtils
                        .getResponseFromHttpUrl(weatherRequestUrl);

                String[] simpleJsonWeatherData = OpenWeatherJsonUtils
                        .getSimpleWeatherStringsFromJson(MainActivity.this, jsonWeatherResponse);


                return simpleJsonWeatherData;


            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }


        /*Override the onPostExecute method to display  the resultsof network request.*/

        @Override
        protected void onPostExecute(String[] weatherData) {
            if (weatherData != null) {
                //Append to string in the TextView
                for (String weatherString : weatherData) {
                    mWeatherTextView.append((weatherString) + "\n\n\n");
                }
            }
        }
    }

    //On adding menu to inflate the menu for this Activity
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.forecast,menu);
        return true;
    }


    //To handle clicks on refresh button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id =item.getItemId();
        if(id == R.id.action_refresh){
            mWeatherTextView.setText("");
            loadWeatherData();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

