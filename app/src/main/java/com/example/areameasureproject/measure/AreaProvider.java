package com.example.areameasureproject.measure;

import android.util.Log;

import com.example.areameasureproject.entity.LatLngAdapter;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class AreaProvider {
    //todo zamiana jednostek
    private static final String TAG = "AreaProvider";
    private final List<LatLngAdapter> coordinates;

    public AreaProvider(List<LatLngAdapter> coordinates) {
        this.coordinates = coordinates;
    }

    public double getArea() {
        return measureArea();
    }

    private double measureArea() {
        double area = 0.0;
        Log.e(TAG, "measureArea: " + coordinates.toString());

        int j = coordinates.size() - 1;
        for (int i = 0; i < coordinates.size(); i++) {
            area += (coordinates.get(j).getLatitude() + coordinates.get(i).getLatitude()) *
                    (coordinates.get(j).getLongitude() - coordinates.get(i).getLongitude());

            j = i;
        }
        return Math.abs(area / 2.0) * Math.pow(10, 10);
    }
}
