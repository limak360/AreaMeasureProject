package com.example.areameasureproject;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.tasks.Task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
    private Polygon polygon;

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
    ImageView stopMeasurement;
    ImageView discardMeasurement;
    ImageView dropPin;

    public void clickStartMeasurement(View view) {
        clearMap();
        startMeasurement.setVisibility(View.INVISIBLE);
        stopMeasurement.setVisibility(View.VISIBLE);
        discardMeasurement.setVisibility(View.VISIBLE);
        dropPin.setVisibility(View.VISIBLE);

        dropPin.setOnClickListener(v -> {
            drawPositionMarker(currentLocation);
            coordinates.add(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()));
            removePolygon();
            drawPolygon();
        });
    }

    public void clickStopMeasurement(View view) {
        startMeasurement.setVisibility(View.VISIBLE);
        stopMeasurement.setVisibility(View.INVISIBLE);
        discardMeasurement.setVisibility(View.INVISIBLE);
        dropPin.setVisibility(View.INVISIBLE);
        if (!coordinates.isEmpty()) {
            coordinates.add(coordinates.get(0));
        }
        saveMeasureResult();
    }

    public void clickDiscardMeasurement(View view) {
        clearMap();
        deleteCoordinates();
        startMeasurement.setVisibility(View.VISIBLE);
        stopMeasurement.setVisibility(View.INVISIBLE);
        discardMeasurement.setVisibility(View.INVISIBLE);
        dropPin.setVisibility(View.INVISIBLE);
    }

    private void drawPolygon() {
        polygon = mMap.addPolygon(new PolygonOptions()
                .clickable(false)
                .strokeColor(0xAA0000FF)
                .fillColor(0x660000FF)
                .addAll(coordinates));
    }

    private void removePolygon() {
        if (!Objects.isNull(polygon)) {
            polygon.remove();
        }
    }

    private void drawPolygonIfAvailable() {
        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        try {
            List<LatLngAdapter> latLngAdapters = (List<LatLngAdapter>) bundle.getSerializable("MeasurementCoordinates");
            List<LatLng> latLngs = latLngAdapters.stream()
                    .map(latLngAdapter -> new LatLng(latLngAdapter.getLatitude(), latLngAdapter.getLongitude()))
                    .collect(Collectors.toList());

            polygon = mMap.addPolygon(new PolygonOptions()
                    .clickable(false)
                    .strokeColor(0xAA0000FF)
                    .fillColor(0x660000FF)
                    .addAll(latLngs));
        } catch (Exception ignore) {
        }
    }

    private void clearMap() {
        mMap.clear();
    }

    private void drawPositionMarker(Location location) {
        MarkerOptions markerOptions = new MarkerOptions();
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        markerOptions.position(latLng);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker());
        mMap.addMarker(markerOptions);
    }

    private void saveMeasureResult() {
        Measurement measurement = createMeasurementObject();
        List<LatLngAdapter> latLngAdapterList = createLatLngAdapterList(measurement);
        measurement.setCoordinates(latLngAdapterList);
        //save to database
        DatabaseManager databaseManager = DatabaseManager.getInstance(this);
        databaseManager.addLatLngAdapters(latLngAdapterList);
        databaseManager.addMeasurement(measurement);
        deleteCoordinates();
    }

    private void deleteCoordinates() {
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
        drawerLayout = findViewById(R.id.drawer_layout);
        startMeasurement = findViewById(R.id.start_measurement);
        stopMeasurement = findViewById(R.id.finish_measurement);
        discardMeasurement = findViewById(R.id.discard_measurement);
        dropPin = findViewById(R.id.add_marker);
        mContext = getApplicationContext();
        coordinates = new ArrayList<>();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                Location lastLocation = locationResult.getLastLocation();
                if (lastLocation != null) {
                    currentLocation = lastLocation;
                }
            }
        };
        mLocationRequest = createLocationRequest();
        builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        checkLocationSetting(builder);

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
        getLastKnownLocation();
        drawPolygonIfAvailable();

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
        mLocationRequest.setInterval(10000);
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
//                AlertDialog.Builder builder1 = new AlertDialog.Builder(mContext);
//                builder1.setTitle("Continuous Location Request");
//                builder1.setMessage("This request is essential to get location update continuously");
//                builder1.create();
//                builder1.setPositiveButton("OK", (dialog, which) -> {
//                    ResolvableApiException resolvable = (ResolvableApiException) e;
//                    try {
//                        resolvable.startResolutionForResult(MapActivity.this,
//                                REQUEST_CHECK_SETTINGS);
//                    } catch (IntentSender.SendIntentException e1) {
//                        e1.printStackTrace();
//                    }
//                });
//                builder1.setNegativeButton("Cancel", (dialog, which) ->
//                        Toast.makeText(mContext, "Location update permission not granted", Toast.LENGTH_LONG).show());
//                builder1.show();
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

    private void getLastKnownLocation() {
        Log.e(TAG, "getLastKnownLocation:");
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            showPermissionAlert();
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        Log.d(TAG, "getLastKnownLocation: locationSuccess");
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), ZOOM));
                    }
                });
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