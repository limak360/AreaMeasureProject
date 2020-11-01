package com.example.areameasureproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

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

public class MeasurementListActivity extends AppCompatActivity implements RecyclerViewAdapter.OnMeasurementListener, RecyclerViewAdapter.OnLongMeasurementListener {

    private static final String TAG = "MeasurementListActivity";

    private List<Measurement> measurementsList;
    private DrawerLayout drawerLayout;

    @Override
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
        redirectActivity(this, AppInfoActivity.class);
    }

    public void prepareObjects() {
        DatabaseManager databaseManager = DatabaseManager.getInstance(this);
        measurementsList = databaseManager.getAllMeasurements();
        initRecyclerView();
    }

    private void initRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter(measurementsList, this, this);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void onMeasurementClick(int position) {
        Intent intent = new Intent(this, RecycleViewItemActivity.class);
        intent.putExtra("measurement", measurementsList.get(position));
        startActivity(intent);
    }

    @Override
    public void onLongMeasurementListener(int position) {
        DatabaseManager databaseManager = DatabaseManager.getInstance(this);
        databaseManager.deleteRecord(measurementsList.get(position).getId());
        measurementsList.remove(measurementsList.get(position));
    }
}