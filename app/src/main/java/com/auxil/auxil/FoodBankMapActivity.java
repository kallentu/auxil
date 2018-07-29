package com.auxil.auxil;

import android.Manifest;
import android.content.Intent;
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
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.auxil.auxil.mapmarker.MapWrapperLayout;
import com.auxil.auxil.mapmarker.OnInfoWindowTouchListener;
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class FoodBankMapActivity extends FragmentActivity implements OnMapReadyCallback {

    private String TAG = FoodBankMapActivity.class.getSimpleName();
    private static final int NAV_MAP_INDEX = 0;
    private static final int NAV_DONATE_INDEX = 1;
    private static final int NAV_SETTINGS_INDEX = 2;
    private static final int FINE_LOCATION_PERMISSION_REQUEST_CODE = 1;
    private Boolean locationPermissionGranted;

    private GoogleMap map;
    private GeoDataClient geoDataClient;
    private PlaceDetectionClient placeDetectionClient;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private MapWrapperLayout mapWrapperLayout;

    private LatLng defaultLocation = new LatLng(-34, 151);
    private Location lastKnownLocation;

    private ViewGroup infoWindow;
    private TextView infoTitle;
    private TextView infoSnippet;
    private Button infoButton;
    private OnInfoWindowTouchListener infoButtonListener;


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
     * Sets up map when GoogleMap is available
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        // Prompt the user for permission.
        updateLocationPermission();

        // Sets location controls on map
        updateLocationUI();

        // Gets current location and sets position on the map
        updateDeviceLocation();

        setUpInfoWindow();

        // TODO: Iterate through all place information and add correct positions + titles
        // Adds all place markers on the map
        addMarkers(defaultLocation, "Feed Everyone Food Bank");
        moveCameraToMarker(defaultLocation);
    }

    /**
     * Uses position and marker title to set marker on map
     */
    public void addMarkers(LatLng position, String title) {
        // TODO: Populate with hours, and short description
        map.addMarker(new MarkerOptions()
                .title(title)
                .snippet("Czech Republic")
                .position(position));

        map.addMarker(new MarkerOptions()
                .title("Paris")
                .snippet("France")
                .position(new LatLng(48.86,2.33)));

        map.addMarker(new MarkerOptions()
                .title("London")
                .snippet("United Kingdom")
                .position(new LatLng(51.51,-0.1)));
    }

    /**
     * Uses position to move to the area of the marker
     */
    public void moveCameraToMarker(LatLng position) {
        // TODO: Instead of taking in position, take in Marker, makes more sense for fn
        map.moveCamera(CameraUpdateFactory.newLatLng(position));
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
     * Sets up the custom info windows from {@link OnInfoWindowTouchListener}
     */
    private void setUpInfoWindow() {
        mapWrapperLayout = (MapWrapperLayout)findViewById(R.id.map_relative_layout);
        this.infoWindow = (ViewGroup)getLayoutInflater().inflate(R.layout.info_window, null);
        this.infoTitle = (TextView)infoWindow.findViewById(R.id.title);
        this.infoSnippet = (TextView)infoWindow.findViewById(R.id.snippet);
        this.infoButton = (Button)infoWindow.findViewById(R.id.button);

        // Setting up button listener so it shows up
        this.infoButtonListener = new OnInfoWindowTouchListener(infoButton,
                getResources().getDrawable(R.drawable.button_info),
                getResources().getDrawable(R.drawable.button_info))
        {
            @Override
            protected void onClickConfirmed(View v, Marker marker) {
                // Here we can perform some action triggered after clicking the button
                Toast.makeText(FoodBankMapActivity.this,
                        marker.getTitle() + "'s button clicked!",
                        Toast.LENGTH_SHORT)
                        .show();
            }
        };

        this.infoButton.setOnTouchListener(infoButtonListener);

        map.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                // Setting up the infoWindow with current's marker info
                infoTitle.setText(marker.getTitle());
                infoSnippet.setText(marker.getSnippet());
                infoButtonListener.setMarker(marker);

                // We must call this to set the current marker and infoWindow references
                // to the MapWrapperLayout
                mapWrapperLayout.setMarkerWithInfoWindow(marker, infoWindow);
                return infoWindow;
            }
        });
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
                        Intent intent;
                        switch(item.getItemId()) {
                            case R.id.nav_map:
                                intent = new Intent(getApplicationContext(),
                                        FoodBankMapActivity.class);
                                startActivity(intent);

                                item.setEnabled(false);
                                bottomNavigationView.getMenu()
                                        .getItem(NAV_DONATE_INDEX).setEnabled(true);
                                bottomNavigationView.getMenu()
                                        .getItem(NAV_SETTINGS_INDEX).setEnabled(true);
                                break;
                            case R.id.nav_donate:
                                intent = new Intent(getApplicationContext(),
                                        FoodBankDonateActivity.class);
                                startActivity(intent);

                                item.setEnabled(false);
                                bottomNavigationView.getMenu()
                                        .getItem(NAV_MAP_INDEX).setEnabled(true);
                                bottomNavigationView.getMenu()
                                        .getItem(NAV_SETTINGS_INDEX).setEnabled(true);

                                break;
                            case R.id.nav_settings:
                                intent = new Intent(getApplicationContext(),
                                        SettingsActivity.class);
                                startActivity(intent);

                                item.setEnabled(false);
                                bottomNavigationView.getMenu()
                                        .getItem(NAV_MAP_INDEX).setEnabled(true);
                                bottomNavigationView.getMenu()
                                        .getItem(NAV_DONATE_INDEX).setEnabled(true);
                                break;
                        }
                        return true;
                    }
                }
        );
    }

    /**
     * Requests runtime permissions for user location access
     */
    private void updateLocationPermission() {
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
                updateLocationPermission();
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
    private void updateDeviceLocation() {
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
