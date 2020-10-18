package com.example.areameasureproject.db;

import android.annotation.SuppressLint;
import android.content.Context;

import com.example.areameasureproject.entity.Measurement;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.List;

public class DatabaseManager {

    @SuppressLint("StaticFieldLeak")
    private static DatabaseManager INSTANCE;

    private Dao<Measurement, Long> simpleDao;
    private DatabaseHelper databaseHelper;
    private final Context mContext;

    private DatabaseManager(Context mContext) {
        this.mContext = mContext;
        databaseHelper = OpenHelperManager.getHelper(mContext, DatabaseHelper.class);
        simpleDao = databaseHelper.getDao();
    }

    public static DatabaseManager getInstance(Context context) {
        if (INSTANCE == null) INSTANCE = new DatabaseManager(context);
        return INSTANCE;
    }

    public void addMeasurement(Measurement measurement) {
        try {
            simpleDao.createOrUpdate(measurement);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Measurement> getAllMeasurements() {
        List<Measurement> measurements = null;
        try {
            measurements.addAll(simpleDao.queryForAll());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return measurements;
    }

    public void deleteRecord(Long id) {
        try {
            simpleDao.deleteById(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
