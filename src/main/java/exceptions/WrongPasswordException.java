package exceptions;

/**
 * abstract class for wrong password exception type
 */
public class WrongPasswordException extends RuntimeException{
    /**
     * parametrized constructor of the class WrongPasswordException
     * @param message - error message(String)
     */
    public WrongPasswordException(String message){
        super(message);
    }
}
