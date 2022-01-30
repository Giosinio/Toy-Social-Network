package exceptions;

/**
 * exception class for the time a user is not found in our records
 */
public class UserNotFoundException extends RuntimeException{
    /**
     * parametrized constructor of the class UserNotFoundException
     * @param message - error message(String)
     */
    public UserNotFoundException(String message){
        super(message);
    }
}
