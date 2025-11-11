package com.team3.forum.exceptions;

public class DuplicateEntityException extends RuntimeException {
    public DuplicateEntityException(String message) {
        super(message);
    }

    public DuplicateEntityException(String type, int id) {
        this(type, "id", String.valueOf(id));
    }

    public DuplicateEntityException(String entity, String attribute, String value) {
        this(String.format("%s with %s %s already exists.", entity, attribute, value));
    }
}
