package exception;

/**
 * This is the response that we will use for the server facade
 */

public class ResponseException extends Exception {
    private final int statusCode;

    public ResponseException(int statusCode, String message) {
        // super here calls the constructor of Exception (the parent class)
        super(message);
        this.statusCode = statusCode;
    }
    
    public int getStatusCode() {
        return statusCode;
    }
    
}
