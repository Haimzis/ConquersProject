package Exceptions;

public class invalidInputException extends Exception {
    public invalidInputException() { super(); }

    public invalidInputException(String message) { super(message); }

    public invalidInputException(String message, Throwable cause) { super(message, cause); }

    public invalidInputException(Throwable cause) { super(cause); }
}
