package com.example.areameasureproject;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.areameasureproject.db.DatabaseManager;
import com.example.areameasureproject.entity.Measurement;
import com.example.areameasureproject.measure.LatLngAdapter;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import static com.example.areameasureproject.MainActivity.closeDrawer;
import static com.example.areameasureproject.MainActivity.openDrawer;
import static com.example.areameasureproject.MainActivity.redirectActivity;

public class MeasurementListActivity extends AppCompatActivity {

    private static final String TAG = "MeasurementListActivity";

    private List<Measurement> measurementsList;
    private DrawerLayout drawerLayout;

    @Override
    //todo  przetestowac dodawanie i pobieranie z bazy danych na sztywno na razie
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measurement_list);
        measurementsList = new ArrayList<>();
        drawerLayout = findViewById(R.id.drawer_layout);

        prepareObjects();
    }

    public void ClickMenu(View view) {
        openDrawer(drawerLayout);
    }

    public void ClickLogo(View view) {
        closeDrawer(drawerLayout);
    }

    public void ClickMap(View view) {
        redirectActivity(this, MapActivity.class);
    }

    public void ClickSavedMeasurements(View view) {
        redirectActivity(this, MeasurementListActivity.class);
    }

    public void ClickSettings(View view) {
        redirectActivity(this, SettingsActivity.class);
    }

    public void ClickAboutMe(View view) {
        redirectActivity(this, AboutMeActivity.class);
    }

    public void prepareObjects() {
        LatLng latLng = new LatLng(222.5, 333.67);
        LatLng latLng1 = new LatLng(89.43, 21.66);
        LatLng latLng2 = new LatLng(655.12, 56.98);

        List<LatLngAdapter> latLngAdapters = new ArrayList<>();
        latLngAdapters.add(new LatLngAdapter(latLng.latitude, latLng.longitude));
        latLngAdapters.add(new LatLngAdapter(latLng1.latitude, latLng1.longitude));
        latLngAdapters.add(new LatLngAdapter(latLng2.latitude, latLng2.longitude));
        Measurement measurement1 = new Measurement();
        measurement1.setDate("12/05/2020");
        measurement1.setCoordinates(latLngAdapters);
        measurement1.setArea(40.331);

        DatabaseManager databaseManager = DatabaseManager.getInstance(this);
        databaseManager.addMeasurement(measurement1);
        databaseManager.addMeasurement(measurement1);

        measurementsList = databaseManager.getAllMeasurements();// niedziala

        initRecyclerView();
    }

    private void initRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter(this, measurementsList);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}