package exceptions;

public class NotificationException extends RuntimeException{
    public NotificationException(String errors){
        super(errors);
    }
}
