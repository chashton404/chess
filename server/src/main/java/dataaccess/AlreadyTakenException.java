package dataaccess;

/** 
 * Indicates that the user tried to create a value that already exists.
 * Used in this case for username and game color
*/

public class AlreadyTakenException extends Exception{
    public AlreadyTakenException(String message) {
        super(message);
    }
    public AlreadyTakenException(String message, Throwable ex) {
        super(message, ex);
    }
}
