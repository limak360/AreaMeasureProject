package com.example.areameasureproject;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.areameasureproject.entity.LatLngAdapter;
import com.example.areameasureproject.entity.Measurement;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RecycleViewItemActivity extends AppCompatActivity {

    private static final String TAG = "RecycleViewItemActivity";

    private TextView name;
    private TextView date;
    private TextView area;
    private TextView coordinatesValues;
    private PreferenceUtils preferenceUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycle_view_item);
        name = findViewById(R.id.tv_recycle_view_name_value);
        date = findViewById(R.id.tv_recycle_view_date_value);
        area = findViewById(R.id.tv_recycle_view_area_value);
        coordinatesValues = findViewById(R.id.tv_recycle_view_coordinates_value);
        preferenceUtils = new PreferenceUtils(this);

        fulfillMeasurementValues();
    }

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    private void fulfillMeasurementValues() {
        Measurement measurement = getMeasurement();
        this.name.setText(measurement.getName());
        this.date.setText(measurement.getDate());
        this.area.setText(preferenceUtils.formatArea(measurement.getArea()));
        setScrollViewCoordinates(measurement.getCoordinates());
    }

    private void setScrollViewCoordinates(Collection<LatLngAdapter> coordinates) {
        List<LatLngAdapter> latLngAdapters = getUniqueList(coordinates);
        StringBuilder stringBuilder = new StringBuilder();
        for (LatLngAdapter latLngAdapter : latLngAdapters) {
            stringBuilder.append("{").append(latLngAdapter.getLatitude()).append("; ").append(latLngAdapter.getLongitude()).append("}\n");
        }

        coordinatesValues.setText(stringBuilder);
    }

    private List<LatLngAdapter> getUniqueList(Collection<LatLngAdapter> coordinates) {
        List<LatLngAdapter> latLngAdapters = new ArrayList<>(coordinates);
        if (!latLngAdapters.isEmpty()) {
            latLngAdapters.remove(latLngAdapters.get(latLngAdapters.size() - 1));
        }
        return latLngAdapters;
    }

    public void clickBackArrow(View v) {
        finish();
    }

    public void clickLoadMeasurement(View v) {
        Intent intent = new Intent(v.getContext(), MapActivity.class);
        intent.putExtras(prepareIntentExtras());
        v.getContext().startActivity(intent);
    }

    private Bundle prepareIntentExtras() {
        Bundle bundle = new Bundle();
        Measurement measurement = getMeasurement();
        List<LatLngAdapter> latLngAdapterList = new ArrayList<>(measurement.getCoordinates());
        bundle.putSerializable("MeasurementCoordinates", (Serializable) latLngAdapterList);
        return bundle;
    }

    private Measurement getMeasurement() {
        Intent intent = getIntent();
        return (Measurement) intent.getSerializableExtra("measurement");
    }

    public void clickExportMeasurement(View v) {
        Measurement measurement = getMeasurement();
        Collection<LatLngAdapter> locations = measurement.getCoordinates();
        writeFile(locations);
    }

    public void writeFile(Collection<LatLngAdapter> locations) {
        if (isExternalStorageWritable() && checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            String name = getMeasurement().getName();
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), name + ".gpx");
            new GPXGenerator(getApplicationContext()).generateGFX(file, name, locations);
        } else {
            showPermissionAlert();
        }
    }

    public boolean isExternalStorageWritable() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    public boolean checkPermission(String permission) {
        int check = ContextCompat.checkSelfPermission(this, permission);
        return (check == PackageManager.PERMISSION_GRANTED);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: ");
        if (requestCode == 123) {
            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                showPermissionAlert();
            } else {
                ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
        }
    }

    private void showPermissionAlert() {
        Log.d(TAG, "showPermissionAlert: ");
        if (ActivityCompat.checkSelfPermission(RecycleViewItemActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(RecycleViewItemActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 123);
        }
    }
}