package com.example.areameasureproject;

import com.google.android.gms.maps.model.LatLng;

import java.util.Date;
import java.util.List;

public class Measurement {

    private int id;
    private Date date;
    private List<LatLng> coordinates;
    private double area;

    public Measurement() {
    }

    public Measurement(int id, Date date, List<LatLng> coordinates, double area) {
        this.id = id;
        this.date = date;
        this.coordinates = coordinates;
        this.area = area;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public List<LatLng> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(List<LatLng> coordinates) {
        this.coordinates = coordinates;
    }

    public double getArea() {
        return area;
    }

    public void setArea(double area) {
        this.area = area;
    }

    @Override
    public String toString() {
        return "Measurement{" +
                "id=" + id +
                ", date=" + date +
                ", coordinates=" + coordinates +
                ", area=" + area +
                '}';
    }
}
