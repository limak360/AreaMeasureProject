package com.example.areameasureproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;


public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String MEASUREMENT_TABLE = "MEASUREMENT_TABLE";
    public static final String COLUMN_MEASUREMENT_DATE = "DATE";
    public static final String COLUMN_MEASUREMENT_COORDINATES = "COORDINATES";
    public static final String COLUMN_MEASUREMENT_AREA = "AREA";

    public DatabaseHelper(@Nullable Context context) {
        super(context, "PolygonsDB", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {// data i godzina, koordynaty, pole powierzchni
        String createTableStatement = "CREATE TABLE "
                + MEASUREMENT_TABLE + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_MEASUREMENT_DATE + " TEXT, "
                + COLUMN_MEASUREMENT_COORDINATES + " TEXT, "
                + COLUMN_MEASUREMENT_AREA + " REAL)";
        db.execSQL(createTableStatement);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public boolean addMeasurement(Measurement measurement) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_MEASUREMENT_DATE, measurement.getDate());
        cv.put(COLUMN_MEASUREMENT_COORDINATES, measurement.getCoordinates());
        cv.put(COLUMN_MEASUREMENT_AREA, measurement.getArea());

        long insertResult = db.insert(MEASUREMENT_TABLE, null, cv);
        return insertResult != -1;

        return true;
    }
    //TODO serializacja scrolView zrobic z menu kontekstowym i moze menu boczne w mainActivity ?
    public List<Measurement> getAllMeasurements() {
        List<Measurement> measurements = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        String queryString = "SELECT * FROM " + MEASUREMENT_TABLE;
        Cursor cursor = db.rawQuery(queryString, null);

        return measurements;
    }

}
