package org.apache.coyote.http11.message.request;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import lombok.ToString;

@ToString
public class QueryString {

    // Query String 을 구분하기 위한 구분자(Separator) &
    private static final String QUERY_STRING_DELIMITER = "&";
    // key = value 구분자 "="
    private static final String KEY_VALUE_DELIMITER = "=";
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private final Map<String, String> query = new HashMap<>();

    public QueryString(final String queryString) {
        if (Objects.isNull(queryString) || queryString.isBlank()) {
            return;
        }
        parseQueryString(queryString);
    }

    private void parseQueryString(final String queryString) {
        for (String singleQuery : queryString.split(QUERY_STRING_DELIMITER)) {
            String[] splitQuery = singleQuery.split(KEY_VALUE_DELIMITER);
            String key = splitQuery[KEY_INDEX];
            String value = splitQuery[VALUE_INDEX];
            query.put(key, value);
        }
    }

    public Optional<String> getQuery(final String key) {
        String value = query.get(key);
        if (Objects.isNull(value)) {
            return Optional.empty();
        }

        return Optional.of(value);
    }
}