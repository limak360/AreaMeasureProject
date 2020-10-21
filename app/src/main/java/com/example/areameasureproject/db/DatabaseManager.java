package com.example.areameasureproject.db;

import android.annotation.SuppressLint;
import android.content.Context;

import com.example.areameasureproject.entity.LatLngAdapter;
import com.example.areameasureproject.entity.Measurement;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {

    private static final String TAG = "DatabaseManager";

    @SuppressLint("StaticFieldLeak")
    private static DatabaseManager INSTANCE;

    private Dao<Measurement, Long> measurementDao;
    private Dao<LatLngAdapter, Long> latLngAdapterDao;

    private DatabaseManager(Context mContext) {
        DatabaseHelper databaseHelper = OpenHelperManager.getHelper(mContext, DatabaseHelper.class);
        measurementDao = databaseHelper.getMeasurementDao();
        latLngAdapterDao = databaseHelper.getLatLngAdapterDao();
    }

    public static DatabaseManager getInstance(Context context) {
        if (INSTANCE == null) INSTANCE = new DatabaseManager(context);
        return INSTANCE;
    }

    public void addLatLngAdapters(List<LatLngAdapter> latLngAdapters) {
        try {
            for (LatLngAdapter latLngAdapter : latLngAdapters) {
                latLngAdapterDao.create(latLngAdapter);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addMeasurement(Measurement measurement) {
        try {
            measurementDao.createOrUpdate(measurement);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Measurement> getAllMeasurements() {
        List<Measurement> measurements = new ArrayList<>();
        try {
            measurements.addAll(measurementDao.queryForAll());
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return measurements;
    }

    public void deleteRecord(Long id) {
        try {
            measurementDao.deleteById(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
