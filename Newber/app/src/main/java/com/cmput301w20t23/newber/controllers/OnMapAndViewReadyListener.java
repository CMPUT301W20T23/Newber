package com.cmput301w20t23.newber.controllers;

import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;

import com.cmput301w20t23.newber.helpers.Callback;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

/**
 * This class listens for when the map is rendered and ready.
 *
 * @author Ayushi Patel
 */
public class OnMapAndViewReadyListener implements OnGlobalLayoutListener, OnMapReadyCallback {
    private final SupportMapFragment mapFragment;
    private final View mapView;
    private final Callback<GoogleMap> callback;
    private boolean isViewReady;
    private boolean isMapReady;
    private GoogleMap googleMap;

    /**
     * Instantiates a new OnMapandViewReadyListener.
     *
     * @param mapFragment   the map fragment
     * @param callback      the callback to be fired when the map is rendered and ready
     */
    public OnMapAndViewReadyListener(SupportMapFragment mapFragment, Callback<GoogleMap> callback) {
        this.mapFragment = mapFragment;
        mapView = mapFragment.getView();
        this.callback = callback;
        isViewReady = false;
        isMapReady = false;
        googleMap = null;

        registerListeners();
    }

    /**
     * Registers listeners.
     */
    private void registerListeners() {
        if ((mapView.getWidth() != 0) && (mapView.getHeight() != 0)) {
            // View has already completed layout.
            isViewReady = true;
        } else {
            // Map has not undergone layout, register a View observer.
            mapView.getViewTreeObserver().addOnGlobalLayoutListener(this);
        }

        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        isMapReady = true;
        fireCallbackIfReady();
    }

    @Override
    public void onGlobalLayout() {
        mapView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
        isViewReady = true;
        fireCallbackIfReady();
    }

    /**
     * Fires callback if both flags are set.
     */
    private void fireCallbackIfReady() {
        if (isViewReady && isMapReady) {
            callback.myResponseCallback(googleMap);
        }
    }
}
