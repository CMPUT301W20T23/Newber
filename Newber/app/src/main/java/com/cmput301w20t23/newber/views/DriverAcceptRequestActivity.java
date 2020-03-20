package com.cmput301w20t23.newber.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.cmput301w20t23.newber.R;
import com.cmput301w20t23.newber.controllers.RideController;
import com.cmput301w20t23.newber.controllers.UserController;
import com.cmput301w20t23.newber.helpers.Callback;
import com.cmput301w20t23.newber.models.Driver;
import com.cmput301w20t23.newber.models.Location;
import com.cmput301w20t23.newber.models.RideRequest;
import com.cmput301w20t23.newber.models.User;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Locale;
import java.util.Map;

/**
 * The Android Activity that handles Driver Accepting Request.
 *
 * @author Ayushi Patel, Ibrahim Aly
 */
public class DriverAcceptRequestActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap googleMap;
    private RideRequest request;
    private RideController rideController;
    private UserController userController;
    private Driver driver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_accept_request);
        request = (RideRequest) getIntent().getSerializableExtra("request");
        driver = (Driver) getIntent().getSerializableExtra("driver");
        rideController = new RideController();
        userController = new UserController(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        setUpTextViews();
    }

    /**
     * Function that sets the Text Views to the Rider that this (driver) accepted the ride request from
     */
    public void setUpTextViews() {
        // Get the pick up and drop off locations
        TextView pickUp = findViewById(R.id.driver_accept_pick_up);
        pickUp.setText(request.getStartLocation().toString());

        TextView dropOff = findViewById(R.id.driver_accept_drop_off);
        dropOff.setText(request.getEndLocation().toString());

        // Get the rider's name and set it in the text view
        final TextView riderName = findViewById(R.id.driver_accept_rider_name);
        userController.getUser(request.getRider(), new Callback<Map<String, Object>>() {
            @Override
            public void myResponseCallback(Map<String, Object> result) {
                String name = ((User) result.get("user")).getFullName();
                riderName.setText(name);
            }
        });

        // Set the fare
        TextView fare = findViewById(R.id.driver_accept_fare);
        fare.setText(String.format(Locale.US, "$%.2f", request.getCost()));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        // Add a marker in Edmonton and move the camera
        this.googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(request.getStartLocation().toLatLng(), 12.0f));
        this.googleMap.addMarker(new MarkerOptions().position(request.getStartLocation().toLatLng()));
        this.googleMap.addMarker(new MarkerOptions().position(request.getEndLocation().toLatLng()));
    }

    /**
     * Cancel Driver Accept Request Function
     * @param view
     */
    public void cancelRequest(View view) {
        setResult(Activity.RESULT_CANCELED, new Intent());
        finish();
    }

    /**
     * Handler Function when a Rider Request has been accepted by the Driver
     * @param view
     */
    public void acceptRequest(View view) {
        setResult(Activity.RESULT_OK, new Intent());
        request.setDriver(driver.getUid());
        rideController.updateDriverAndRequest(request);

        driver.setCurrentRequestId(request.getRequestId());
        userController.updateUserCurrentRequestId(driver.getUid(), driver.getCurrentRequestId());
        finish();
    }
}
