package com.example.areameasureproject.measure;

import android.location.Location;

import com.example.areameasureproject.db.DatabaseManager;
import com.example.areameasureproject.entity.LatLngAdapter;
import com.example.areameasureproject.entity.Measurement;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


public class MeasurementProvider {

//    private Polygon polygon;
//    private GoogleMap mMap;
//    private List<LatLng> coordinates;
//
//    public MeasurementProvider(Polygon polygon, GoogleMap mMap, List<LatLng> coordinates) {
//        this.polygon = polygon;
//        this.mMap = mMap;
//        this.coordinates = coordinates;
//    }
//
//    public void drawPolygon() {
//        polygon = mMap.addPolygon(new PolygonOptions()
//                .clickable(false)
//                .strokeColor(0xAA0000FF)
//                .fillColor(0x660000FF)
//                .addAll(coordinates));
//    }
//
//    public void removePolygon() {
//        if (!Objects.isNull(polygon)) {
//            polygon.remove();
//        }
//    }
//
//    public void drawMeasurement(Measurement measurement) {
//        List<LatLng> latLngs = measurement.getCoordinates().stream()
//                .map(latLngAdapter -> new LatLng(latLngAdapter.getLatitude(), latLngAdapter.getLongitude()))
//                .collect(Collectors.toList());
//        mMap.addPolygon(new PolygonOptions()
//                .clickable(false)
//                .strokeColor(0xAA0000FF)
//                .fillColor(0x660000FF)
//                .addAll(latLngs));
//    }
//
//    public void clearMap() {
//        mMap.clear();
//    }
//
//    public void drawPositionMarker(Location location) {
//        MarkerOptions markerOptions = new MarkerOptions();
//        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
//        markerOptions.position(latLng);
//        markerOptions.icon(BitmapDescriptorFactory.defaultMarker());
//        mMap.addMarker(markerOptions);
//    }
//
//    public void saveMeasureResult() {
//        Measurement measurement = createMeasurementObject();
//        List<LatLngAdapter> latLngAdapterList = createLatLngAdapterList(measurement);
//        measurement.setCoordinates(latLngAdapterList);
//        //save to database
//        DatabaseManager databaseManager = DatabaseManager.getInstance(this);
//        databaseManager.addLatLngAdapters(latLngAdapterList);
//        databaseManager.addMeasurement(measurement);
//        deleteCoordinates();
//    }
//
//    public void deleteCoordinates() {
//        coordinates.clear();
//    }
//
//    public Measurement createMeasurementObject() {
//        Measurement measurement = new Measurement();
//        LocalDateTime dateTime = LocalDateTime.now();
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyy HH:mm");
//        measurement.setDate(dateTime.format(formatter));
//        measurement.setArea(new AreaProvider(coordinates).getArea());
//        return measurement;
//    }
//
//    public List<LatLngAdapter> createLatLngAdapterList(Measurement measurement) {
//        return coordinates.stream()
//                .map(latLng -> new LatLngAdapter(measurement, latLng.latitude, latLng.longitude))
//                .collect(Collectors.toList());
//    }
}
