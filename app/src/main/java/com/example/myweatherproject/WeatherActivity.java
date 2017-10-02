package com.example.myweatherproject;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import static com.example.myweatherproject.WeatherDataAsyncTask.mAdapter;


public class WeatherActivity extends AppCompatActivity {

    private final String TAG = "WeatherDataAsyncTask";

    LocationDetector mLocationDetector;
    private Button mButton;
    private Double mLongitude;
    private Double mLatitude;
    public static boolean mGetbyNetworkLocation;
    private SharedPreferences mSharedPreferences;
    private ProgressDialog mDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTitle(R.string.app_name);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        mDialog = new ProgressDialog(this);

        //To request location permission once the user start the app fore the first time
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        //For the preference settings( default and new)
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        //To check if Internet is avaliable
        //to handle the app when no connection found
        isNetworkStatusAvialable();

        // set the button for weather based on user location
        mButton = (Button) findViewById(R.id.load_button);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "clicked");
                //Call the locationDetector class
                mLocationDetector = new LocationDetector(WeatherActivity.this);

                //Loading dialog untill finding the location
                mDialog.setMessage("Loading...");
                mDialog.setCancelable(false);
                mDialog.show();

                //clear the adapter
                mAdapter.clear();

                //I sat a boolean value to make the app
                // switch the list from Zipcode forecast to Network based forecast
                mGetbyNetworkLocation = true;
                getWeatherSetting();

            }
        });
    }




    public void getWeatherSetting() {
        if (mDialog.isShowing())
            mDialog.dismiss();

        //check for updated values in the prerfrence settings
        String zipcode = mSharedPreferences.getString(getString(R.string.zipcode_key), getString(R.string.zipcode_default));
        String unit = mSharedPreferences.getString(getString(R.string.units_key), getString(R.string.units_default));
        String daysNum = mSharedPreferences.getString(getString(R.string.days_key), getString(R.string.days_default));

        Log.v(TAG, "got new location at " + zipcode);
        Log.v(TAG, "got new number of days " + daysNum);
        Log.v(TAG, "got new unit " + unit);

        //if the user chose to search by current location
        //then take the needed updated settings and go to next method
        if(mGetbyNetworkLocation)
            getLocationWeather(unit, daysNum);

        else
        { //If search by zipcode, execute the WeatherAsyncTask from here

            String zipWeather = "q=" + zipcode + ",USA" + "&units=" + unit + "&cnt=" + daysNum;
            WeatherDataAsyncTask task = new WeatherDataAsyncTask(WeatherActivity.this);
            task.execute(zipWeather);

        }
    }


    public void getLocationWeather(String unit, String daysNum) {

        //Get the Lat & Lon values from the Location Detector
            if (mLocationDetector.isCanGetLocation()) {
                mLongitude = mLocationDetector.getLongitude();
                mLatitude = mLocationDetector.getLatitude();

                //Execute WeatherDataAsyncTask using these values
                String latlon = "lat=" + mLatitude + "&lon=" + mLongitude + "&units=" + unit + "&cnt=" + daysNum;;

                WeatherDataAsyncTask task = new WeatherDataAsyncTask(WeatherActivity.this);
                task.execute(latlon);


                // show the user the obtained values if found
                Toast.makeText(getApplicationContext(), "Location Found", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Unable to get location.", Toast.LENGTH_SHORT).show();
               showDisabledAlert();
            }
    }



    // the list adapter for the returned values of the Asynctask to be displayed
    public void setUpListView() {
        // start the ListView Adapter
        final WeatherAdapter adapter = new WeatherAdapter(this, mAdapter);
        ListView mListView = (ListView) findViewById(R.id.listview);
        mListView.setAdapter(adapter);
    }





    //To request the location permision and handle the result
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    // if permission denied, disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }
    }





    //Show alert message to ask the user to enable the location from the settings
    public void showDisabledAlert() {

        final android.support.v7.app.AlertDialog.Builder dialog = new android.support.v7.app.AlertDialog.Builder(this);
        //The shown message
        dialog.setTitle("Enable Location")
                .setMessage("Your Locations is disabled.\nPlease Enable it to " +
                        "use this app")
                //settings button
                .setPositiveButton("Location Settings", new DialogInterface.OnClickListener() {
                    //Location of Phone settings
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Intent mIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(mIntent);
                    }
                }) //if cancel
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        //set the value to false to view result based on settings last prefrence
                        mGetbyNetworkLocation = false;
                        getWeatherSetting();

                    }
                });
        dialog.show();
    }



    //To check internet connection
    public void isNetworkStatusAvialable () {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            android.net.NetworkInfo wifi = connectivityManager
                    .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            android.net.NetworkInfo datac = connectivityManager
                    .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if ((wifi != null & datac != null)
                    && (wifi.isConnected() | datac.isConnected())) {
                //iflocation avaliable then OK
            } else {
                //iF no internet, the show this message to notify the user
                Toast.makeText(getApplicationContext(), "No internet is avialable", Toast.LENGTH_LONG).show();

            }
        }
    }

    //To update the Listview everytime the user change something in the settings
    @Override
    public void onStart() {
        super.onStart();

        isNetworkStatusAvialable();

        //To use the Zipcode fom the settings
        mGetbyNetworkLocation = false;
        mAdapter.clear();
        mDialog.setMessage("Loading...");
        mDialog.setCancelable(false);
        mDialog.show();
        getWeatherSetting();
    }


    //add the items to the action bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    //TO handle the action bar item clicks
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        //Handle the items on the menu, and start the clicked one
        //The first is for the prefrence settings (zipcode, unit, days)
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;

        }
        //This to handle the language
        if (id == R.id.action_language) {
            startActivity(new Intent(this, LanguageHelper.class));
            return true;


        }
        return super.onOptionsItemSelected(item);


    }

}













