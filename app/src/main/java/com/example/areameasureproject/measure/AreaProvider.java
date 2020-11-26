package com.example.areameasureproject.measure;

import android.util.Log;

import com.example.areameasureproject.entity.LatLngAdapter;

import java.util.List;


public class AreaProvider {

    private static final String TAG = "AreaProvider";

    private final List<LatLngAdapter> coordinates;

    public AreaProvider(List<LatLngAdapter> coordinates) {
        this.coordinates = coordinates;
    }

    public double getArea() {
        return measureArea();
    }

    private double measureArea() {
        Log.e(TAG, "measureArea: " + coordinates.toString());

        double area = 0.0;
        int j = coordinates.size() - 1;
        for (int i = 0; i < coordinates.size(); i++) {
            area += (coordinates.get(j).getLatitude() + coordinates.get(i).getLatitude()) *
                    (coordinates.get(j).getLongitude() - coordinates.get(i).getLongitude());

            j = i;
        }
        return format(Math.abs(area / 2.0) * Math.pow(10, 10));
    }

    private double format(double value) {
        double scale = Math.pow(10, 3);
        return Math.round(value * scale) / scale;
    }
}
