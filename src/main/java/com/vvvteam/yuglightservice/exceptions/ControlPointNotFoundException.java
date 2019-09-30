package com.vvvteam.yuglightservice.exceptions;

public class ControlPointNotFoundException extends RuntimeException {
    public ControlPointNotFoundException(String message) {
        super(message);
    }

    public ControlPointNotFoundException() {
        super();
    }
}
