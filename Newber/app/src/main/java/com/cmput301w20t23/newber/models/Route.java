package com.cmput301w20t23.newber.models;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class Route {
    private List<LatLng> points;
    private double distanceInMeters;

    public Route(List<LatLng> points, double distanceInMeters) {
        this.points = points;
        this.distanceInMeters = distanceInMeters;
    }

    public List<LatLng> getPoints() {
        return points;
    }

    public void setPoints(List<LatLng> points) {
        this.points = points;
    }

    public double getDistanceInMeters() {
        return distanceInMeters;
    }

    public void setDistanceInMeters(double distanceInMeters) {
        this.distanceInMeters = distanceInMeters;
    }
}
