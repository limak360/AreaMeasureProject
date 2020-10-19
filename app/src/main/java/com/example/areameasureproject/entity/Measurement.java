package com.example.areameasureproject.entity;

import com.example.areameasureproject.measure.LatLngAdapter;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Measurement implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String date;
    private List<LatLngAdapter> coordinates;
    private double area;

    public Measurement() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<LatLngAdapter> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(List<LatLngAdapter> coordinates) {
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
