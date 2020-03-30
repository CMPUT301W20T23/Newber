package com.cmput301w20t23.newber.controllers;

import com.cmput301w20t23.newber.database.DatabaseAdapter;
import com.cmput301w20t23.newber.helpers.Callback;
import com.cmput301w20t23.newber.models.Location;
import com.cmput301w20t23.newber.models.RequestStatus;
import com.cmput301w20t23.newber.models.RideRequest;
import com.cmput301w20t23.newber.models.User;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Observer;

public class RideController {
    private final FirebaseAuth mAuth;
    private DatabaseAdapter databaseAdapter;

    public RideController() {
        this.mAuth = FirebaseAuth.getInstance();
        this.databaseAdapter = DatabaseAdapter.getInstance();
    }

    public void getRideRequest(String requestId, Callback<RideRequest> callback) {
        this.databaseAdapter.getRideRequest(requestId, callback);
    }

    public void createRideRequest(final Location startLocation, final Location endLocation, double cost, String rider) {
        RideRequest rideRequest = new RideRequest(startLocation, endLocation, rider, cost);
        this.databaseAdapter.createRideRequest(rider, rideRequest);
    }

    public void removeRideRequest(RideRequest rideRequest) {
        // Remove request from firebase requests table
        this.databaseAdapter.removeRideRequest(rideRequest);
    }

    public void updateRideRequest(RideRequest request) {
        databaseAdapter.updateRideRequest(request);
    }

    public void getPendingRideRequests(Callback<ArrayList<RideRequest>> callback) {
        databaseAdapter.getPendingRideRequests(callback);
    }

    public void finishRideRequest(User driver, RideRequest rideRequest) {
        driver.setCurrentRequestId("");
        databaseAdapter.setUserCurrentRequestId(driver.getUid(), "");

        rideRequest.setStatus(RequestStatus.COMPLETED);
        updateRideRequest(rideRequest);
    }

    public void addListenerToRideRequest(Observer observer, String requestId) {
        databaseAdapter.addListenerToRideRequest(requestId);
        this.databaseAdapter.addObserver(observer);
    }

    public void removeListeners(Observer observer, String requestId) {
        this.databaseAdapter.deleteObserver(observer);

        if (requestId != null && !requestId.isEmpty()) {
            this.databaseAdapter.rideRequestListener.remove();
        }
    }
}
