package com.example.areameasureproject.entity;

import com.j256.ormlite.field.ForeignCollectionField;

import java.io.Serializable;
import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Measurement implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String date;
    @ForeignCollectionField(eager = true)
    private Collection<LatLngAdapter> coordinates;
    @Column
    private double area;

    public Measurement() {
    }

//    public static class Build extends Measurement {
//        private String date;
//        private Collection<LatLngAdapter> coordinates;
//        private double area;
//
//    }

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

    public Collection<LatLngAdapter> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Collection<LatLngAdapter> coordinates) {
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
