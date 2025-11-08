package br.com.leonardo.router.matcher;

import java.util.List;

public record EndpointUriMatcher(
        List<UriMatcher> uriMatchers
) implements UriMatcher {

    @Override
    public boolean match(String inputUri, String contextUri) {
        final long count = uriMatchers
                .stream()
                .filter(matcher -> matcher.match(inputUri, contextUri))
                .count();

        return count >= 1;
    }

}
