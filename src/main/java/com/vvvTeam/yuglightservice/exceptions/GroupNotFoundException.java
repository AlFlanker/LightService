package com.vvvTeam.yuglightservice.exceptions;

public class GroupNotFoundException extends RuntimeException {
    public GroupNotFoundException(String message) {
        super(message);
    }

    public GroupNotFoundException() {
        super();
    }
}
