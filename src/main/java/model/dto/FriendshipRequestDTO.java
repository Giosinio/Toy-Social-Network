package model.dto;

import model.FriendshipRequest;
import model.User;

import java.time.LocalDate;

/**
 * Data Transfer Object for {@link FriendshipRequest} entity
 */

public class FriendshipRequestDTO {
    private final LocalDate created;
    private final UserDTO fromUserDTO, toUserDTO;

    /**
     * constructor for FriendshipRequestDTO class from two
     * {@link UserDTO UserDTOs} of the sending, respectively receiving
     * {@link User Users}, and a LocalDate that represents the creation date
     * of the FriendshipRequest
     * @param fromUserDTO - DTO of the sending User from the FriendshipRequest
     * @param toUserDTO - DTO of the receiving User from the FriendshipRequest
     * @param created - LocalDate representing the creation date of the FriendshipRequest
     */
    public FriendshipRequestDTO(UserDTO fromUserDTO, UserDTO toUserDTO, LocalDate created) {
        this.created = created;
        this.fromUserDTO = fromUserDTO;
        this.toUserDTO = toUserDTO;
    }

    /**
     * returns the UserDTO of the sending User from the FriendshipRequest
     * @return - UserDTO
     */
    public UserDTO getFromUserDTO() {return fromUserDTO;}

    /**
     * returns the UserDTO of the receiving User from the FriendshipRequest
     * @return - UserDTO
     */
    public UserDTO getToUserDTO(){return toUserDTO;}

    /**
     * returns the creation date of the FriendshipRequest
     * @return the creation LocalDate of the FriendshipRequest
     */
    public LocalDate getCreated() {
        return created;
    }

    /**
     * overrides toString method, should be used only internally, and not for display
     * @return a String representation of FriendshipRequestDTO
     */
    @Override
    public String toString(){
        return getFromUserDTO().toString() + " sent " + getToUserDTO().toString() + " a FriendshipRequest on " + getCreated().toString() + ".";
    }
}
