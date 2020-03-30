package com.cmput301w20t23.newber.models;


/**
 * Describes all possible states of a ride request.
 *
 * @author Jessica D'Cunha, Gaurav Sekhar
 */
public enum RequestStatus{
    /**
     * The request is pending (has yet to be offered to be serviced by a driver).
     */
    PENDING,
    /**
     * The request has been offered to be serviced by a driver.
     */
    OFFERED,
    /**
     * The offer of service has been accepted by the rider.
     */
    ACCEPTED,
    /**
     * The ride is in progress.
     */
    IN_PROGRESS,
    /**
     * The ride request has been cancelled.
     */
    CANCELLED,
    /**
     * The ride request is in a payment state for QR Codes
     */
    PAYMENT,
    /**
     * The ride has been completed.
     */
    COMPLETED
}
