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

/**
 * This is the Ride Controller, which is responsible for the Ride Requests Logic
 *
 * @author Ibrahim Aly
 */
public class RideController {
    private final FirebaseAuth mAuth;
    private DatabaseAdapter databaseAdapter;

    public RideController() {
        this.mAuth = FirebaseAuth.getInstance();
        this.databaseAdapter = DatabaseAdapter.getInstance();
    }

    /**
     * Registers listeners.
     * @param requestId The ID of Ride Request
     * @param callback The Callback that will be returned to the Activity
     */
    public void getRideRequest(String requestId, Callback<RideRequest> callback) {
        this.databaseAdapter.getRideRequest(requestId, callback);
    }

    /**
     * Creates a new Ride Request in Firestore
     * @param startLocation The start location of the ride request
     * @param endLocation The end location of the ride request
     * @param cost The cost of the ride request
     * @param rider The UID of the Rider who requested the Ride Request
     */
    public void createRideRequest(final Location startLocation, final Location endLocation, double cost, String rider) {
        RideRequest rideRequest = new RideRequest(startLocation, endLocation, rider, cost);
        this.databaseAdapter.createRideRequest(rider, rideRequest);
    }

    /**
     * Cancels a Ride Request that the Rider just requested
     * @param rideRequest The ride request to be cancelled and removed from Firestore
     */
    public void removeRideRequest(RideRequest rideRequest) {
        // Remove request from firebase requests table
        this.databaseAdapter.removeRideRequest(rideRequest);
    }

    /**
     * Updates a ride request with updated fields in the request
     * @param request The ride request to be updated in Firestore
     */
    public void updateRideRequest(RideRequest request) {
        databaseAdapter.updateRideRequest(request);
    }

    /**
     * Get all pending ride requests from Firestore
     * @param callback The callback that will be returned to the activity
     */
    public void getPendingRideRequests(Callback<ArrayList<RideRequest>> callback) {
        databaseAdapter.getPendingRideRequests(callback);
    }

    /**
     * Completes a ride request
     * @param driver The driver that is completing the ride request
     * @param rideRequest The ride request to be completed
     */
    public void finishRideRequest(User driver, RideRequest rideRequest) {
        //Clear the current request Id field in the driver
        driver.setCurrentRequestId("");
        databaseAdapter.setUserCurrentRequestId(driver.getUid(), "");

        //Set the status of the ride request to COMPLETED and update it in Firestore
        rideRequest.setStatus(RequestStatus.COMPLETED);
        updateRideRequest(rideRequest);
    }

    /**
     * Add an activity as an observer to updates to a ride request in Firestore
     * @param observer An activity that will be listening to updates in Firestore
     * @param requestId The ID of the ride request that will be listened on
     */
    public void addListenerToRideRequest(Observer observer, String requestId) {
        databaseAdapter.addListenerToRideRequest(requestId);
        this.databaseAdapter.addObserver(observer);
    }

    /**
     * Removes all observers/listeners
     * @param observer The observers to be removed
     * @param requestId The ID of the ride request to stop listening on
     */
    public void removeListeners(Observer observer, String requestId) {
        this.databaseAdapter.deleteObserver(observer);

        if (requestId != null && !requestId.isEmpty()) {
            this.databaseAdapter.rideRequestListener.remove();
        }
    }
}
