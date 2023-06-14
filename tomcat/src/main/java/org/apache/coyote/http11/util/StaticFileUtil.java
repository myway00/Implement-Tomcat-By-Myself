package org.apache.coyote.http11.util;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
public class StaticFileUtil {

    private static final String STATIC_DIRECTORY = "static";

    public static String readFile(final String path) {
        ClassLoader classLoader = StaticFileUtil.class.getClassLoader();
        URL resource = classLoader.getResource(STATIC_DIRECTORY + path);
        if (Objects.isNull(resource)) {
            throw new RuntimeException(path);
        }

        try {
            return Files.readString(Path.of(resource.getPath()));
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }
}