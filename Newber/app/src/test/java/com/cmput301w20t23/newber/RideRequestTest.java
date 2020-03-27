package com.cmput301w20t23.newber;

import com.cmput301w20t23.newber.models.Driver;
import com.cmput301w20t23.newber.models.Location;
import com.cmput301w20t23.newber.models.Rating;
import com.cmput301w20t23.newber.models.RequestStatus;
import com.cmput301w20t23.newber.models.RideRequest;
import com.cmput301w20t23.newber.models.Rider;

import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RideRequestTest {
    private RideRequest testRequest;
    private Location testStart;
    private Location testEnd;
    private Rider testRider;
    private Driver testDriver;

    @Before
    public void setUp() {
        testStart = new Location();
        testStart.setName("Start");
        testStart.setLatitude(12.345);
        testStart.setLongitude(98.765);

        testEnd = new Location();
        testEnd.setName("End");
        testEnd.setLatitude(55.555);
        testEnd.setLongitude(44.444);

        testRider = new Rider("First",
                "Last",
                "username",
                "7801234567",
                "rider@example.com",
                "12345",
                "98765");

        testDriver = new Driver("First",
                "Last",
                "username",
                "7801234567",
                "rider@example.com",
                "12345",
                "98765",
                new Rating(1, 1));

        testRequest = new RideRequest("11111", testStart, testEnd, RequestStatus.PENDING, testRider, testDriver, 10.0);
    }

    @Test
    public void testSetAndGetRequestId() {
        assertEquals("11111", testRequest.getRequestId());
        String testUUID = UUID.randomUUID().toString();
        testRequest.setRequestId(testUUID);
        assertEquals(testUUID, testRequest.getRequestId());
    }

    @Test
    public void testSetAndGetStartLocation() {
        assertEquals("Start", testRequest.getStartLocation().getName());
        assertEquals(12.345, testRequest.getStartLocation().getLatitude(), 0.001);
        assertEquals(98.765, testRequest.getStartLocation().getLongitude(), 0.001);

        Location newStart = new Location();
        newStart.setName("New");
        newStart.setLatitude(11.111);
        newStart.setLongitude(22.222);
        testRequest.setStartLocation(newStart);

        assertEquals("New", testRequest.getStartLocation().getName());
        assertEquals(11.111, testRequest.getStartLocation().getLatitude(), 0.001);
        assertEquals(22.222, testRequest.getStartLocation().getLongitude(), 0.001);
    }

    @Test
    public void testSetAndGetEndLocation() {
        assertEquals("End", testRequest.getEndLocation().getName());
        assertEquals(55.555, testRequest.getEndLocation().getLatitude(), 0.001);
        assertEquals(44.444, testRequest.getEndLocation().getLongitude(), 0.001);

        Location newEnd = new Location();
        newEnd.setName("New");
        newEnd.setLatitude(99.999);
        newEnd.setLongitude(88.888);
        testRequest.setEndLocation(newEnd);

        assertEquals("New", testRequest.getEndLocation().getName());
        assertEquals(99.999, testRequest.getEndLocation().getLatitude(), 0.001);
        assertEquals(88.888, testRequest.getEndLocation().getLongitude(), 0.001);
    }

    @Test
    public void testSetAndGetStatus() {
        assertEquals(RequestStatus.PENDING, testRequest.getStatus());
        testRequest.setStatus(RequestStatus.OFFERED);
        assertEquals(RequestStatus.OFFERED, testRequest.getStatus());
    }

    @Test
    public void testSetAndGetDriver() {
        assertTrue(testDriver.equals(testRequest.getDriver()));
    }

    @Test
    public void testSetAndGetRider() {
        assertTrue(testRider.equals(testRequest.getRider()));
    }

    @Test
    public void testSetAndGetCost() {
        assertEquals(10.0, testRequest.getCost(), 0.01);
        testRequest.setCost(15.0);
        assertEquals(15.0, testRequest.getCost(), 0.01);
    }

    @Test
    public void testToString() {
        String expected = "Pick up at: Start";
        assertEquals(expected, testRequest.toString());
    }
}
