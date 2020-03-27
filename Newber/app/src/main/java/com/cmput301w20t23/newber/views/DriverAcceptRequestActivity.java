package com.cmput301w20t23.newber.views;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.cmput301w20t23.newber.R;
import com.cmput301w20t23.newber.controllers.OnMapAndViewReadyListener;
import com.cmput301w20t23.newber.controllers.RideController;
import com.cmput301w20t23.newber.controllers.UserController;
import com.cmput301w20t23.newber.helpers.Callback;
import com.cmput301w20t23.newber.helpers.RouteGetter;
import com.cmput301w20t23.newber.models.Driver;
import com.cmput301w20t23.newber.models.RideRequest;
import com.cmput301w20t23.newber.models.Route;
import com.cmput301w20t23.newber.models.User;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.Locale;
import java.util.Map;

/**
 * The Android Activity that handles Driver Accepting Request.
 *
 * @author Ayushi Patel, Ibrahim Aly
 */
public class DriverAcceptRequestActivity extends AppCompatActivity {
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

        new OnMapAndViewReadyListener(mapFragment, new Callback<GoogleMap>() {
            @Override
            public void myResponseCallback(GoogleMap result) {
                googleMap = result;
                configureMap();
            }
        });

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

    private void configureMap() {
        LatLng pickUp = request.getStartLocation().toLatLng();
        LatLng dropOff = request.getEndLocation().toLatLng();

        // Draw route between pick up and drop off locations
        RouteGetter.getRoute(pickUp, dropOff, getString(R.string.API_KEY), new Callback<Route>() {
            @Override
            public void myResponseCallback(Route result) {
                // Drawing polyline in the Google Map
                if (result != null) {
                    googleMap.addPolyline(new PolylineOptions()
                            .addAll(result.getPoints())
                            .width(20)
                            .color(getColor(R.color.bannerGreen)));
                }
            }
        });

        // Add pick up and drop off location markers
        googleMap.addMarker(new MarkerOptions().position(pickUp).title("Pick Up"));
        googleMap.addMarker(new MarkerOptions().position(dropOff).title("Drop Off")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));

        // Move camera
        LatLngBounds latLngBounds = new LatLngBounds.Builder()
                .include(pickUp)
                .include(dropOff)
                .build();

        this.googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 100));
    }
}
