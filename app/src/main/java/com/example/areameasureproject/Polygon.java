package com.example.areameasureproject;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class Polygon {

    private int id;
    private double area;
    private List<LatLng> coordinates;

    public Polygon() {
    }

    public Polygon(int id, double area, List<LatLng> coordinates) {
        this.id = id;
        this.area = area;
        this.coordinates = coordinates;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getArea() {
        return area;
    }

    public void setArea(double area) {
        this.area = area;
    }

    public List<LatLng> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(List<LatLng> coordinates) {
        this.coordinates = coordinates;
    }

    @Override
    public String toString() {
        return "Polygon{" +
                "id=" + id +
                ", area=" + area +
                ", coordinates=" + coordinates +
                '}';
    }
}
