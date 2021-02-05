package com.example.areameasureproject.entity;

import com.j256.ormlite.field.DatabaseField;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class LatLngAdapter implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @DatabaseField(foreign = true, foreignAutoRefresh = true, foreignAutoCreate = true)
    private Measurement measurement;
    @Column
    private double latitude;
    @Column
    private double longitude;
    @Column
    private long time;

    public LatLngAdapter() {
    }

    public LatLngAdapter(double latitude, double longitude, long time) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.time = time;
    }

    public LatLngAdapter(Measurement measurement, double latitude, double longitude, long time) {
        this.measurement = measurement;
        this.latitude = latitude;
        this.longitude = longitude;
        this.time = time;
    }

    public Measurement getMeasurement() {
        return measurement;
    }

    public void setMeasurement(Measurement measurement) {
        this.measurement = measurement;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "LatLngAdapter{" +
                "Latitude=" + latitude +
                ", Longitude=" + longitude +
                '}';
    }
}
