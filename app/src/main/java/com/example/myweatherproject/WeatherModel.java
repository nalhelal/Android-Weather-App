package com.example.myweatherproject;

/**
 * Created by Nora on 10/28/16.
 */

public class WeatherModel {




    public String mDate;
    public String mDescription;
    public String mTemp;
    public String mIcon;


    public WeatherModel(String date, String description, String temp, String icon) {

        this.mDate = date;
        this.mDescription = description;
        this.mTemp = temp;
        this.mIcon = icon;

    }
}
