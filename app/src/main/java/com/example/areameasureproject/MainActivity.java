package com.example.areameasureproject;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class MainActivity extends AppCompatActivity implements LocationListener {

    private static final int REQUEST_CODE_PERMISSION = 1;
    private static final String TAG = "MainActivity";
    String[] mPermission = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};

    private TextView exitBtn;
    private LocationManager manager;
    private String locationProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        manager = (LocationManager) getSystemService(LOCATION_SERVICE);
        exitBtn = (TextView) findViewById(R.id.exitTextView);

        Criteria kryteria = new Criteria();
        locationProvider = manager.getBestProvider(kryteria, true);

        //wyswietl("\nTwoja Lokalizacja:");

        assert locationProvider != null;
        @SuppressLint("MissingPermission") Location location = manager.getLastKnownLocation(locationProvider);
//        wyrzucLokacje(location);

        wyswietl("\nTwoja Lokalizacja:");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {
            return;
        }
        manager.requestLocationUpdates(locationProvider, 150, 1, this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        manager.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        wyrzucLokacje(location);
    }

    private void wyswietl(String string) {
        exitBtn.append(string);
    }

    private void wyrzucLokacje(Location location) {
        if (location == null)
            wyswietl("\nLokacja niedostepna");
        else
            wyswietl("\n" + location.toString());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionResult: called.");

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        }
    }

}