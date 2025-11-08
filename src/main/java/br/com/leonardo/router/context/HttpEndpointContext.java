package br.com.leonardo.router.context;

import br.com.leonardo.http.HttpMethod;
import br.com.leonardo.http.RequestLine;
import br.com.leonardo.router.matcher.EndpointUriMatcher;
import br.com.leonardo.router.matcher.PathVariableUriMatcher;
import br.com.leonardo.router.matcher.QueryParameterUriMatcher;
import br.com.leonardo.router.matcher.UriMatcher;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class HttpEndpointContext {

    private final Map<HttpMethod, Set<HttpEndpoint<?, ?>>> endpointMap = new ConcurrentHashMap<>();
    private final UriMatcher endpointUriMatcher = new EndpointUriMatcher(
            List.of(
                    new PathVariableUriMatcher(),
                    new QueryParameterUriMatcher()
            )
    );

    public HttpEndpointContext add(HttpEndpoint<?, ?> endpoint) {
        endpointMap
                .computeIfAbsent(endpoint.getMethod(), k -> ConcurrentHashMap.newKeySet())
                .add(endpoint);
        return this;
    }

    public Optional<HttpEndpoint<?, ?>> get(RequestLine requestLine) {
        //TODO: validar ambiguidade
        return Optional.ofNullable(endpointMap.get(requestLine.method()))
                .flatMap(set ->
                        set
                                .stream()
                                .filter(handler ->
                                        endpointUriMatcher.match(handler.getUri(), requestLine.uri())
                                )
                                .findFirst()
                );
    }

}
