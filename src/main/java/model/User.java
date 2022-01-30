package model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Class that extends generic class Entity, representing User entity
 */
public class User extends Entity<String>{
    private String password, firstName, lastName;
    private final String saltValue;
    private LocalDate lastLoginAt;

    /**
     * parametrized constructor
     * @param password - String
     * @param saltValue - String
     * @param firstName - String
     * @param lastName - String
     * @param lastLoginAt - LocalDate
     */
    public User(String password, String saltValue, String firstName, String lastName, LocalDate lastLoginAt) {
        this.password = password;
        this.saltValue = saltValue;
        this.firstName = firstName;
        this.lastName = lastName;
        this.lastLoginAt = lastLoginAt;
    }

    /**
     * method that returns the first name of the User
     * @return - String
     */
    public String getFirstName(){return firstName;}

    /**
     * method that changes the firstName attribute with a new one
     * @param firstName - String
     */
    public void setFirstName(String firstName){
        this.firstName = firstName;
    }

    /**
     * method that changes the lastName attribute with a new one
     * @param lastName - String
     */
    public void setLastName(String lastName){
        this.lastName = lastName;
    }

    /**
     * method that returns the second name of the User
     * @return - String
     */
    public String getLastName(){return lastName;}

    /**
     * method that returns the password of the account of the User
     * @return - String
     */
    public String getPassword(){return password;}

    /**
     * method that changes the current password of the account with a new one
     */
    public void setPassword(String newPassword){
        this.password = newPassword;
    }

    /**
     * method that returns the salt value for a certain User
     * @return - String
     */
    public String getSaltValue(){
        return saltValue;
    }

    /**
     * method that returns the last login date of the User
     * @return - LocalDate
     */
    public LocalDate getLastLoginAt() {
        return lastLoginAt;
    }

    /**
     * method that changes the last login date of the User
     */
    public void setLastLoginAt(LocalDate lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
    }

    @Override
    public String toString(){
        return super.getId() + "->" + firstName + " " + lastName;
    }
}
