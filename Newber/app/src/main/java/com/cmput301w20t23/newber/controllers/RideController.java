package com.cmput301w20t23.newber.controllers;
import android.content.Context;
import androidx.annotation.NonNull;

import com.cmput301w20t23.newber.database.DatabaseAdapter;
import com.cmput301w20t23.newber.helpers.Callback;
import com.cmput301w20t23.newber.models.RequestStatus;
import com.cmput301w20t23.newber.models.Location;
import com.cmput301w20t23.newber.models.RideRequest;
import com.cmput301w20t23.newber.models.Rider;
import com.cmput301w20t23.newber.views.DriverRequestActivity;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.libraries.places.api.model.Place;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class RideController {
    private final FirebaseDatabase database;
    private final FirebaseAuth mAuth;
    private DatabaseAdapter databaseAdapter;

    public RideController() {
        this.database = FirebaseDatabase.getInstance();
        this.mAuth = FirebaseAuth.getInstance();
        this.databaseAdapter = DatabaseAdapter.getInstance();
    }

    public void getRideRequest(String requestId, Callback<RideRequest> callback) {
        this.databaseAdapter.getRideRequest(requestId, callback);
    }

    public void createRideRequest(final Location startLocation, final Location endLocation, double cost, Rider rider) {
        RideRequest rideRequest = new RideRequest(startLocation, endLocation, rider, cost);
        this.databaseAdapter.createRideRequest(rider, rideRequest);
    }

    public void removeRideRequest(RideRequest rideRequest) {
        // Remove request from firebase requests table
        this.databaseAdapter.removeRideRequest(rideRequest);
    }

    public void updateDriverAndRequest(RideRequest request) {
        request.setStatus(RequestStatus.OFFERED);
        updateRideRequest(request);
    }

    public void updateRideRequest(RideRequest request) {
        databaseAdapter.updateRideRequest(request);
    }

    public void getPendingRideRequests(Callback<ArrayList<RideRequest>> callback) {
        databaseAdapter.getPendingRideRequests(callback);
    }
}
