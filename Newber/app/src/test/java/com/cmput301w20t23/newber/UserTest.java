package com.cmput301w20t23.newber;

import com.cmput301w20t23.newber.models.User;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class UserTest {
    private User testUser;

    @Before
    public void setUp() {
        testUser = new User("Test",
                "Testerson",
                "testuser",
                "7801234567",
                "test@test.com",
                "1234567890");
    }

    @Test
    public void testSetAndGetFirstName() {
        assertEquals("Test", testUser.getFirstName());
        testUser.setFirstName("Testifer");
        assertEquals("Testifer", testUser.getFirstName());
    }

    @Test
    public void testSetAndGetLastName() {
        assertEquals("Testerson", testUser.getLastName());
        testUser.setLastName("Testopolous");
        assertEquals("Testopolous", testUser.getLastName());
    }

    @Test
    public void testSetAndGetUsername() {
        assertEquals("testuser", testUser.getUsername());
        testUser.setUsername("usertest");
        assertEquals("usertest", testUser.getUsername());
    }

    @Test
    public void testSetAndGetPhone() {
        assertEquals("7801234567", testUser.getPhone());
        testUser.setPhone("7809876543");
        assertEquals("7809876543", testUser.getPhone());
    }

    @Test
    public void testSetAndGetEmail() {
        assertEquals("test@test.com", testUser.getEmail());
        testUser.setEmail("test@test.ca");
        assertEquals("test@test.ca", testUser.getEmail());
    }

    @Test
    public void testSetAndGetCurrentRequestId() {
        assertEquals("", testUser.getCurrentRequestId());
        testUser.setCurrentRequestId("123456");
        assertEquals("123456", testUser.getCurrentRequestId());
    }
}
