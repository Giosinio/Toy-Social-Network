package model;

import utils.Tuple;

import java.time.LocalDate;

/**
 * Class that represents a {@link Friendship} request sent by a {@link User} to another User
 */

public class FriendshipRequest extends Entity<Tuple<String, String>> {

    private LocalDate created;

    /**
     * constructor for FriendshipRequest with given creation LocalDate
     * @param created - LocalDate that represents the date of this FriendshipRequest's creation
     */
    public FriendshipRequest(LocalDate created) {
        this.created = created;
    }

    /**
     * returns the username of the User who initiated the FriendshipRequest
     * @return a String representing the username of the User who initiated
     * the FriendshipRequest
     */
    public String getFrom() {
        return id.getFirst();
    }

    /**
     * returns the username of the User to whom the FriendshipRequest is sent
     * @return a String representing the username of the User who receives the
     * FriendshipRequest
     */
    public String getTo() {
        return id.getSecond();
    }

    /**
     * returns the creation date of the FriendshipRequest
     * @return the creation LocalDate of the FriendshipRequest
     */
    public LocalDate getCreated() {
        return created;
    }

    /**
     * sets the creation date of the FriendshipRequest
     * @param created - the creation LocalDate of the FriendshipRequest
     */
    public void setCreated(LocalDate created) {
        this.created = created;
    }
}
