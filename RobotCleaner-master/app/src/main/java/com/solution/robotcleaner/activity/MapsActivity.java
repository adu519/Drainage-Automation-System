package com.solution.robotcleaner.activity;

import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;
import com.preference.PowerPreference;
import com.solution.robotcleaner.R;
import com.solution.robotcleaner.Util;
import com.solution.robotcleaner.databinding.ActivityMapsBinding;
import com.solution.robotcleaner.service.CustomFirebaseMessagingService;

import java.util.Objects;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private static final int REQUEST_CHECK_SETTINGS = 11;
    private static final String TAG = "MapsActivity";
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private Location mLocation;
    private boolean permissionsGranted;
    private Marker mainMarker;
    private MarkerOptions mainMarkerOptions;
    private LocationRequest locationRequest;

    private ActivityMapsBinding binding;
    private ActionBarDrawerToggle actionBarToggle;
    private boolean admin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String token = PowerPreference.getDefaultFile().getString("user");
        if (token == null) {
            CustomFirebaseMessagingService.setToken();

        }
        boolean subscribed = PowerPreference.getDefaultFile().getBoolean("subscribed");
        if (!subscribed) {
            admin = PowerPreference.getDefaultFile().getBoolean("admin");
            FirebaseMessaging.getInstance().subscribeToTopic(admin ? "admin" : "user")
                    .addOnCompleteListener(task -> {
                        String msg = getString(R.string.msg_subscribed);
                        if (!task.isSuccessful()) {
                            msg = "Subscription failed";
                        }
                        Log.d(TAG, msg);
                        Toast.makeText(MapsActivity.this, msg, Toast.LENGTH_SHORT).show();
                    });
        }
        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        setContentView(binding.getRoot());

        requestPermission();
        binding.myLocation.setOnClickListener(v -> {
            if (mLocation != null) {
                moveCamera(mLocation, false);
            }
        });
        actionBarToggle = new ActionBarDrawerToggle(this, binding.drawerLayout, R.string.Open, R.string.Close);
        binding.drawerLayout.addDrawerListener(actionBarToggle);
        actionBarToggle.syncState();
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        binding.navigationView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.nav_logout:
                    FirebaseAuth.getInstance().signOut();
                    FirebaseMessaging.getInstance().unsubscribeFromTopic(admin ? "admin" : "user")
                            .addOnCompleteListener(task -> {
                                PowerPreference.getDefaultFile().setBoolean("subscribed", false);
                            });
                    startActivity(new Intent(MapsActivity.this, LoginAuthActivity.class));
                    finish();
                    break;
                case R.id.nav_person:
                    startActivity(new Intent(MapsActivity.this, AdminActivity.class));
                    break;
                default:
                    return true;
            }
            return true;
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.maps_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_refresh) {
            finish();
            startActivity(getIntent());
            return true;
        }
        if (actionBarToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getLastLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        //Fetch last location
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            mLocation = location;
                            initMap();
                        }
                    }
                });
    }

    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (mLocation != null) {

            moveCamera(mLocation, true);
        }

        mMap.setOnMarkerClickListener(this);
        // Map UI settings
        mMap.setMinZoomPreference(12f);
        mMap.setMaxZoomPreference(18f);
        mMap.setMyLocationEnabled(false);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.getUiSettings().setMapToolbarEnabled(false);
    }

    private void requestPermission() {
        permissionsGranted = Util.requestPermissions(this);
        if (permissionsGranted) {
            // Create Location request
            locationRequest = LocationRequest.create();
            //Set Location request settings
            createLocationRequest();

        }
    }

    private void moveCamera(Location location, boolean myLocation) {

        // Move map camera to specified location
        mLocation = location;
        float ZOOM_LEVEL = 14f;

        if (mMap != null)
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(locationToLatLng(location), ZOOM_LEVEL));

        if (myLocation && mMap != null) {
            if (mainMarker != null) mainMarker.remove();

            mainMarkerOptions = new MarkerOptions().position(locationToLatLng(mLocation))
                    .title("You");
//                    .icon(Misc.vectorToBitmap(MapsActivity.this, R.drawable.ic_car));

            mainMarker = mMap.addMarker(mainMarkerOptions);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(locationToLatLng(location), 15f));
        }
//         OpenLocationCode.CodeArea code = OpenLocationCode.decode("HW4G+G4");
//        String code1 = OpenLocationCode.encode(22.556370, 72.925256);

        // Get current pincode

    }

    private LatLng locationToLatLng(Location location) {
        return new LatLng(location.getLatitude(), location.getLongitude());
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (!marker.getTitle().equals("You") || marker.getTag() != null || true) {
            Intent intent = new Intent(MapsActivity.this, SensorActivity.class);
            startActivity(intent);
        }
        return true;
    }


    private void createLocationRequest() {

        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        // Request to turn on Device location/GPS
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        SettingsClient client = LocationServices.getSettingsClient(this);

        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
        task.addOnSuccessListener(locationSettingsResponse -> {
            Log.v(TAG, "Location request complete");
            getLastLocation();
        });

        task.addOnFailureListener(this, e -> {
            if (e instanceof ResolvableApiException) {
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    ResolvableApiException resolvable = (ResolvableApiException) e;
                    resolvable.startResolutionForResult(MapsActivity.this,
                            REQUEST_CHECK_SETTINGS);
                } catch (IntentSender.SendIntentException sendEx) {
                    // Ignore the error.
                }
            }
        });
    }

}
