package com.cmput301w20t23.newber.models;

import java.io.Serializable;

/**
 * Describes the Rider type of user.
 *
 * @author Jessica D'Cunha, Gaurav Sekhar
 */
public class Rider extends User implements Serializable {

    RideRequest currentRequest;

    /**
     * Instantiates a new Rider.
     *
     * @param firstName     the first name
     * @param lastName      the last name
     * @param username      the username
     * @param phone         the phone
     * @param email         the email
     * @param uId           the user id
     * @param currentRequestId the current request id
     */

    public Rider(String firstName, String lastName, String username, String phone, String email,
                 String uId, String currentRequestId) {
        super(firstName, lastName, username, phone, email, uId);
        this.setCurrentRequestId(currentRequestId);
        this.currentRequest = null;
    }

    public Rider(String firstName, String lastName, String username, String phone, String email,
                 String uId, String currentRequestId, double balance) {
        super(firstName, lastName, username, phone, email, uId, balance);
        this.setCurrentRequestId(currentRequestId);
        this.currentRequest = null;
    }


    public Rider() {}

    public void setCurrentRequest(RideRequest request) { this.currentRequest = request; }

    public RideRequest getCurrentRequest() { return this.currentRequest; }
}
