package com.team3.forum.exceptions;

public class EntityUpdateConflictException extends RuntimeException {

    public EntityUpdateConflictException(String message) {
        super(message);
    }
}