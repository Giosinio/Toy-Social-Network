package model.dto;

import model.User;

import java.time.LocalDate;

/**
 * Data Transfer Object for User entity
 */
public class UserDTO {
    private final String username, firstName, lastName;
    private final LocalDate lastLoginAt;

    /**
     * parametrized constructor of the UserDTO class
     * @param username - String
     * @param firstName - String
     * @param lastName - String
     * @param lastLoginAt - LocalDate
     */
    public UserDTO(String username, String firstName, String lastName, LocalDate lastLoginAt) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.lastLoginAt = lastLoginAt;
    }

    public UserDTO(User user){
        this.username = user.getId();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.lastLoginAt = user.getLastLoginAt();
    }

    /**
     * method that returns the username of the User
     * @return - String
     */
    public String getUsername() {
        return username;
    }

    /**
     * method that returns the first name of the User
     * @return - String
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * method that returns the last name of the User
     * @return - String
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * method that returns the full name of the User
     * @return - String
     */
    public String getFullName(){
        return firstName + " " + lastName;
    }

    /**
     * method that returns the last login date of the User
     * @return - LocalDate
     */
    public LocalDate getLastLoginAt() {
        return lastLoginAt;
    }

    @Override
    public String toString(){
        return firstName + " " + lastName;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (!(obj instanceof UserDTO))
            return false;
        return getUsername().equals(((UserDTO) obj).getUsername());
    }
}
