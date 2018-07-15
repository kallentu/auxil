package com.auxil.auxil;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.MenuItem;
import android.view.Window;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class FoodBankMapActivity extends FragmentActivity implements OnMapReadyCallback {

    private String TAG = FoodBankMapActivity.class.getSimpleName();

    private static final int FINE_LOCATION_PERMISSION_REQUEST_CODE = 1;
    private Boolean locationPermissionGranted;

    private GoogleMap map;
    private GeoDataClient geoDataClient;
    private PlaceDetectionClient placeDetectionClient;
    private FusedLocationProviderClient fusedLocationProviderClient;

    private LatLng defaultLocation = new LatLng(-34, 151);
    private Location lastKnownLocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Removes the top nav bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_food_bank_map);
        setUpBottomNavigation();

        geoDataClient = Places.getGeoDataClient(this);
        placeDetectionClient = Places.getPlaceDetectionClient(this);
        fusedLocationProviderClient =
                LocationServices.getFusedLocationProviderClient(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Sets listener for the navigation and handles click events
     */
    private void setUpBottomNavigation() {
        final BottomNavigationView bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch(item.getItemId()) {
                            case R.id.nav_map:
                                item.setEnabled(false);
                                bottomNavigationView.getMenu().getItem(1).setEnabled(true);
                                bottomNavigationView.getMenu().getItem(2).setEnabled(true);
                                break;
                            case R.id.nav_donate:
                                item.setEnabled(false);
                                bottomNavigationView.getMenu().getItem(0).setEnabled(true);
                                bottomNavigationView.getMenu().getItem(2).setEnabled(true);
                                break;
                            case R.id.nav_settings:
                                item.setEnabled(false);
                                bottomNavigationView.getMenu().getItem(0).setEnabled(true);
                                bottomNavigationView.getMenu().getItem(1).setEnabled(true);
                                break;
                        }
                        return true;
                    }
                }
        );
    }

    /**
     * Sets up map when GoogleMap is available
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        // Prompt the user for permission.
        getLocationPermission();

        // Sets location controls on map
        updateLocationUI();

        // Gets current location and sets position on the map
        getDeviceLocation();

        // TODO: Iterate through all place information and add correct positions + titles
        // Adds all place markers on the map
        addMarkers(defaultLocation, "PLACEHOLDER_TITLE");
    }

    /**
     * Uses position and marker title to set marker on map
     * and move to the area of the marker
     */
    private void addMarkers(LatLng position, String title) {
        map.addMarker(new MarkerOptions().position(position).title(title));
        map.moveCamera(CameraUpdateFactory.newLatLng(position));
    }

    /**
     * Requests runtime permissions for user location access
     */
    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        }
        else {
            ActivityCompat.requestPermissions(
                    this,
                    new String[] {Manifest.permission.ACCESS_FINE_LOCATION},
                    FINE_LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    /**
     * Callback to handle results of permission request
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        locationPermissionGranted = false;
        switch (requestCode) {
            case FINE_LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationPermissionGranted = true;
                }
            }
        }

        updateLocationUI();
    }

    /**
     * Sets location controls on the map, depending on user permissions
     */
    private void updateLocationUI() {
        if (map == null) {
            return;
        }

        try {
            if (locationPermissionGranted) {
                map.setMyLocationEnabled(true);
                map.getUiSettings().setMyLocationButtonEnabled(true);
            }
            else {
                map.setMyLocationEnabled(false);
                map.getUiSettings().setMyLocationButtonEnabled(false);
                lastKnownLocation = null;
                getLocationPermission();
            }
        }
        catch (SecurityException e)  {
            Log.e("SecurityException: %s", e.getMessage());
        }
    }

    /**
     * Get the best and most recent location of the device, which may be null in rare
     * cases when a location is not available.
     */
    private void getDeviceLocation() {

        try {
            if (locationPermissionGranted) {
                Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set camera position to current location
                            lastKnownLocation = task.getResult();
                            map.moveCamera(CameraUpdateFactory.newLatLng(
                                    new LatLng(lastKnownLocation.getLatitude(),
                                               lastKnownLocation.getLongitude())));
                        }
                        else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Task Exception: %s", task.getException());
                            map.moveCamera(CameraUpdateFactory.newLatLng(defaultLocation));
                            map.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        }
        catch (SecurityException e)  {
            Log.e("SecurityException: %s", e.getMessage());
        }
    }
}
