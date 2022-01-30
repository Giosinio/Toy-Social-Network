package repository;

import model.User;

import java.time.LocalDate;

/**
 * Message Repository interface
 */
public interface UserRepository extends Repository<String, User>{
    /**
     * updates the last login date of the User
     * @param username String (ID of a User entity)
     * @param newLastLoginAt LocalDate
     */
    public void updateLastLoginAt(String username, LocalDate newLastLoginAt);
}
