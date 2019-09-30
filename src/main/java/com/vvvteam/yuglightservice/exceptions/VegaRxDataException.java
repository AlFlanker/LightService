package com.vvvteam.yuglightservice.exceptions;

public class VegaRxDataException extends RuntimeException {
    /**
     * Constructs a new runtime exception with the specified detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     *
     * @param message the detail message. The detail message is saved for
     *                later retrieval by the {@link #getMessage()} method.
     */
    public VegaRxDataException(String message) {
        super(message);
    }
}
