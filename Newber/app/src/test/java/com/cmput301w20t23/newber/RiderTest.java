package com.cmput301w20t23.newber;

import com.cmput301w20t23.newber.models.Driver;
import com.cmput301w20t23.newber.models.Location;
import com.cmput301w20t23.newber.models.RequestStatus;
import com.cmput301w20t23.newber.models.RideRequest;
import com.cmput301w20t23.newber.models.Rider;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RiderTest {
    private Rider testRider;
    private RideRequest testRequest;
    private Location testStart;
    private Location testEnd;

    @Before
    public void setup(){
        testStart = new Location();
        testStart.setLatitude(53.5461);
        testStart.setLongitude(113.4938);
        testStart.setName("Edmonton");

        testEnd = new Location();
        testEnd.setLongitude(40.7128);
        testEnd.setLatitude(74.0060);
        testStart.setName("New York");

        testRider = new Rider("first",
                "last",
                "user",
                "1234567890",
                "email@site.ca",
                "69420",
                "");

        Driver testDriver = new Driver();

        testRequest = new RideRequest("123", testStart, testEnd, RequestStatus.PENDING, testRider.getUid(), testDriver.getUid(), 10);

        testRider.setCurrentRequest(testRequest);
        testRider.setCurrentRequestId(testRequest.getRequestId());
    }

    @Test
    public void testSetAndGetCurrentRequest(){
        RideRequest result = testRider.getCurrentRequest();
        RideRequest expected = testRequest;
        assertEquals(true, expected.getRequestId() == result.getRequestId()
                && expected.getStartLocation() == result.getStartLocation()
                && expected.getEndLocation() == result.getEndLocation()
                && expected.getStatus() == result.getStatus()
                && expected.getCost() == result.getCost()
        );


        expected.setRequestId("321");
        expected.setStartLocation(testEnd);
        expected.setEndLocation(testStart);
        expected.setStatus(RequestStatus.COMPLETED);
        expected.setCost(16);

        testRider.setCurrentRequest(expected);
        result = testRider.getCurrentRequest();
        assertEquals(true, expected.getRequestId() == result.getRequestId()
                && expected.getStartLocation() == result.getStartLocation()
                && expected.getEndLocation() == result.getEndLocation()
                && expected.getStatus() == result.getStatus()
                && expected.getCost() == result.getCost());
    }
}

