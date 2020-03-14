package com.cmput301w20t23.newber.models;

import androidx.annotation.NonNull;

import com.google.android.libraries.places.api.model.Place;

import java.io.Serializable;
import java.util.UUID;

/**
 * Describes a request for a ride.
 *
 * @author Jessica D'Cunha, Gaurav Sekhar, Ibrahim Aly
 */
public class RideRequest implements Serializable {
    private String requestId;
    private Location startLocation;
    private Location endLocation;
    private RequestStatus status;
    private String driver;
    private String rider;
    private double cost;

    public RideRequest() { }

    /**
     * Instantiates a new Ride request.
     *
     * @param startLocation    the starting location
     * @param endLocation      the end location
     * @param rider      the rider involved
     * @param cost          the cost of the ride
     */
    public RideRequest(Location startLocation, Location endLocation, String rider, double cost) {
        this.requestId = UUID.randomUUID().toString();
        this.startLocation = startLocation;
        this.endLocation = endLocation;
        this.status = RequestStatus.PENDING;
        this.rider = rider;
        this.driver = null;
        this.cost = cost;
    }

    public RideRequest(String requestId, Location startLocation, Location endLocation, RequestStatus status, String rider, String driver, double cost) {
        this.requestId = requestId;
        this.startLocation = startLocation;
        this.endLocation = endLocation;
        this.status = status;
        this.rider = rider;
        this.driver = driver;
        this.cost = cost;
    }

    /**
     * Gets request id.
     *
     * @return the request id
     */
    public String getRequestId() {
        return requestId;
    }

    /**
     * Sets request id.
     *
     * @param requestId the request id
     */
    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    /**
     * Gets start.
     *
     * @return the starting location
     */
    public Location getStartLocation() {
        return startLocation;
    }

    /**
     * Sets start.
     *
     * @param startLocation the starting location
     */
    public void setStartLocation(Location startLocation) {
        this.startLocation = startLocation;
    }

    /**
     * Gets end.
     *
     * @return the ending location
     */
    public Location getEndLocation() {
        return endLocation;
    }

    /**
     * Sets end.
     *
     * @param endLocation the ending location
     */
    public void setEndLocation(Location endLocation) {
        this.endLocation = endLocation;
    }

    /**
     * Gets status.
     *
     * @return the request status
     */
    public RequestStatus getStatus() {
        return status;
    }

    /**
     * Sets status.
     *
     * @param status the request status
     */
    public void setStatus(RequestStatus status) {
        this.status = status;
    }

    /**
     * Gets driver.
     *
     * @return the driver involved
     */
    public String getDriver() {
        return driver;
    }

    /**
     * Sets driver.
     *
     * @param driver the driver involved
     */
    public void setDriver(String driver) {
        this.driver = driver;
    }

    /**
     * Gets rider.
     *
     * @return the rider involved
     */
    public String getRider() {
        return rider;
    }

    /**
     * Sets rider.
     *
     * @param rider the rider involved
     */
    public void setRider(String rider) {
        this.rider = rider;
    }

    /**
     * Gets cost.
     *
     * @return the cost of the ride
     */
    public double getCost() {
        return cost;
    }

    /**
     * Sets cost.
     *
     * @param cost the cost of the ride
     */
    public void setCost(double cost) {
        this.cost = cost;
    }

    @NonNull
    @Override
    public String toString() {
        return "Pick up at: " + startLocation.getName();
    }
}