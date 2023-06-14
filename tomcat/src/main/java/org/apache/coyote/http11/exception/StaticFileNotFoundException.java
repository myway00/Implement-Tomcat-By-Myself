package org.apache.coyote.http11.exception;

public class StaticFileNotFoundException extends RuntimeException {

    public StaticFileNotFoundException(final String path) {
        super( "No file exists in " + path );
    }
}