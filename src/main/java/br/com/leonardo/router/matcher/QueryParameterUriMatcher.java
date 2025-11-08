package br.com.leonardo.router.matcher;

public class QueryParameterUriMatcher implements UriMatcher {

    @Override
    public boolean match(String inputUri, String contextUri) {
        final String[] contextUriParts = contextUri.split("\\?");
        return contextUriParts[0].equals(inputUri);
    }

}
