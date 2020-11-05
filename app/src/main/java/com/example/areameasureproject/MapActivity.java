package com.example.areameasureproject;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

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
    private PopupWindow popupWindow;
    private List<LatLngAdapter> coordinates;

    private ImageView startMeasurement;
    private ImageView stopMeasurement;
    private ImageView discardMeasurement;
    private ImageView dropPin;
    private EditText editTextMeasureName;

    private PreferenceUtils preferenceUtils;
    private MapDrawUtils mapDrawUtils;

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
        redirectActivity(this, AppInfoActivity.class);
    }

    public void clickStartMeasurement(View view) {
        deleteCoordinates();
        mapDrawUtils.clearMap();
        setMeasurementOptionsVisibility(View.INVISIBLE, View.VISIBLE);

        dropPin.setOnClickListener(v -> {
            mapDrawUtils.drawPositionMarker();
            addCoordinates();
            mapDrawUtils.removePolygon();
            mapDrawUtils.drawPolygon();
        });
    }

    public void clickStopMeasurement(View view) {
        setMeasurementOptionsVisibility(View.VISIBLE, View.INVISIBLE);
        if (coordinates.size() >= 3) {
            coordinates.add(coordinates.get(0));
            showPopUpWindow(view);
        } else {
            Toast.makeText(this, "To measure area you need at least 3 points.", Toast.LENGTH_SHORT).show();
            deleteCoordinates();
        }
    }

    public void clickDiscardMeasurement(View view) {
        mapDrawUtils.clearMap();
        deleteCoordinates();
        setMeasurementOptionsVisibility(View.VISIBLE, View.INVISIBLE);
    }

    private void setMeasurementOptionsVisibility(int invisible, int visible) {
        startMeasurement.setVisibility(invisible);
        stopMeasurement.setVisibility(visible);
        discardMeasurement.setVisibility(visible);
        dropPin.setVisibility(visible);
    }

    public void showPopUpWindow(View view) {
        LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        @SuppressLint("InflateParams") View popupView = inflater.inflate(R.layout.popup_window, null);
        editTextMeasureName = popupView.findViewById(R.id.editTextMeasureName);
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true;
        popupWindow = new PopupWindow(popupView, width, height, focusable);
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, -200);
    }

    private void dismissPopUpWindow() {
        popupWindow.dismiss();
    }

    public void clickSave(View view) {
        saveMeasureResult();
        dismissPopUpWindow();
        deleteCoordinates();
    }

    public void clickCancel(View view) {
        dismissPopUpWindow();
        deleteCoordinates();
    }

    private void saveMeasureResult() {
        Measurement measurement = createMeasurementObject();
        List<LatLngAdapter> latLngAdapterList = createLatLngAdapterObjectList(measurement);
        measurement.setCoordinates(latLngAdapterList);

        saveObjectsToDatabase(measurement, latLngAdapterList);
        deleteCoordinates();
    }

    private List<LatLngAdapter> createLatLngAdapterObjectList(Measurement measurement) {
        return coordinates.stream()
                .map(latLng -> new LatLngAdapter(measurement, latLng.getLatitude(), latLng.getLongitude(), latLng.getTime()))
                .collect(Collectors.toList());
    }

    private Measurement createMeasurementObject() {
        LocalDateTime dateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyy HH:mm");
        return new Measurement.Builder()
                .name(editTextMeasureName.getText().toString())
                .date(dateTime.format(formatter))
                .area(new AreaProvider(coordinates).getArea())
                .build();
    }

    private void saveObjectsToDatabase(Measurement measurement, List<LatLngAdapter> latLngAdapterList) {
        DatabaseManager databaseManager = DatabaseManager.getInstance(this);
        databaseManager.addLatLngAdapters(latLngAdapterList);
        databaseManager.addMeasurement(measurement);
    }

    private void addCoordinates() {
        coordinates.add(new LatLngAdapter(currentLocation.getLatitude(), currentLocation.getLongitude(), currentLocation.getTime()));
    }

    private void deleteCoordinates() {
        coordinates.clear();
    }

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

        coordinates = new ArrayList<>();
        preferenceUtils = new PreferenceUtils(this);
        mapDrawUtils = new MapDrawUtils();

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        mLocationRequest = createLocationRequest();
        builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        checkLocationSetting(builder);
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                Location lastLocation = locationResult.getLastLocation();
                if (lastLocation != null) {
                    currentLocation = lastLocation;
                }
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        startLocationUpdates();

        initMap();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady: map is ready");
        getLastKnownLocation();
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(false);

        mapDrawUtils.drawPolygonIfAvailable();
    }

    protected LocationRequest createLocationRequest() {
        Log.d(TAG, "createLocationRequest: ");
        LocationRequest mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(preferenceUtils.getUpdatesPref());
        mLocationRequest.setPriority(preferenceUtils.getPriorityPref());
        return mLocationRequest;
    }

    private void checkLocationSetting(LocationSettingsRequest.Builder builder) {
        Log.d(TAG, "checkLocationSetting: ");
        showPermissionAlert();

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
                != PackageManager.PERMISSION_GRANTED) {
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

    private class MapDrawUtils {

        private Polygon polygon;

        private void drawPolygon() {
            addPolygonToMap(getLatLngList(coordinates));
        }

        private void removePolygon() {
            if (!Objects.isNull(polygon)) {
                polygon.remove();
            }
        }

        private void drawPolygonIfAvailable() {
            Bundle bundle = getIntentExtras();
            try {
                List<LatLngAdapter> latLngAdapters = (List<LatLngAdapter>) bundle.getSerializable("MeasurementCoordinates");
                addPolygonToMap(getLatLngList(latLngAdapters));
                drawPositionMarkers(latLngAdapters);
            } catch (Exception ignore) {
            }
        }

        private List<LatLng> getLatLngList(List<LatLngAdapter> latLngAdapters) {
            return latLngAdapters.stream()
                    .map(latLngAdapter -> new LatLng(latLngAdapter.getLatitude(), latLngAdapter.getLongitude()))
                    .collect(Collectors.toList());
        }

        private Bundle getIntentExtras() {
            Intent intent = getIntent();
            return intent.getExtras();
        }

        private void addPolygonToMap(List<LatLng> latLngs) {
            polygon = mMap.addPolygon(new PolygonOptions()
                    .clickable(false)
                    .strokeColor(0xAA0000FF)
                    .fillColor(0x660000FF)
                    .addAll(latLngs));
        }

        private void drawPositionMarkers(List<LatLngAdapter> latLngAdapters) {
            for (LatLng latLng : getLatLngList(latLngAdapters)) {
                mMap.addMarker(getMarkerOptions(latLng));
            }
        }

        private void drawPositionMarker() {
            LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
            mMap.addMarker(getMarkerOptions(latLng));
        }

        private MarkerOptions getMarkerOptions(LatLng latLng) {
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker());
            markerOptions.title(latLng.toString());
            return markerOptions;
        }

        private void clearMap() {
            mMap.clear();
        }
    }
}