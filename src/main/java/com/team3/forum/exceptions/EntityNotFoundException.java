package com.team3.forum.exceptions;

public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(String message) {
        super(message);
    }

    public EntityNotFoundException(String entity, String attribute, String value) {
        super(String.format("%s with %s %s not found.", entity, attribute, value));
    }
}
