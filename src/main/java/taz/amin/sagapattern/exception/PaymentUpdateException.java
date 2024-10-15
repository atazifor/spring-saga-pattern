package taz.amin.sagapattern.exception;

public class PaymentUpdateException extends RuntimeException{
    public PaymentUpdateException() {}

    public PaymentUpdateException(String message) {
        super(message);
    }

    public PaymentUpdateException(String message, Throwable cause) {
        super(message, cause);
    }

    public PaymentUpdateException(Throwable cause) {
        super(cause);
    }
}
