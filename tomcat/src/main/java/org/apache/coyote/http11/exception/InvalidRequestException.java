package org.apache.coyote.http11.exception;

public class InvalidRequestException extends RuntimeException{
    public InvalidRequestException() {
        super(" Invalid Request");
    }
}
