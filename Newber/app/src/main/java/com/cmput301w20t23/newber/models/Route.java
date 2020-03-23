package com.cmput301w20t23.newber.models;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class Route {
    private List<LatLng> points;
    private double distance;

    public Route(List<LatLng> points, double distance) {
        this.points = points;
        this.distance = distance;
    }

    public List<LatLng> getPoints() {
        return points;
    }

    public void setPoints(List<LatLng> points) {
        this.points = points;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
}
