package tdd.micro.boot.exception;

/**
 * Exception thrown when there are no funds available in account.
 */
public class InsufficientFundsException extends Exception {

    public InsufficientFundsException(String m) {
        super(m);
    }
}
