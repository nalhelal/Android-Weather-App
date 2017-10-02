package com.example.myweatherproject;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import static android.content.ContentValues.TAG;

/**
 * Created by Nora on 10/30/16.
 */

public class WeatherDataAsyncTask extends AsyncTask<String, Void, String> {
        //AsyncTask to fetch the weather from the url and get the objects

    private ProgressDialog mDialog;
    private WeatherActivity mActivity;
    public static ArrayList<WeatherModel> mAdapter = new ArrayList<WeatherModel>();

    //To use methods in the activity and to setup the dialog
    public WeatherDataAsyncTask(WeatherActivity activity) {
        this.mActivity = activity;
        mDialog = new ProgressDialog(activity);
    }



    @Override
    protected String doInBackground(String... params) {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String weatherJson = "";

        try {
            //The URL for the OpenWeatherMap API with the key attached
            final String BASE_URL = "http://api.openweathermap.org/data/2.5/forecast/daily?&APPID=34bcecac4f1928428e375ba447b2d612&";

            //The URL to map the icon to, as OpenWeatherMap doesn't have a url for the icons
            //but, a number that can mach photo in this link
            final  String ICON_ADDR = "http://openweathermap.org/img/w/";


            //params here is either a Zipcode or Lat&Lon values
            URL url = new URL(BASE_URL + params[0]  + "&mode=json");
            Log.v(TAG, "URL=" + url.toString());

            //Request and open the connection with OpenWeatherMap
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            //Read the stream
            reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));


            weatherJson = reader.readLine();
            Log.v(TAG, "Objects=" + weatherJson);


            try {
                //get the Objects starting from the List array
                JSONObject root = new JSONObject(weatherJson);
                JSONArray weatherList = root.getJSONArray("list");

                //To show the days/date values as Openweathermap provide
                // the value in un readable format (ex. "dt":1477940400)
                Calendar gc = new GregorianCalendar();


                for (int i = 0; i < weatherList.length(); i++) {
                    String dateDay;
                    String icon;
                    JSONObject dateObject = weatherList.getJSONObject(i);

                    //Get the Weather objects
                    //First access the nested array "weather'
                    JSONObject weatherObj = dateObject.getJSONArray("weather").getJSONObject(0);
                    //get these objects
                    JSONObject mainObj = dateObject.getJSONObject("temp");
                    icon = ICON_ADDR + weatherObj.getString("icon") + ".png";

                    //The day/date format
                    //get the month
                    String month = gc.getDisplayName(Calendar.MONTH, Calendar.LONG,
                            Locale.ENGLISH);
                    //The date
                    int date = gc.get(Calendar.DATE);
                    //The diplayed format
                    dateDay = month + "  (" + date + ")";
                    //Increase the days as the array increasing
                    gc.add(Calendar.DATE, 1);

                    //Get Values in the adapter to the WeatherModel to set up the view
                    mAdapter.add(new WeatherModel( dateDay,
                            weatherObj.getString("main"),
                            mainObj.getString("day"),
                            icon
                    ));
                }


            } catch (Exception e) {
                Log.e(TAG, "Error in Parsing (): " + e.toString());
            }


        }
        catch (Exception e) {
            Log.e(TAG, "Error in URL connecting (): " + e.toString());
        }

        return null;

    }



    protected void onPreExecute() {
        //Show a loading dialog message between parsing and displaying
        mDialog.setMessage("Loading...");
        mDialog.setCancelable(false);
        mDialog.show();

    }

    protected void onPostExecute(String result) {
        if(result!= null)
            Log.i("GPS", "onPostExecute launched");

        //Once get the result, display then in the ListView
        mActivity.setUpListView();
        //Close dialog if any
        if (mDialog.isShowing())
            mDialog.dismiss();
    }


}
