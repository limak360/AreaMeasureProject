package com.example.areameasureproject.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.areameasureproject.entity.LatLngAdapter;
import com.example.areameasureproject.entity.Measurement;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;


public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    private static final String TAG = "DatabaseHelper";

    private Dao<Measurement, Long> measurementDao;
    private Dao<LatLngAdapter, Long> latLngAdapterDao;

    public static final String DATABASE_NAME = "PolygonsDB";
    public static final int DATABASE_VERSION = 1;

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        Log.d(TAG, "onCreate: ");
        try {
            TableUtils.createTable(connectionSource, Measurement.class);
            TableUtils.createTable(connectionSource, LatLngAdapter.class);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        Log.d(TAG, "onUpgrade: ");
    }

    public Dao<Measurement, Long> getMeasurementDao() {
        Log.d(TAG, "getDao: measurement ");
        if (measurementDao == null) {
            try {
                measurementDao = getDao(Measurement.class);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return measurementDao;
    }

    public Dao<LatLngAdapter, Long> getLatLngAdapterDao() {
        Log.d(TAG, "getDao: latlng");
        if (latLngAdapterDao == null) {
            try {
                latLngAdapterDao = getDao(LatLngAdapter.class);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return latLngAdapterDao;
    }
}
