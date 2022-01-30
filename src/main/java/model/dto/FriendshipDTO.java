package model.dto;

import java.time.LocalDate;

/**
 * Data Transfer Object for Friendship entity
 */
public class FriendshipDTO {
    private final UserDTO user1, user2;
    private final LocalDate date;

    /**
     * parametrized constructor for FriendshipDTO class
     * @param user1 - reference to the first User from the Friendship
     * @param user2 - reference to the second User from the Friendship
     * @param date - LocalDate
     */
    public FriendshipDTO(UserDTO user1, UserDTO user2, LocalDate date){
        this.user1 = user1;
        this.user2 = user2;
        this.date = date;
    }

    /**
     * method that returns a reference to the first UserDTO entity
     * @return - UserDTO
     */
    public UserDTO getFirstUser(){return user1;}

    /**
     * method that returns a reference to the first UserDTO entity
     * @return - UserDTO
     */
    public UserDTO getSecondUser(){return user2;}

    /**
     * method that returns the date on which this Friendship was made
     * @return - UserDTO
     */
    public LocalDate getDate() {
        return date;
    }

    @Override
    public String toString(){
        return user1.toString() + " is friend with " + user2.toString() + " since " + date;
    }
}
