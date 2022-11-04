package com.isfett.workflow.exception;

public class NoPlacesDefinedException extends RuntimeException {
    public NoPlacesDefinedException() {
        super("It seems you forgot to add places to the current workflow.");
    }
}
