package com.example.areameasureproject;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.areameasureproject.db.DatabaseManager;
import com.example.areameasureproject.entity.LatLngAdapter;
import com.example.areameasureproject.entity.Measurement;
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
        drawerLayout = findViewById(R.id.drawer_layout);
        measurementsList = new ArrayList<>();

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
        DatabaseManager databaseManager = DatabaseManager.getInstance(this);
        Measurement measurement1 = new Measurement();
        measurement1.setDate("12/05/2020");
        measurement1.setArea(40.331);
        List<LatLngAdapter> latLngAdapters = new ArrayList<>();
        measurement1.setCoordinates(latLngAdapters);
        latLngAdapters.add(new LatLngAdapter(measurement1, new LatLng(58.344, 3.67).latitude, new LatLng(22.5, 75.67).longitude));

        databaseManager.addLatLngAdapters(latLngAdapters);
        databaseManager.addMeasurement(measurement1);

        Measurement measurement2 =  new Measurement();
        measurement2.setDate("08/05/2019");
        measurement2.setArea(17000.33);
        List<LatLngAdapter> latLngAdapters2 = new ArrayList<>();
        latLngAdapters2.add(new LatLngAdapter(measurement2, new LatLng(18.2, 3.67).latitude, new LatLng(22.5, 11.4).longitude));
        latLngAdapters2.add(new LatLngAdapter(measurement2, new LatLng(41.78, 3.67).latitude, new LatLng(22.5, 9.577).longitude));
        measurement2.setCoordinates(latLngAdapters2);

        databaseManager.addLatLngAdapters(latLngAdapters2);
        databaseManager.addMeasurement(measurement2);

        Measurement measurement3 =  new Measurement();
        measurement3.setDate("01/02/2020");
        measurement3.setArea(111.222);
        List<LatLngAdapter> latLngAdapters3 = new ArrayList<>();
        latLngAdapters3.add(new LatLngAdapter(measurement3, new LatLng(1.2, 3.67).latitude, new LatLng(22.5, 1.4).longitude));
        measurement3.setCoordinates(latLngAdapters3);

        databaseManager.addLatLngAdapters(latLngAdapters3);
        databaseManager.addMeasurement(measurement3);

        measurementsList = databaseManager.getAllMeasurements();

        initRecyclerView();
    }

    private void initRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter(this, measurementsList);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}