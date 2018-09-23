package com.auxil.auxil.map;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.auxil.auxil.FoodBank;
import com.auxil.auxil.donate.FoodBankDonateFragment;
import com.auxil.auxil.R;
import com.auxil.auxil.faq.FAQFragment;
import com.auxil.auxil.settings.SettingsActivity;
import com.auxil.auxil.info.FoodBankInfoFragment;
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
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;

/** Activity for Google Maps API. */
public class FoodBankMapActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {

    private String TAG = FoodBankMapActivity.class.getSimpleName();
    private static final int FINE_LOCATION_PERMISSION_REQUEST_CODE = 1;

    /** The ordering of the bottom navigation buttons. */
    private static final int NAV_MAP_INDEX = 0;
    private static final int NAV_DONATE_INDEX = 1;
    private static final int NAV_FAQ_INDEX = 2;
    private static final int NAV_SETTINGS_INDEX = 3;

    /** Firebase database references. */
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private final DatabaseReference databaseReference = database.getReference();
    private static final String FOOD_BANKS_REFERENCE = "foodbanks";

    /** Fields used for the initialization of the Google Maps API. */
    private GoogleMap map;
    private GeoDataClient geoDataClient;
    private PlaceDetectionClient placeDetectionClient;
    private FusedLocationProviderClient fusedLocationProviderClient;

    /** Fields used for location of the user in {@code updateDeviceLocation}. */
    private LatLng defaultLocation = new LatLng(-34, 151);
    private Boolean locationPermissionGranted;
    private Location lastKnownLocation;

    /** Field for the bottom navigation bar. */
    private BottomNavigationView bottomNavigationView;

    /** Fields used for the info bar, customized by {@link MapWrapperLayout}. */
    private MapWrapperLayout mapWrapperLayout;
    private ViewGroup infoWindow;
    private TextView infoTitle;
    private TextView infoSnippet;


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
        map.setOnInfoWindowClickListener(this);

        // Prompt the user for permission.
        updateLocationPermission();

        // Sets location controls on map
        updateLocationUI();

        // Gets current location and sets position on the map
        updateDeviceLocation();

        setUpInfoWindow();

        // Adds all place markers on the map
        addMarkersFromDatabase();
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        String name = marker.getTitle();
        databaseReference.child(FOOD_BANKS_REFERENCE).child(name).addValueEventListener(
                new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    try {
                        // Food bank information to send to the fragment
                        FoodBank foodBank = dataSnapshot.getValue(FoodBank.class);
                        Bundle infoBundle = new Bundle();
                        infoBundle.putSerializable("foodbank", foodBank);
//                        if (foodBank.name() != null) infoBundle.putString("name", foodBank.name());
//                        if (foodBank.address() != null) infoBundle.putString("address", foodBank.address());
//                        if (foodBank.number() != null) infoBundle.putString("number", foodBank.number());
//                        if (foodBank.website() != null) infoBundle.putString("website", foodBank.website());

                        FoodBankInfoFragment infoFragment= new FoodBankInfoFragment();
                        infoFragment.setArguments(infoBundle);

                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_switch, infoFragment)
                                .addToBackStack("Map")
                                .commit();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.e(TAG, "Food bank not found in database for info window.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

    }

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
     * Adds markers from all the food bank information in the FireBase database.
     */
    public void addMarkersFromDatabase() {
        final Geocoder geocoder = new Geocoder(getApplicationContext());

        databaseReference.child(FOOD_BANKS_REFERENCE).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                // Retrieve FoodBank class to
                FoodBank foodBank = dataSnapshot.getValue(FoodBank.class);
                assert foodBank != null;

                // Uses Geocoder to obtain coordinates from address, then adds marker to map
                try {
                    Address address = geocoder.getFromLocationName(foodBank.address(), 1).get(0);
                    map.addMarker(new MarkerOptions()
                            .title(foodBank.name())
                            .snippet(foodBank.address())
                            .position(new LatLng(address.getLatitude(), address.getLongitude())));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });
    }

    /**
     * Uses position to move to the area of the marker
     */
    public void moveCameraToMarker(LatLng position) {
        map.moveCamera(CameraUpdateFactory.newLatLng(position));
    }

    /**
     * Closes top fragment window when onClick
     */
    public void closeFragment(View view) {
        getSupportFragmentManager().popBackStack();
    }

    /**
     * Sets up the custom info windows from {@link MapWrapperLayout}
     */
    private void setUpInfoWindow() {
        mapWrapperLayout = (MapWrapperLayout)findViewById(R.id.map_relative_layout);
        this.infoWindow = (ViewGroup)getLayoutInflater().inflate(R.layout.info_window, null);
        this.infoTitle = (TextView)infoWindow.findViewById(R.id.title);
        this.infoSnippet = (TextView)infoWindow.findViewById(R.id.snippet);

        map.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                // Setting up the infoWindow with current's marker info
                infoTitle.setText(marker.getTitle());
                infoSnippet.setText(marker.getSnippet());

                // We must call this to set the current marker and infoWindow references
                // to the MapWrapperLayout
                mapWrapperLayout.initializeMap(map);
                mapWrapperLayout.setMarkerWithInfoWindow(marker, infoWindow);
                return infoWindow;
            }

            @Override
            public View getInfoContents(Marker marker) {
                return null;
            }
        });
    }

    /**
     * Sets listener for the navigation and handles click events
     */
    private void setUpBottomNavigation() {
        bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch(item.getItemId()) {
                            case R.id.nav_map:
                                getSupportFragmentManager().popBackStack(null,
                                        FragmentManager.POP_BACK_STACK_INCLUSIVE);

                                item.setEnabled(false);
                                bottomNavigationView.getMenu()
                                        .getItem(NAV_DONATE_INDEX).setEnabled(true);
                                bottomNavigationView.getMenu()
                                        .getItem(NAV_FAQ_INDEX).setEnabled(true);
                                bottomNavigationView.getMenu()
                                        .getItem(NAV_SETTINGS_INDEX).setEnabled(true);
                                break;
                            case R.id.nav_donate:
                                getSupportFragmentManager().beginTransaction()
                                        .add(R.id.fragment_switch, new FoodBankDonateFragment())
                                        .addToBackStack("Map")
                                        .commit();

                                item.setEnabled(false);
                                bottomNavigationView.getMenu()
                                        .getItem(NAV_MAP_INDEX).setEnabled(true);
                                bottomNavigationView.getMenu()
                                        .getItem(NAV_FAQ_INDEX).setEnabled(true);
                                bottomNavigationView.getMenu()
                                        .getItem(NAV_SETTINGS_INDEX).setEnabled(true);
                                break;

                            case R.id.nav_faq:
                                getSupportFragmentManager().beginTransaction()
                                        .add(R.id.fragment_switch, new FAQFragment())
                                        .addToBackStack("FAQ")
                                        .commit();

                                item.setEnabled(false);
                                bottomNavigationView.getMenu()
                                        .getItem(NAV_MAP_INDEX).setEnabled(true);
                                bottomNavigationView.getMenu()
                                        .getItem(NAV_DONATE_INDEX).setEnabled(true);
                                bottomNavigationView.getMenu()
                                        .getItem(NAV_SETTINGS_INDEX).setEnabled(true);
                                break;
                            case R.id.nav_settings:
                                startActivity(new Intent(getApplicationContext(),
                                        SettingsActivity.class));
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
                            moveCameraToMarker(new LatLng(
                                    lastKnownLocation.getLatitude(),
                                    lastKnownLocation.getLongitude()));
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
