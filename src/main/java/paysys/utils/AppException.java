package paysys.utils;

/**
 * Exception for business logic
 */
public class AppException extends RuntimeException {

    /**
     * Constructs a new exception by message
     *
     * @param message Error message
     */
    public AppException(String message) {
        super(message);
    }
    
    @Override
    public Throwable fillInStackTrace() {
        return this;
    }
}
