package com.cmput301w20t23.newber.models;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * Class to handle drawing a route between a start and location, used in RouteGetter
 */
public class Route {
    private List<LatLng> points;
    private double distanceInMetres;

    public Route(List<LatLng> points, double distanceInMetres) {
        this.points = points;
        this.distanceInMetres = distanceInMetres;
    }

    public List<LatLng> getPoints() {
        return points;
    }

    public void setPoints(List<LatLng> points) {
        this.points = points;
    }

    public double getDistanceInMetres() {
        return distanceInMetres;
    }

    public void setDistanceInMetres(double distanceInMetres) {
        this.distanceInMetres = distanceInMetres;
    }
}
