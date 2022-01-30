package validators;

/**
 * Exception class for validation
 */
public class ValidationException extends RuntimeException {
    /**
     * empty class constructor
     */
    public ValidationException() {
    }

    /**
     * class constructor
     * @param message - message error
     */
    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ValidationException(Throwable cause) {
        super(cause);
    }

    public ValidationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
