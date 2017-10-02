package com.example.myweatherproject;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;

public class SettingsActivity extends PreferenceActivity implements Preference.OnPreferenceChangeListener {

    private final String TAG = "SettingsActivity";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // add the preference layout in the XML file
        addPreferencesFromResource(R.xml.settings_main);

        //This to update the UI when preferences changes
        bindPreferenceSummaryToValue(findPreference(getString(R.string.zipcode_key)));
        bindPreferenceSummaryToValue(findPreference(getString(R.string.units_key)));
        bindPreferenceSummaryToValue(findPreference(getString(R.string.days_key)));


        Log.v(TAG, "preference changed " + getString(R.string.zipcode_key)
                + getString(R.string.units_key)
                + getString(R.string.days_key));
    }



    private void bindPreferenceSummaryToValue(Preference preference) {
        // To update the values above, we attach a listener
        preference.setOnPreferenceChangeListener(this);

        //Start the listener with the prefrences current values
        onPreferenceChange(preference, PreferenceManager.getDefaultSharedPreferences(preference.getContext())
                .getString(preference.getKey(), ""));

    }


    @Override
    public boolean onPreferenceChange(Preference preference, Object value) {
        //For the List preferences, find the correct valuse in the list entries
        String stringValue = value.toString();
        if (preference instanceof ListPreference) {
            ListPreference listPreference = (ListPreference) preference;
            int prefIndex = listPreference.findIndexOfValue(stringValue);
            if (prefIndex >= 0) {
                preference.setSummary(listPreference.getEntries()[prefIndex]);
            }
        } else {
            //non list preferences (zipcode), set the summary to the value
            preference.setSummary(stringValue);
        }
        return true;
    }

}




