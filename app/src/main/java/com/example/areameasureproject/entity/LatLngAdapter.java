package com.example.areameasureproject.measure;

import java.io.Serializable;

public class LatLngAdapter implements Serializable {

    private double Latitude;
    private double Longitude;

    public LatLngAdapter() {
    }

    public LatLngAdapter(double latitude, double longitude) {
        Latitude = latitude;
        Longitude = longitude;
    }

    public double getLatitude() {
        return Latitude;
    }

    public void setLatitude(double latitude) {
        Latitude = latitude;
    }

    public double getLongitude() {
        return Longitude;
    }

    public void setLongitude(double longitude) {
        Longitude = longitude;
    }
}
