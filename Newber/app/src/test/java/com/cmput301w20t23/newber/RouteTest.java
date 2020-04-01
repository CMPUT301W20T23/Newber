package com.cmput301w20t23.newber;

import com.cmput301w20t23.newber.models.Route;
import com.google.android.gms.maps.model.LatLng;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertTrue;

public class RouteTest {
    private Route testRoute;

    @Before
    public void setup() {
        LatLng latLng1 = new LatLng(-47.5, 47.5);
        LatLng latLng2 = new LatLng(-49.5, 49.5);
        LatLng latLng3 = new LatLng(-50.4, 108.5);
        List<LatLng> testLatLngList = new ArrayList<LatLng>();
        testLatLngList.add(latLng1);
        testLatLngList.add(latLng2);
        testLatLngList.add(latLng3);
        testRoute = new Route(testLatLngList, 150.593);
    }

    @Test
    public void testSetAndGetPoints() {
        LatLng latLng1 = new LatLng(-47.5, 47.5);
        LatLng latLng2 = new LatLng(-49.5, 49.5);
        LatLng latLng3 = new LatLng(-50.4, 108.5);
        List<LatLng> testLatLngList = testRoute.getPoints();
        assertTrue(testLatLngList.contains(latLng1) &&
                    testLatLngList.contains(latLng2) &&
                    testLatLngList.contains(latLng3));

        LatLng latLng4 = new LatLng(-23.4, 95.5);
        List<LatLng> newTestLatLngList = new ArrayList<LatLng>();
        newTestLatLngList.add(latLng1);
        newTestLatLngList.add(latLng2);
        newTestLatLngList.add(latLng3);
        newTestLatLngList.add(latLng4);
        testRoute.setPoints(newTestLatLngList);
        testLatLngList = testRoute.getPoints();
        assertTrue(testLatLngList.contains(latLng1) &&
                testLatLngList.contains(latLng2) &&
                testLatLngList.contains(latLng3) &&
                testLatLngList.contains(latLng4));
    }

    @Test
    public void testGetAndSetDistanceInMeters() {
        assert(150.593 == testRoute.getDistanceInMetres());
        testRoute.setDistanceInMetres(1234.9283);
        assert(1234.9283 == testRoute.getDistanceInMetres());
    }
}
