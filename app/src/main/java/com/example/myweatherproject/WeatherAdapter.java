package com.example.myweatherproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Nora on 10/25/16.
 */

public class WeatherAdapter extends ArrayAdapter<WeatherModel> {

    private final ArrayList<WeatherModel> mAdapter;
    private final Context mContext;

    //Similar to the tutorial of Listview application we took in the class (AllRecipes)
    public WeatherAdapter(Context context, ArrayList<WeatherModel> objects) {
        //The list layout
        super(context, R.layout.weather_list, objects);
        mAdapter = objects;
        mContext = context;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        //Inflate the layout where the object will be displayed on
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context
                .LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.weather_list, parent, false);

        //use the adapter to map the objects to the right locations using the WeatherModel class
        WeatherModel weatherModel = mAdapter.get(position);

        //positions

        TextView temp = (TextView) rowView.findViewById(R.id.temp);
        TextView day = (TextView) rowView.findViewById(R.id.day);
        TextView description = (TextView) rowView.findViewById(R.id.description);
        ImageView img = (ImageView) rowView.findViewById(R.id.img);
        //get Weather Models
        day.setText(weatherModel.mDate);
        temp.setText(weatherModel.mTemp);
        description.setText(weatherModel.mDescription);

        //Use Picasso library to download the icon and set it as ImageView
        Picasso.with(mContext).load(weatherModel.mIcon).into(img);


     return rowView;
    }


}