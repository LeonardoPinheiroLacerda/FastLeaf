package br.com.leonardo.http.request;

import br.com.leonardo.http.RequestLine;
import br.com.leonardo.http.request.map.HeaderMap;
import br.com.leonardo.http.request.map.PathVariableMap;
import br.com.leonardo.http.request.map.QueryParameterMap;

public record HttpRequest<I>(
        RequestLine requestLine,
        HeaderMap headers,
        I body,
        PathVariableMap pathVariables,
        QueryParameterMap queryParameters
) {
    public String uri() {
        return requestLine.uri().split("\\?")[0];
    }
}
