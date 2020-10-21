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
    private double Latitude;
    @Column
    private double Longitude;

    public LatLngAdapter() {
    }

    public LatLngAdapter(Measurement measurement, double latitude, double longitude) {
        this.measurement = measurement;
        Latitude = latitude;
        Longitude = longitude;
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

    @Override
    public String toString() {
        return "LatLngAdapter{" +
                "Latitude=" + Latitude +
                ", Longitude=" + Longitude +
                '}';
    }
}
