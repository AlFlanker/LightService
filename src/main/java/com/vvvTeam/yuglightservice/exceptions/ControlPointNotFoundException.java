package com.vvvTeam.yuglightservice.exceptions;

public class ControlPointNotFoundException extends RuntimeException {
    public ControlPointNotFoundException(String message) {
        super(message);
    }

    public ControlPointNotFoundException() {
        super();
    }
}
