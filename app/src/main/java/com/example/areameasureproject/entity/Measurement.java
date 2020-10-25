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
    private String name;
    @Column
    private String date;
    @ForeignCollectionField(eager = true)
    private Collection<LatLngAdapter> coordinates;
    @Column
    private double area;

    public Measurement() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public static final class Builder {
        private String name;
        private String date;
        private Collection<LatLngAdapter> coordinates;
        private double area;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder date(String date) {
            this.date = date;
            return this;
        }

        public Builder coordinates(Collection<LatLngAdapter> coordinates) {
            this.coordinates = coordinates;
            return this;
        }

        public Builder area(double area) {
            this.area = area;
            return this;
        }

        public Measurement build() {
            Measurement measurement = new Measurement();
            measurement.name = this.name;
            measurement.date = this.date;
            measurement.coordinates = this.coordinates;
            measurement.area = this.area;
            return measurement;
        }
    }
}
