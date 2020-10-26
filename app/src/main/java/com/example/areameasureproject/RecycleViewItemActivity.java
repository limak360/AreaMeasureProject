package com.example.areameasureproject;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycle_view_item);
        name = findViewById(R.id.tv_recycle_view_name_value);
        date = findViewById(R.id.tv_recycle_view_date_value);
        area = findViewById(R.id.tv_recycle_view_area_value);
        coordinatesValues = findViewById(R.id.tv_recycle_view_coordinates_value);

        fulfillMeasurementValues();
    }

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    private void fulfillMeasurementValues() {
        Measurement measurement = getMeasurement();
        this.name.setText(measurement.getName());
        this.date.setText(measurement.getDate());
        String formattedArea = String.format("%.3f", measurement.getArea());
        this.area.setText(formattedArea + " m²");
        setScrollViewCoordinates(measurement.getCoordinates());
    }

    private void setScrollViewCoordinates(Collection<LatLngAdapter> coordinates) {
        StringBuilder stringBuilder = new StringBuilder();
        for (LatLngAdapter latLngAdapter : coordinates) {
            stringBuilder.append("{").append(latLngAdapter.getLatitude()).append("; ").append(latLngAdapter.getLongitude()).append("}\n");
        }
        coordinatesValues.setText(stringBuilder);
    }

    public void clickBackArrow(View v) {
        finish();
    }

    public void clickLoadMeasurement(View v) {
        Intent intent = new Intent(v.getContext(), MapActivity.class);
        intent.putExtras(prepareIntentExtras());
        v.getContext().startActivity(intent); //TODO Activity Result Launcher ??? startActivityForResult
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
        List<Location> locations = createLocationList(measurement.getCoordinates());
        writeFile(locations);
    }

    private List<Location> createLocationList(Collection<LatLngAdapter> coordinates) {
        List<Location> locations = new ArrayList<>();
        for (LatLngAdapter latLngAdapter : coordinates) {
            Location location = new Location("");
            location.setLatitude(latLngAdapter.getLatitude());
            location.setLongitude(latLngAdapter.getLongitude());

            locations.add(location);
        }
        return locations;
    }

    public void writeFile(List<Location> locations) {
        if (isExternalStorageWritable() && checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "measurement.gpx");
            new GPXGenerator(getApplicationContext()).generateGFX(file, getMeasurement().getName(), locations);
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