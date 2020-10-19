package com.example.areameasureproject.measure;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class AreaProvider {

    private final List<LatLng> coordinates;

    public AreaProvider(List<LatLng> coordinates) {
        this.coordinates = coordinates;
    }

    public double getArea() {
        return measureArea();
    }

    private double measureArea() {
        double area = 0.0;

        int j = coordinates.size() - 1;
        for (int i = 0; i < coordinates.size(); i++) {
            area += (coordinates.get(j).latitude + coordinates.get(i).latitude) *
                            (coordinates.get(j).longitude - coordinates.get(i).longitude);

            j = i;
        }
        return Math.abs(area / 2.0);
    }
}