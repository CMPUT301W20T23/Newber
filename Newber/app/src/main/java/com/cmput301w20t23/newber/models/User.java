package com.cmput301w20t23.newber.models;

import java.io.Serializable;

/**
 * Describes a user of the app.
 *
 * @author Jessica D'Cunha, Gaurav Sekhar
 */
public class User implements Serializable {
    private String firstName;
    private String lastName;
    private String username;
    private String phone;
    private String email;
    private String uId;
    private String currRequestId;
    private double balance;

    public final static double START_BALANCE = 200;

    /**
     * Instantiates a new User.
     *
     * @param firstName the first name
     * @param lastName  the last name
     * @param username  the username
     * @param phone     the phone
     * @param email     the email
     * @param uId       the user id
     */

    public User(String firstName, String lastName, String username, String phone, String email, String uId) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.phone = phone;
        this.email = email;
        this.uId = uId;
        this.currRequestId = "";
        this.balance = START_BALANCE;
    }

    public User(String firstName, String lastName, String username, String phone, String email, String uId,
                double balance) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.phone = phone;
        this.email = email;
        this.uId = uId;
        this.currRequestId = "";
        this.balance = balance;
    }

    public User() {}

    /**
     * Gets first name.
     *
     * @return the first name
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Sets first name.
     *
     * @param firstName the first name
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Gets last name.
     *
     * @return the last name
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets last name.
     *
     * @param lastName the last name
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Gets username.
     *
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets username.
     *
     * @param username the username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Gets phone.
     *
     * @return the phone
     */
    public String getPhone() {
        return phone;
    }

    /**
     * Sets phone.
     *
     * @param phone the phone
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * Gets email.
     *
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets email.
     *
     * @param email the email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Sets email.
     *
     * @return the user ID
     */
    public String getUid() {
        return this.uId;
    }


    /**
     * Sets email.
     *
     * @param uId the user ID
     */
    public void setUid(String uId) {
        this.uId = uId;
    }

    public String getCurrentRequestId() { return this.currRequestId; }

    public void setCurrentRequestId(String currRequestId) { this.currRequestId = currRequestId; }

    public String getFullName() { return this.firstName + " " + this.lastName; };

    public double getBalance() { return this.balance; }

    public void topUpBalance() {
        this.balance = START_BALANCE;
    }

    public void addToBalance(double cost) {
        this.balance += cost;
    }

    public void subtractFromBalance(double cost) {
        this.balance -= cost;
    }
}
