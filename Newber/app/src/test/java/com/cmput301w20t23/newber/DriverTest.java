package com.cmput301w20t23.newber;

import com.cmput301w20t23.newber.models.Driver;
import com.cmput301w20t23.newber.models.Location;
import com.cmput301w20t23.newber.models.Rating;
import com.cmput301w20t23.newber.models.RequestStatus;
import com.cmput301w20t23.newber.models.RideRequest;
import com.cmput301w20t23.newber.models.Rider;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DriverTest {
    private Driver testDriver;
    private Rating testRating;
    private RideRequest testRequest;
    private Location testStart;
    private Location testEnd;

    @Before
    public void setup(){
        testRating = new Rating(0, 0);
        testStart = new Location();
        testStart.setLatitude(53.5461);
        testStart.setLongitude(113.4938);
        testStart.setName("Edmonton");

        testEnd = new Location();
        testEnd.setLongitude(40.7128);
        testEnd.setLatitude(74.0060);
        testStart.setName("New York");

        Rider testRider = new Rider();

        testRequest = new RideRequest("123", testStart, testEnd, RequestStatus.PENDING, testRider, testDriver, 10);
        testDriver = new Driver("first",
                "last",
                "user",
                "1234567890",
                "email@site.ca",
                "69420",
                testRequest.getRequestId(),
                testRating);

        testDriver.setCurrentRequest(testRequest);
    }

    @Test
    public void testSetAndGetRating(){
        Rating result = testDriver.getRating();
        Rating expected = new Rating(0, 0);
        assertEquals(true, expected.getUpvotes() == result.getUpvotes() && expected.getDownvotes() == result.getDownvotes());

        Rating newRating = new Rating(1, 6);
        testDriver.setRating(newRating);
        expected.setUpvotes(1);
        expected.setDownvotes(6);
        result = testDriver.getRating();
        assertEquals(true, expected.getUpvotes() == result.getUpvotes() && expected.getDownvotes() == result.getDownvotes());
    }

    @Test
    public void testSetAndGetCurrentRequest(){
        RideRequest result = testDriver.getCurrentRequest();
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

        testDriver.setCurrentRequest(expected);
        result = testDriver.getCurrentRequest();
        assertEquals(true, expected.getRequestId() == result.getRequestId()
                && expected.getStartLocation() == result.getStartLocation()
                && expected.getEndLocation() == result.getEndLocation()
                && expected.getStatus() == result.getStatus()
                && expected.getCost() == result.getCost());


    }
}
