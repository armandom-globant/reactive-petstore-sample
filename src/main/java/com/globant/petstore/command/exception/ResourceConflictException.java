package com.globant.petstore.command.exception;

public class ResourceConflictException extends RuntimeException {
    public ResourceConflictException() {
    }

    public ResourceConflictException(String message) {
        super(message);
    }
}
