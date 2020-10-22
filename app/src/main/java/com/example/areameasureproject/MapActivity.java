package com.example.areameasureproject;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.areameasureproject.db.DatabaseManager;
import com.example.areameasureproject.entity.LatLngAdapter;
import com.example.areameasureproject.entity.Measurement;
import com.example.areameasureproject.measure.AreaProvider;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.tasks.Task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.areameasureproject.MainActivity.closeDrawer;
import static com.example.areameasureproject.MainActivity.openDrawer;
import static com.example.areameasureproject.MainActivity.redirectActivity;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {
    //TODO
    private static final String TAG = "MapActivity";

    private static final int REQUEST_CHECK_SETTINGS = 102;
    private static final float ZOOM = 20f;

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;
    private LocationSettingsRequest.Builder builder;
    private Location currentLocation;

    private DrawerLayout drawerLayout;
    private Context mContext;
    private List<LatLng> coordinates;

    public void clickMenu(View view) {
        openDrawer(drawerLayout);
    }

    public void clickLogo(View view) {
        closeDrawer(drawerLayout);
    }

    public void clickMap(View view) {
        closeDrawer(drawerLayout);
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

    //przeniesc do innej klasy
    ImageView startMeasurement;
    ImageView dropPin;
    ImageView stopMeasurement;

    public void clickStartMeasurement(View view) {
        removePolygon();
        startMeasurement = findViewById(R.id.start_measurement);
        stopMeasurement = findViewById(R.id.finish_measurement);
        dropPin = findViewById(R.id.add_marker);
        startMeasurement.setVisibility(View.INVISIBLE);
        dropPin.setVisibility(View.VISIBLE);
        stopMeasurement.setVisibility(View.VISIBLE);
        dropPin.setOnClickListener(v -> {
            drawPositionMarker(currentLocation);
            coordinates.add(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()));
            drawPolygon();
        });
    }

    public void clickStopMeasurement(View view) {
        stopMeasurement.setVisibility(View.INVISIBLE);
        dropPin.setVisibility(View.INVISIBLE);
        startMeasurement.setVisibility(View.VISIBLE);
        if (!coordinates.isEmpty()) {
            coordinates.add(coordinates.get(0));
            drawPolygon();
        }
        saveMeasureResult();
    }

    private void drawPolygon() { //TODO stylizacja
        mMap.addPolygon(new PolygonOptions()
                .clickable(false)
                .fillColor(Color.GREEN)
                .addAll(coordinates));
    }

    private void removePolygon() {
        mMap.clear();
    }

    private void saveMeasureResult() {
        Measurement measurement = createMeasurementObject();
        List<LatLngAdapter> latLngAdapterList = createLatLngAdapterList(measurement);
        measurement.setCoordinates(latLngAdapterList);
        //save to database
        DatabaseManager databaseManager = DatabaseManager.getInstance(this);
        databaseManager.addLatLngAdapters(latLngAdapterList);
        databaseManager.addMeasurement(measurement);
        coordinates.clear();
    }

    private Measurement createMeasurementObject() {
        Measurement measurement = new Measurement();
        LocalDateTime dateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyy HH:mm");
        measurement.setDate(dateTime.format(formatter));
        measurement.setArea(new AreaProvider(coordinates).getArea());
        return measurement;
    }

    private List<LatLngAdapter> createLatLngAdapterList(Measurement measurement) {
        return coordinates.stream()
                .map(latLng -> new LatLngAdapter(measurement, latLng.latitude, latLng.longitude))
                .collect(Collectors.toList());
    }
//

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        mContext = getApplicationContext();
        drawerLayout = findViewById(R.id.drawer_layout);
        coordinates = new ArrayList<>();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        mLocationRequest = createLocationRequest();
        builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        checkLocationSetting(builder); //na tym etapie powinny byc rozwiazane permisions wstepnie

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                Location lastLocation = locationResult.getLastLocation();
                if (lastLocation != null) {
                    currentLocation = lastLocation;
                }
            }
        };
        initMap();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startLocationUpdates();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady: map is ready");
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
    }

    protected LocationRequest createLocationRequest() {
        Log.d(TAG, "createLocationRequest: ");
        LocationRequest mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return mLocationRequest;
    }

    private void checkLocationSetting(LocationSettingsRequest.Builder builder) {
        Log.d(TAG, "checkLocationSetting: ");
        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnSuccessListener(this, locationSettingsResponse -> startLocationUpdates());
        task.addOnFailureListener(this, e -> {
            if (e instanceof ResolvableApiException) {
                try {
                    ResolvableApiException resolvable = (ResolvableApiException) e;
                    resolvable.startResolutionForResult(MapActivity.this,
                            REQUEST_CHECK_SETTINGS);
                } catch (IntentSender.SendIntentException ignored) {
                }
                AlertDialog.Builder builder1 = new AlertDialog.Builder(mContext);
                builder1.setTitle("Continuous Location Request");
                builder1.setMessage("This request is essential to get location update continuously");
                builder1.create();
                builder1.setPositiveButton("OK", (dialog, which) -> {
                    ResolvableApiException resolvable = (ResolvableApiException) e;
                    try {
                        resolvable.startResolutionForResult(MapActivity.this,
                                REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException e1) {
                        e1.printStackTrace();
                    }
                });
                builder1.setNegativeButton("Cancel", (dialog, which) ->
                        Toast.makeText(mContext, "Location update permission not granted", Toast.LENGTH_LONG).show());
                builder1.show();
            }
        });
    }

    public void startLocationUpdates() {
        Log.e(TAG, "startLocationUpdates:");
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.requestLocationUpdates(
                mLocationRequest,
                mLocationCallback,
                null);
    }

    private void stopLocationUpdates() {
        Log.e(TAG, "stopLocationUpdates: ");
        fusedLocationClient.removeLocationUpdates(mLocationCallback);
    }

    private void initMap() {
        Log.d(TAG, "initMap: initializing map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(MapActivity.this);
        }
    }

    private void drawPositionMarker(Location location) {
        Log.d(TAG, "moveCamera: moving the camera to: lat: " + location.getLatitude() + ", lng: " + location.getLongitude());
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker());
        mMap.addMarker(markerOptions);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Log.d(TAG, "onActivityResult: ");
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            if (resultCode == RESULT_OK) {
                startLocationUpdates();
            } else {
                checkLocationSetting(builder);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: ");
        if (requestCode == 123) {
            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                showPermissionAlert();
            } else {
                ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION);
            }
        }
    }

    private void showPermissionAlert() {
        Log.d(TAG, "showPermissionAlert: ");
        if (ActivityCompat.checkSelfPermission(MapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MapActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 123);
        }
    }
}