package org.apache.coyote.http11.exception;

public class DuplicateHeaderException extends RuntimeException {

    public DuplicateHeaderException() {
        super("Duplicate Header is prohibited.");
    }
}