package exception;

/**
 * Thrown when there is an external issue loading/writing data
 */
public class PersistenceException extends RuntimeException {
    public PersistenceException(String message) {
        super(message);
    }
    public PersistenceException(String message, Throwable cause) {
        super(message, cause);
    }
}
