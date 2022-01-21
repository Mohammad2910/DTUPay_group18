package exceptions;

/**
 * @author David
 */
public class CustomerAlreadyExistsException extends Exception {
    public CustomerAlreadyExistsException(String message) {
        super(message);
    }
}
