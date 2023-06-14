package org.apache.coyote.http11.exception;

public class ReadFileException extends RuntimeException {

    public ReadFileException() {
        super("Error while reading a file.");
    }
}