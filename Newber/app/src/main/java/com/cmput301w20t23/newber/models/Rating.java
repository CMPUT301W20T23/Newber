package com.cmput301w20t23.newber.models;

/**
 * Describes a driver's rating.
 *
 * @author Jessica D'Cunha, Gaurav Sekhar
 */
public class Rating {
    private int upvotes;
    private int downvotes;

    public Rating() { }

    /**
     * Instantiates a new Rating.
     *
     * @param upvotes   the driver's upvotes
     * @param downvotes the driver's downvotes
     */
    public Rating(int upvotes, int downvotes) {
        this.upvotes = upvotes;
        this.downvotes = downvotes;
    }

    /**
     * Gets upvotes.
     *
     * @return the upvotes
     */
    public int getUpvotes() {
        return upvotes;
    }

    /**
     * Sets upvotes.
     *
     * @param upvotes the upvotes
     */
    public void setUpvotes(int upvotes) {
        this.upvotes = upvotes;
    }

    /**
     * Gets downvotes.
     *
     * @return the downvotes
     */
    public int getDownvotes() {
        return downvotes;
    }

    /**
     * Sets downvotes.
     *
     * @param downvotes the downvotes
     */
    public void setDownvotes(int downvotes) {
        this.downvotes = downvotes;
    }

    /**
     * Upvote, meaning upvotes count increases by 1.
     */
    public void upvote() { this.upvotes += 1; }

    /**
     * Downvote, meaning downvotes count decreases by 1.
     */
    public void downvote() { this.downvotes += 1; }

    /**
     * Calculate the driver's rating as a percent.
     *
     * @return the driver's rating as a percent
     */
    public double calculateRating() {
        if (upvotes == 0 && downvotes == 0) {
            return 0;
        }
        else {
            return ((double) upvotes/((double) upvotes + (double) downvotes))*100;
        }
    }
}
