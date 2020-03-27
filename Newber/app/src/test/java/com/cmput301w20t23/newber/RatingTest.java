package com.cmput301w20t23.newber;

import com.cmput301w20t23.newber.models.Rating;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RatingTest {
    private Rating testRating;

    @Before
    public void setUp() {
        testRating = new Rating(5, 10);
    }

    @Test
    public void testSetAndGetUpvotes() {
        assertEquals(5, testRating.getUpvotes());
        testRating.setUpvotes(12);
        assertEquals(12, testRating.getUpvotes());
    }

    @Test
    public void testSetAndGetDownvotes() {
        assertEquals(10, testRating.getDownvotes());
        testRating.setDownvotes(15);
        assertEquals(15, testRating.getDownvotes());
    }

    @Test
    public void testCalculateRating() {
        assertEquals(33.33, testRating.calculateRating(), 0.01);
    }

    @Test
    public void testCalculateZeroRating() {
        testRating.setUpvotes(0);
        testRating.setDownvotes(0);
        assertEquals(0, testRating.calculateRating(), 0.01);
    }
}
