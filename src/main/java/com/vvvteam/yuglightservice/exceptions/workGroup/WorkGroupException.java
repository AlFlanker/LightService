package com.vvvteam.yuglightservice.exceptions.workGroup;

public class WorkGroupException extends RuntimeException {
    /**
     * Constructs a new runtime exception with the specified detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     *
     * @param message the detail message. The detail message is saved for
     *                later retrieval by the {@link #getMessage()} method.
     */
    public WorkGroupException(String message) {
        super(message);
    }
}
