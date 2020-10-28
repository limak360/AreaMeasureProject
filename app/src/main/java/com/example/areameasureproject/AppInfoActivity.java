package com.example.areameasureproject;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import static com.example.areameasureproject.MainActivity.closeDrawer;
import static com.example.areameasureproject.MainActivity.openDrawer;
import static com.example.areameasureproject.MainActivity.redirectActivity;

public class AppInfoActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_info_activity);
        drawerLayout = findViewById(R.id.drawer_layout);
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
}