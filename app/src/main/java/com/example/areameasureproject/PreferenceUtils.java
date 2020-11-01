package com.example.areameasureproject;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

public class PreferenceUtils {

    public static final String LOCATION_UPDATES_DEF_VALUE = "10000";
    public static final String LOCATION_ACCURACY_DEF_VALUE = "100";

    public static final String CM = " cm²";
    public static final String M = " m²";
    public static final String KM = " km²";

    private final Context mContext;

    public PreferenceUtils(Context mContext) {
        this.mContext = mContext;
    }

    public int getUpdatesPref() {
        SharedPreferences sharedPref = getSharedPreferences();
        return Integer.parseInt(sharedPref.getString("location_updates", LOCATION_UPDATES_DEF_VALUE));
    }

    public int getPriorityPref() {
        SharedPreferences sharedPref = getSharedPreferences();
        return Integer.parseInt(sharedPref.getString("location_accuracy", LOCATION_ACCURACY_DEF_VALUE));
    }

    public String formatArea(double area) {
        String areaPrefUnit = getAreaPrefUnit();
        if (areaPrefUnit.equals("cm")) {
            return area * 10000 + CM;
        } else if (areaPrefUnit.equals("km")) {
            return area / 1000000 + KM;
        } else {
            return area + M;
        }
    }

    private String getAreaPrefUnit() {
        SharedPreferences sharedPref = getSharedPreferences();
        boolean preference_cm = sharedPref.getBoolean("preference_cm", false);
        boolean preference_km = sharedPref.getBoolean("preference_km", false);

        if (preference_cm) {
            return "cm";
        } else if (preference_km) {
            return "km";
        } else {
            return "m";
        }
    }

    private SharedPreferences getSharedPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(mContext);
    }
}

