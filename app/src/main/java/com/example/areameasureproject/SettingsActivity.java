package com.example.areameasureproject;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceScreen;

import static com.example.areameasureproject.MainActivity.closeDrawer;
import static com.example.areameasureproject.MainActivity.openDrawer;
import static com.example.areameasureproject.MainActivity.redirectActivity;

public class SettingsActivity extends AppCompatActivity{

    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        drawerLayout = findViewById(R.id.drawer_layout);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);

            Preference systemSettingsPreference = findPreference("systemSettingsPreference");
            systemSettingsPreference.setOnPreferenceClickListener(preference -> {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.fromParts("package", "com.example.areameasureproject", null));
                startActivity(intent);
                return false;
            });
        }
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