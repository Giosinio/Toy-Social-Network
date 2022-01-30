package exceptions;

/**
 * exception class for issues encountered with FriendshipRequestException
 */

public class FriendshipRequestException extends RuntimeException {

    /**
     * constructor for FriendshipRequestException
     * @param message a String representing the exception's message
     */
    public FriendshipRequestException(String message){
        super(message);
    }
}
