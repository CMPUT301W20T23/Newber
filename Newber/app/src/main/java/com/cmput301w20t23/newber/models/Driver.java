package com.cmput301w20t23.newber.models;

import java.io.Serializable;

/**
 * Describes the Driver type of user.
 *
 * @author Jessica D'Cunha, Gaurav Sekhar
 */
public class Driver extends User implements Serializable {
    private Rating rating;
    private RideRequest currentRequest;

    /**
     * Instantiates a new Driver.
     *
     * @param firstName the first name
     * @param lastName  the last name
     * @param username  the username
     * @param phone     the phone
     * @param email     the email
     * @param uId       the user id
     */

    public Driver(String firstName, String lastName, String username, String phone, String email,
                  String uId, String currRequestId, Rating rating) {
        super(firstName, lastName, username, phone, email, uId);
        this.rating = rating;
        this.currentRequest = null;
        this.setCurrentRequestId(currRequestId);
    }

    public Driver(String firstName, String lastName, String username, String phone, String email,
                  String uId, String currRequestId, Rating rating, double balance) {
        super(firstName, lastName, username, phone, email, uId, balance);
        this.rating = rating;
        this.currentRequest = null;
        this.setCurrentRequestId(currRequestId);
    }


    public Driver() {}

    public void setRating(Rating rating) { this.rating = rating; }

    public Rating getRating() { return this.rating; }

    public void setCurrentRequest(RideRequest request) { this.currentRequest = request; }

    public RideRequest getCurrentRequest() { return this.currentRequest; }

}
