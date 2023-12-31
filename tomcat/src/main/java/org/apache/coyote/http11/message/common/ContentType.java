package org.apache.coyote.http11.message.common;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum ContentType {
    PLAINTEXT("", "text/plain"),
    HTML("html", "text/html"),
    CSS("css", "text/css"),
    JAVASCRIPT("javascript", "text/javascript"),
    FAVICON("ico", "image/x-icon");

    private final String extension;
    private final String value;

    ContentType(final String extension, final String value) {
        this.extension = extension;
        this.value = value;
    }

    public static ContentType from(final String extension) {
        return Arrays.stream(values())
                .filter(it -> it.getExtension().equals(extension))
                .findFirst()
                .orElse(PLAINTEXT);
    }

}