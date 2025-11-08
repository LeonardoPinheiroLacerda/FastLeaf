package br.com.leonardo.router.matcher;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PathVariableUriMatcher implements UriMatcher {

    private static final Pattern pathVariablePattern = Pattern.compile("^\\{(.*)}$");

    @Override
    public boolean match(String inputUri, String contextUri) {

        final String[] inputUriParts = inputUri.split("/");
        final String[] contextUriParts = contextUri.split("/");

        if (inputUriParts.length != contextUriParts.length) {
            return false;
        }

        for (int i = 0; i < inputUriParts.length; i++) {
            final String endpointPart = inputUriParts[i];
            final String requestPart = contextUriParts[i];

            final Matcher matcher = pathVariablePattern.matcher(endpointPart);
            if (!matcher.find() && !endpointPart.equals(requestPart)) {
                return false;
            }
        }

        return true;
    }

}
