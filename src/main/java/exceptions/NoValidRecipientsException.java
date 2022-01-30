package exceptions;

public class NoValidRecipientsException extends RuntimeException{
    public NoValidRecipientsException(String message){
        super(message);
    }
}
