package com.example.myweatherproject;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.Locale;

public class LanguageHelper extends AppCompatActivity {


    Spinner mLanguage;
    Locale mLocale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        //Handle the language spiiner
        mLanguage = (Spinner) findViewById(R.id.language);
        mLanguage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            //Switch the strings language based on the selected valuse (pos)
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {
                //For English
                if (pos == 1) {

                    Toast.makeText(parent.getContext(),
                            "You have selected English", Toast.LENGTH_SHORT)
                            .show();
                    setLocale("en");

                    //For Arabic
                } else if (pos == 2) {

                    Toast.makeText(parent.getContext(),
                            "You have selected Arabic", Toast.LENGTH_SHORT)
                            .show();
                    setLocale("ar");
                }

            }

            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }

        });
    }


    public void setLocale(String lang) {
        //handle the resources to change the local og the application to the selected language
        mLocale = new Locale(lang);
        Resources mResources = getResources();
        DisplayMetrics mMetrics = mResources.getDisplayMetrics();
        Configuration conf = mResources.getConfiguration();
        conf.locale = mLocale;
        mResources.updateConfiguration(conf, mMetrics);
        //Refresh when don with the new language
        Intent refresh = new Intent(this, WeatherActivity.class);
        startActivity(refresh);
    }



}




