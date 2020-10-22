package com.example.areameasureproject;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.areameasureproject.db.DatabaseManager;
import com.example.areameasureproject.entity.Measurement;

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

    public void clickMenu(View view) {
        openDrawer(drawerLayout);
    }

    public void clickLogo(View view) {
        closeDrawer(drawerLayout);
    }

    public void clickMap(View view) {
        redirectActivity(this, MapActivity.class);
    }

    public void clickSavedMeasurements(View view) {
        redirectActivity(this, MeasurementListActivity.class);
    }

    public void clickSettings(View view) {
        redirectActivity(this, SettingsActivity.class);
    }

    public void clickAboutMe(View view) {
        redirectActivity(this, AboutMeActivity.class);
    }

    public void prepareObjects() {
        DatabaseManager databaseManager = DatabaseManager.getInstance(this);
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