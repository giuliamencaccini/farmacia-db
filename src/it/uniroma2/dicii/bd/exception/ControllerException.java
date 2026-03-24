package it.uniroma2.dicii.bd.exception;

public class ControllerException extends ApplicationException {

    public ControllerException() {
        super();
    }

    public ControllerException(String message, Throwable cause) {
        super(message, cause);
    }

    public ControllerException(String message) {
        super(message);
    }
}