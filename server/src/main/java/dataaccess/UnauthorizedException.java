package dataaccess;

/**
 * Indicates that the user tried to perform an action in which a authToken is required but the
 * authToken was invalid or nonexistent
 */

public class UnauthorizedException extends Exception{
    public UnauthorizedException(String message) {
        // super here calls the constructor of Exception (the parent class)
        super(message);
    }
    public UnauthorizedException(String message, Throwable ex) {
        super(message, ex);
    }
    
}